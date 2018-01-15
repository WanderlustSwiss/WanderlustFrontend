package eu.wise_iot.wanderlust.models.DatabaseObject;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour_;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.UserTourService;
import io.objectbox.Box;
import io.objectbox.Property;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static eu.wise_iot.wanderlust.models.DatabaseModel.Trip_.userTour;

/**
 * TripDao:
 *
 * @author Rilind Gashi, Alexander Weinbeck
 * @license MIT
 */


public class UserTourDao extends DatabaseObjectAbstract {
    private static UserTourService service;
    Property columnProperty;
    private Box<UserTour> routeBox;

    /**
     * Constructor.
     */

    public UserTourDao() {
        routeBox = DatabaseController.boxStore.boxFor(UserTour.class);
        if (service == null) service = ServiceGenerator.createService(UserTourService.class);
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
    public UserTour update(UserTour usertour) {
        routeBox.put(usertour);
        return usertour;
    }

    /**
     * insert a usertour local and remote
     * @param usertour
     * @param handler
     */
//    public void create(final AbstractModel usertour, final FragmentHandler handler){
//        Call<UserTour> call = service.createUserTour((UserTour)usertour);
//        call.enqueue(new Callback<UserTour>() {
//            @Override
//            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
//                if(response.isSuccessful()){
//                    routeBox.put((UserTour)usertour);
//                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()),response.body()));
//                } else {
//                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), null));
//                }
//            }
//            @Override
//            public void onFailure(Call<UserTour> call, Throwable t) {
//                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR,null));
//            }
//        });
//    }

    /**
     * get usertour out of the remote database by entity
     *
     * @param id
     * @param handler
     */
    public void retrieve(int id, final FragmentHandler handler) {
        Call<UserTour> call = service.retrieveUserTour(id);
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if (response.isSuccessful()) {
                    //try {
                        //UserTour internalPoi = findOne(UserTour_.tour_id, id);
                        UserTour backendTour = response.body();
                        routeBox.put(backendTour);
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendTour));
                    //}
                    /*catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }*/
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
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
        Call<UserTour> call = service.updateUserTour(id, (UserTour) usertour);
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if (response.isSuccessful()) {
                    routeBox.put((UserTour) usertour);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
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
        Call<UserTour> call = service.deleteUserTour((UserTour) usertour);
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if (response.isSuccessful()) {
                    routeBox.remove((UserTour) usertour);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * get all usertours out of the remote database
     *
     * @param handler
     */
    public void retrieveAll(final FragmentHandler handler) {
        Call<List<UserTour>> call = service.retrieveAllUserTours();
        call.enqueue(new Callback<List<UserTour>>() {
            @Override
            public void onResponse(Call<List<UserTour>> call, Response<List<UserTour>> response) {
                if (response.isSuccessful())
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<List<UserTour>> call, Throwable t) {
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
            UserTourService service = ServiceGenerator.createService(UserTourService.class);
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
     * @param userTourId
     * @param imageId
     * @return true if everything went ok
     */
    private boolean writeToDisk(ResponseBody body, long userTourId, long imageId) {

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            byte[] fileReader = new byte[4096]; //Giele machet ned zu krassi bilder suscht bricht das zäme
            //if pictures get too large, need to implement a stream:
            // https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server

            long fileSizeDownloaded = 0;

            String name = userTourId + "-" + imageId + ".jpg";
            inputStream = body.byteStream();
            outputStream = DatabaseController.mainContext.openFileOutput(name, Context.MODE_PRIVATE);


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
    public List<UserTour> find() {
        if (routeBox != null)
            return routeBox.getAll();
        else
            return null;
    }

    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return UserTour which match to the search pattern in the searched columns
     */
    public UserTour findOne(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return routeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public UserTour findOne(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return routeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for routes matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<UserTour> which contains the equipements, which match to the search pattern in the searched columns
     */
    public List<UserTour> find(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<UserTour> find(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<UserTour> find(Property searchedColumn, boolean searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        routeBox.remove(findOne(searchedColumn, searchPattern));
    }

    /**
     * delete:
     * Deleting a UserTour which matches the given pattern
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
}
