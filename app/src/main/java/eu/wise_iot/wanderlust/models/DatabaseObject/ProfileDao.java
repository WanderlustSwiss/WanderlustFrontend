package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.services.ProfileService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ProfileDao
 *
 * @author Rilind Gashi
 * @license MIT
 */

public class ProfileDao extends DatabaseObjectAbstract {

    private static class Holder {
        private static final ProfileDao INSTANCE = new ProfileDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static ProfileDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private static ProfileService service;
    private final Box<Profile> profileBox;
    private final ImageController imageController;

    /**
     *
     */

    private ProfileDao() {
        profileBox = BOXSTORE.boxFor(Profile.class);
        service = ServiceGenerator.createService(ProfileService.class);
        imageController = ImageController.getInstance();
    }


    public long count() {
        return profileBox.count();
    }

    /**
     * count all profile which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, String searchPattern) {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * count all profile which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, long searchPattern) {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * Update an existing user in the database and sync to backend
     *
     * @param profile (required).
     * @param handler
     */
    public void update(Profile profile, final FragmentHandler handler) {

        //TODO remmove when birthday is implemented
        profile.setBirthday("0");

        Call<Profile> call = service.updateProfile(profile);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful()) {
                    Profile backendProfile = response.body();
                    Profile internalProfile = getProfile();
                    backendProfile.setInternal_id(internalProfile.getInternal_id());
                    if (backendProfile.getImagePath() != null) {
                        backendProfile.getImagePath().setLocalDir(imageController.getProfileFolder());
                    }
                    profileBox.put(backendProfile);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendProfile));
                }else{
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }
    /**
     * Update an existing user in the database
     *
     * @param profile (required).
     */
    public void update(Profile profile){
        profileBox.put(profile);
    }

    public Profile getProfile() {
        List<Profile> profiles = find();

        if (!profiles.isEmpty()){
            return profiles.get(0);
        } else {
            return null;
        }
    }

    /**
     * add an image to the db
     *
     * @param file
     * @param profile
     */
    public void addImage(final File file, final Profile profile, final FragmentHandler handler) {
        ProfileService service = ServiceGenerator.createService(ProfileService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        Call<ImageInfo> call = service.uploadImage(body);
        call.enqueue(new Callback<ImageInfo>() {
            @Override
            public void onResponse(Call<ImageInfo> call, Response<ImageInfo> response) {
                if (response.isSuccessful()) {
                    try {
                            ImageInfo imagePath = response.body();
                            Profile internalProfile = getProfile();
                            internalProfile.setImagePath(imagePath);
                            internalProfile.getImagePath().setLocalDir(imageController.getProfileFolder());
                            imageController.save(file, internalProfile.getImagePath());
                            profileBox.put(internalProfile);
                            file.delete();
                            handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), internalProfile));
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
    }

    /**
     * deletes an image from a specific profile from the database
     * and return it in the event
     *
     * @param handler
     */
    public void deleteImage(final FragmentHandler handler) {
        Call<ImageInfo> call = service.deleteImage();
        call.enqueue(new Callback<ImageInfo>() {
            @Override
            public void onResponse(Call<ImageInfo> call, Response<ImageInfo> response) {
                if (response.isSuccessful()) {
                    Profile profile = getProfile();
                    imageController.delete(profile.getImagePath());
                    profile.setImagePath(null);
                    profileBox.put(profile);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), profile));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<ImageInfo> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }


    /**
     * Insert a profile into the database.
     *
     * @param profile (required).
     */
    public void create(Profile profile) {
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
     * @param searchPattern  (required) contain the search pattern.
     * @return Profile who match to the search pattern in the searched columns
     */
    @SuppressWarnings("WeakerAccess")
    public Profile findOne(Property searchedColumn, String searchPattern) {
        return profileBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Profile findOne(Property searchedColumn, long searchPattern) {
        return profileBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for profile matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Profile> which contains the users, who match to the search pattern in the searched columns
     */
    @SuppressWarnings("WeakerAccess")
    public List<Profile> find(Property searchedColumn, String searchPattern) {
        return profileBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    @SuppressWarnings("WeakerAccess")
    public List<Profile> find(Property searchedColumn, long searchPattern) {
        return profileBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Profile> find(Property searchedColumn, boolean searchPattern) {
        return profileBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) {
        profileBox.remove(findOne(searchedColumn, searchPattern));

    }

    /**
     * Delete all profiles out of the database
     */
    public void removeAll() {
        profileBox.removeAll();
    }
}
