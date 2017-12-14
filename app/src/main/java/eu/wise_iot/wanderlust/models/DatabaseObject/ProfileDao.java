package eu.wise_iot.wanderlust.models.DatabaseObject;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile_;
import eu.wise_iot.wanderlust.services.ProfileService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.Property;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ProfileDao
 * @author Rilind Gashi
 * @license MIT
 */

public class ProfileDao extends DatabaseObjectAbstract {
    public Box<Profile> profileBox;
    public static ProfileService service;

    Property columnProperty;

    /**
     *
     */

    public ProfileDao(){
        profileBox = DatabaseController.boxStore.boxFor(Profile.class);
        service = ServiceGenerator.createService(ProfileService.class);
    }


    public long count(){
        return profileBox.count();
    }

    /**
     * count all profile which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * count all profile which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param profile (required).
     *
     */
    public void update(Profile profile, final FragmentHandler handler){
        Call<Profile> call = service.updateProfile(profile.getProfile_id(), profile);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){
                    try {
                        Profile backendProfile = response.body();
                        Profile internalProfile = findOne(Profile_.profile_id, profile.getProfile_id());
                        backendProfile.setInternal_id(internalProfile.getInternal_id());
                        backendProfile.setImageId(internalProfile.getImageId());
                        profileBox.put(backendProfile);
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendProfile));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * delete a profile in the database
     *
     * @param profile
     * @param handler
     */
    //TODO: is copied from Tobis work from PoiDao and it doesn't work. Update it if Tobi has fixed the bug
    public void delete(final Profile profile, final FragmentHandler handler) {
        Call<Profile> call = service.deleteProfile(profile);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful()) {
                    profileBox.remove(profile);
                    //TODO delete image
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * add an image to the db
     *
     * @param file
     * @param profile
     */
    public void addImage(final File file, final Profile profile, final FragmentHandler handler) {
        ProfileService service = ServiceGenerator.createService(ProfileService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        Call<Profile.ImageInfo> call = service.uploadImage(profile.getProfile_id(), body);
        call.enqueue(new Callback<Profile.ImageInfo>() {
            @Override
            public void onResponse(Call<Profile.ImageInfo> call, Response<Profile.ImageInfo> response) {
                if (response.isSuccessful()) {
                    try {
                        Profile internalProfile = findOne(Profile_.profile_id, profile.getProfile_id());
                        Profile.ImageInfo imageInfo = response.body();
                        String name = profile.getProfile_id() + "-" + imageInfo.getId();
                        saveImageOnApp(name, file);
                        internalProfile.setImageId((byte) imageInfo.getId());
                        profileBox.put(internalProfile);
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), internalProfile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Profile.ImageInfo> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void saveImageOnApp(String name, File file) throws IOException {

        InputStream in = new FileInputStream(file);
        FileOutputStream out = DatabaseController.mainContext.openFileOutput(name, Context.MODE_PRIVATE);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
            }

    /**
     * deletes an image from a specific profile from the database
     * and return it in the event
     *
     * @param profileID
     * @param imageID
     * @param handler
     */
    public void deleteImage(final long profileID, final long imageID, final FragmentHandler handler) {
        Call<Profile.ImageInfo> call = service.deleteImage(profileID, imageID);
        call.enqueue(new Callback<Profile.ImageInfo>() {
            @Override
            public void onResponse(Call<Profile.ImageInfo> call, Response<Profile.ImageInfo> response) {
                if (response.isSuccessful()) {
                    try {
                        Profile internalProfile = findOne(Profile_.profile_id, profileID);
                        if (!internalProfile.removeImageId((byte)response.body().getId())){
                            //TODO image id not found
                        }
                        profileBox.put(internalProfile);
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), internalProfile));
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
            public void onFailure(Call<Profile.ImageInfo> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }
    
    
                }
            }

            @Override
            public void onFailure(Call<Profile.ImageInfo> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void saveImageOnApp(String name, File file) throws IOException {

        InputStream in = new FileInputStream(file);
        FileOutputStream out = DatabaseController.mainContext.openFileOutput(name, Context.MODE_PRIVATE);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }

    /**
     * deletes an image from a specific profile from the database
     * and return it in the event
     *
     * @param profileID
     * @param imageID
     * @param handler
     */
    public void deleteImage(final long profileID, final long imageID, final FragmentHandler handler) {
        Call<Profile.ImageInfo> call = service.deleteImage(profileID, imageID);
        call.enqueue(new Callback<Profile.ImageInfo>() {
            @Override
            public void onResponse(Call<Profile.ImageInfo> call, Response<Profile.ImageInfo> response) {
                if (response.isSuccessful()) {
                    try {
                        Profile internalProfile = findOne(Profile_.profile_id, profileID);
                        if (!internalProfile.removeImageId((byte)response.body().getId())){
                            //TODO image id not found
                        }
                        profileBox.put(internalProfile);
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), internalProfile));
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
            public void onFailure(Call<Profile.ImageInfo> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }
    
    
    /**
     * Insert a profile into the database.
     *
     * @param profile (required).
     *
     */
    public void create(Profile profile){
        profileBox.put(profile);
    }

    /**
     * Return a list with all profiles
     *
     * @return List<Profile>
     */
    public List<Profile> find() {
        return profileBox.getAll();
    }

    /**
     * Searching for a single profile with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Profile who match to the search pattern in the searched columns
     */
    public Profile findOne(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return profileBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Profile findOne(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return profileBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for profile matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Profile> which contains the users, who match to the search pattern in the searched columns
     */
    public List<Profile> find(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return profileBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Profile> find(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return profileBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Profile> find(Property searchedColumn, boolean searchPattern) {
        return profileBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        profileBox.remove(findOne(searchedColumn, searchPattern));

    }

}
