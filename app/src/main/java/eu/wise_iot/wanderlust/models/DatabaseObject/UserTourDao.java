package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.TourKitEquipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour_;
import eu.wise_iot.wanderlust.services.PoiService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.TourService;
import eu.wise_iot.wanderlust.views.FilterFragment;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TripDao:
 *
 * @author Rilind Gashi, Alexander Weinbeck
 * @license MIT
 */


public class UserTourDao extends DatabaseObjectAbstract {

    private static class Holder {
        private static final UserTourDao INSTANCE = new UserTourDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static UserTourDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private static TourService service;
    private final Box<Tour> routeBox;
    private final ImageController imageController;

    /**
     * Constructor.
     */

    private UserTourDao() {
        routeBox = BOXSTORE.boxFor(Tour.class);
        service = ServiceGenerator.createService(TourService.class);
        imageController = ImageController.getInstance();
    }

    public long count() {
        return routeBox.count();
    }

    public long count(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * count all tours which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * Update an existing usertour in the database.
     *
     * @param usertour (required).
     */
    public Tour update(Tour usertour) {
        routeBox.put(usertour);
        return usertour;
    }

    /**
     * insert a usertour local
     * @param userTour
     * @param handler
     */
    public void create(final AbstractModel userTour, final FragmentHandler handler){
        routeBox.put((Tour) userTour);
        handler.onResponse(new ControllerEvent(EventType.OK));
    }

    /**
     * get usertour out of the remote database by entity
     *
     * @param id
     * @param handler
     */
    public void retrieve(final long id, final FragmentHandler handler) {
        final long[] newUserTourID = new long[1];
        Call<Tour> call = service.retrieveTour(id);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if (response.isSuccessful()) {
                        Tour backendTour = response.body();
                        //routeBox.put(backendTour); wieso in die lokale db einfügen ??
                        newUserTourID[0] = backendTour.getInternal_id();
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendTour));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /*
    public void retrieve(long id, final FragmentHandler handler) {
        Call<Poi> call = service.retrievePoi(id);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if (response.isSuccessful()) {
                    try {
                        Poi internalPoi = findOne(Poi_.poi_id, id);
                        Poi backendPoi = response.body();
                        if (response.body().isPublic()) {
                            for (Poi.ImageInfo imageInfo : backendPoi.getImagePaths()) {
                                //count will be increases automatically
                                backendPoi.addImageId((byte) imageInfo.getId());
                            }
                            backendPoi.setInternal_id(0);
                        } else {
                            //imagepaths will always be empty
                            backendPoi.setInternal_id(internalPoi.getInternal_id());
                            backendPoi.setImageIds(internalPoi.getImageIds(), internalPoi.getImageCount());
                        }
                        poiBox.put(backendPoi);
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendPoi));
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }
     */

    /**
     * update a usertour
     *
     * @param id
     * @param usertour
     * @param handler
     */
    public void update(int id, final AbstractModel usertour, final FragmentHandler handler) {
        Call<Tour> call = service.updateTour(id, (Tour) usertour);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if (response.isSuccessful()) {
                    routeBox.put((Tour) usertour);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * delete a usertour local and remote
     *
     * @param usertour
     * @param handler
     */
    public void delete(final AbstractModel usertour, final FragmentHandler handler) {
        Tour tour = (Tour) usertour;
        long id = tour.getTour_id();
        Call<Tour> call = service.deleteTour(id);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if (response.isSuccessful()) {
                    routeBox.remove((Tour) usertour);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }
            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * get all usertours out of the remote database
     *
     * @param handler
     */
    public void retrieveAll(final FragmentHandler handler, int page) {
        Call<List<Tour>> call = service.retrieveAllTours(page);
        call.enqueue(new Callback<List<Tour>>() {
            @Override
            public void onResponse(Call<List<Tour>> call, Response<List<Tour>> response) {
                if (response.isSuccessful()) {
                    List<Tour> tours = response.body();
                    for (Tour tour : tours) {
                        for (ImageInfo imageInfo : tour.getImagePaths()) {
                            String name = tour.getTour_id() + "-" + imageInfo.getId() + ".jpg";
                            imageInfo.setName(name);
                            imageInfo.setId(tour.getTour_id());
                            imageInfo.setLocalDir(imageController.getTourFolder());
                        }
                    }
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                }
                else{
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<List<Tour>> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }
    /**
     * get all usertours out of the remote database by filter
     *
     * @param handler
     */
    public void retrieveAllFiltered(final FragmentHandler handler, int page, int distanceS, int distanceE, int durationS, int durationE, String regionIDs, String title, String difficulties) {
        Call<List<Tour>> call = service.retrieveAllFilteredTours(page, distanceS, distanceE, durationS, durationE, regionIDs, title, difficulties);
        call.enqueue(new Callback<List<Tour>>() {
            @Override
            public void onResponse(Call<List<Tour>> call, Response<List<Tour>> response) {
                if (response.isSuccessful()) {
                    List<Tour> tours = response.body();
                    for (Tour tour : tours) {
                        for (ImageInfo imageInfo : tour.getImagePaths()) {
                            String name = tour.getTour_id() + "-" + imageInfo.getId() + ".jpg";
                            imageInfo.setName(name);
                            imageInfo.setId(tour.getTour_id());
                            imageInfo.setLocalDir(imageController.getTourFolder());
                        }
                    }
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                }
                else{
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<List<Tour>> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * add an image to the db
     *
     * @param image_id
     * @param tour_id
     */
    public void downloadImage(final long tour_id, final int image_id, final FragmentHandler handler) {

            //Upload image to backend
            TourService service = ServiceGenerator.createService(TourService.class);
            Call<ResponseBody> call = service.downloadImage(tour_id,image_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {


                        //String name = tour_id + "-" + image_id + ".jpg";

                        //save image on the disk with id of tour linked
                        writeToDisk(response.body(), tour_id, image_id);

//                      internalPoi.addImageId((byte) imageInfo.getId());
//                      poiBox.put(internalPoi);
//                      handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), internalPoi));
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                        call.cancel();

                    } else {
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
                }
            });

    }

    /**
     * Writes a downloaded image tour to the disk and names it correctly
     *
     * @param body    which represents the image downloaded
     * @param tourId
     * @param imageId
     * @return true if everything went ok
     */
    private boolean writeToDisk(ResponseBody body, long tourId, long imageId) {

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            byte[] fileReader = new byte[4096]; //Giele machet ned zu krassi bilder suscht bricht das zäme
            //if pictures get too large, need to implement a stream:
            // https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server

            long fileSizeDownloaded = 0;

            String name = tourId + "-" + imageId + ".jpg";
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(imageController.getPicturesDir() + "/tours/" + tourId + "-" + imageId + ".jpg");

            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0, read);

                fileSizeDownloaded += read;

            }

            outputStream.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();

                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     */
    public List<Tour> find() {
        if (routeBox != null)
            return routeBox.getAll();
        else
            return null;
    }

    public void getExtraEquipment(long id, FragmentHandler handler){
        Call<List<TourKitEquipment>> call = service.retrieveExtraEquipment(id);
        call.enqueue(new Callback<List<TourKitEquipment>>() {
            @Override
            public void onResponse(Call<List<TourKitEquipment>> call, Response<List<TourKitEquipment>> response) {
                if(response.isSuccessful()){
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<List<TourKitEquipment>> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return Tour which match to the search pattern in the searched columns
     */
    public Tour findOne(Property searchedColumn, String searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Tour findOne(Property searchedColumn, long searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for routes matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Tour> which contains the equipements, which match to the search pattern in the searched columns
     */
    public List<Tour> find(Property searchedColumn, String searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Tour> find(Property searchedColumn, long searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Tour> find(Property searchedColumn, boolean searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) {
        routeBox.remove(findOne(searchedColumn, searchPattern));
    }

    /**
     * delete:
     * Deleting a Tour which matches the given pattern
     *
     * @param searchedColumn
     * @param searchPattern
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void deleteByPattern(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        routeBox.remove(findOne(searchedColumn, searchPattern));
    }


    /**
     * add an image to the db
     *
     * @param file the File to be uploaded
     * @param tour
     */
    public void uploadImage(final File file, final Tour tour, final FragmentHandler handler) {
        try {
            if (tour.isPublic()) {
                //Upload image to backend
                TourService service = ServiceGenerator.createService(TourService.class);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                Call<ImageInfo> call = service.uploadImage((int) tour.getTour_id(), body);
                call.enqueue(new Callback<ImageInfo>() {
                    @Override
                    public void onResponse(Call<ImageInfo> call, Response<ImageInfo> response) {
                        if (response.isSuccessful()) {
                            try {
                                Tour currentTour = findOne(Tour_.internal_id, tour.getInternal_id());
                                ImageInfo imageInfo = response.body();
                                imageInfo.setName(tour.getTour_id() + "-" + imageInfo.getId() + ".jpg");
                                imageInfo.setLocalDir(imageController.getTourFolder());
                                imageController.save(file, imageInfo);
                                currentTour.addImagePath(imageInfo);
                                routeBox.put(currentTour);
                                handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), currentTour));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageInfo> call, Throwable t) {
                        handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
                    }
                });
            } else {
                Tour currentTour = findOne(Poi_.poi_id, tour.getTour_id());
                int newId = currentTour.getImageCount() + 1;
                String name = currentTour.getTour_id() + "-" + newId + ".jpg";
                ImageInfo newImage = new ImageInfo(newId, name, imageController.getTourFolder());
                imageController.save(file, newImage);
                currentTour.addImagePath(newImage);
                routeBox.put(currentTour);
                handler.onResponse(new ControllerEvent(EventType.OK, currentTour));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
