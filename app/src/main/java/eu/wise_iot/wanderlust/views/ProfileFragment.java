package eu.wise_iot.wanderlust.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.osmdroid.bonuspack.location.POI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.MapCacheHandler;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.controllers.ProfileController;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.controllers.TourOverviewController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.SavedTour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.views.adapters.ProfileFavoritesRVAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfilePOIRVAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfileSavedRVAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfileTripRVAdapter;
import eu.wise_iot.wanderlust.views.animations.CircleTransform;
import eu.wise_iot.wanderlust.views.dialog.ConfirmDeletePoiDialog;
import eu.wise_iot.wanderlust.views.dialog.PoiEditDialog;
import eu.wise_iot.wanderlust.views.dialog.PoiViewDialog;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;
import static android.os.Process.setThreadPriority;
import static java.lang.Process.*;

/**
 * Fragment which represents the UI of the profile of a user.
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView profilePicture;
    private Button editProfile;

    private TextView amountPOI;
    private TextView amountScore;
    private TextView amountTours;
    private TextView nickname;
    private TextView tvProfileNoContent;

    private TabLayout tabLayout;

    private RecyclerView profileRV;
    //private List list;
    private final List listSaved = new ArrayList();
    private final List listTrips = new ArrayList();
    private final List listFavorites = new ArrayList();
    private final List listPOIs = new ArrayList();

    private ProfileFavoritesRVAdapter profileFavoritesRVAdapter;
    private ProfileTripRVAdapter profileTripRVAdapter;
    private ProfileSavedRVAdapter profileSavedRVAdapter;
    private ProfilePOIRVAdapter profilePOIRVAdapter;

    private final ProfileController profileController;
    private final PoiController poiController;

    public ProfileFragment() {
        // Required empty public constructor
        profileController = new ProfileController();
        poiController = new PoiController();
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //inflate view from xml-file
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setupRVs(view);
        setupTabs(view);
        setupProfileInfo(view);
        setProfileStats();

        //load default list
        tabLayout.getTabAt(0).select();

        return view;
    }

    /**
     * Sets up profile picture, birthdate, amount of poi's, amount of tours and the score
     * in the profile UI. Additionally, the nickname of user is set in the toolbar.
     *
     * @param view in which the data is set
     */
    private void setupProfileInfo(View view) {
        //initializing views
        //birthday = (TextView) view.findViewById(R.id.profileBirthDay);
        amountPOI = (TextView) view.findViewById(R.id.profileAmountPOI);
        amountTours = (TextView) view.findViewById(R.id.profileAmountTours);
        amountScore = (TextView) view.findViewById(R.id.profileAmountScore);
        nickname = (TextView) view.findViewById(R.id.profileNickname);
        profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
        editProfile = (Button) view.findViewById(R.id.editProfileButton);

        File image = profileController.getProfilePicture();
        if (image != null) {
            Picasso.with(getActivity()).load(image).transform(new CircleTransform()).fit().placeholder(R.drawable.progress_animation).into(profilePicture);
            ((MainActivity) getActivity()).updateProfileImage(profileController.getProfilePicture());
        }else{
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            drawable.setCircular(true);
            profilePicture.setImageDrawable(drawable);
            ((MainActivity) getActivity()).updateProfileImage(profileController.getProfilePicture());
        }

        //edit profile button_white
        editProfile.setOnClickListener(v -> {

            Fragment profileEditFragment = getFragmentManager().findFragmentByTag(Constants.PROFILE_EDIT_FRAGMENT);
            if (profileEditFragment == null) profileEditFragment = ProfileEditFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, profileEditFragment, Constants.PROFILE_EDIT_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        });
    }

    /**
     * To set the stats at the top of the profile
     */
    public void setProfileStats(){
        nickname.setText(profileController.getNickName());
        profileController.getScore(controllerEvent -> {
            switch (controllerEvent.getType()){
                case OK:
                    int score = ((Profile) controllerEvent.getModel()).getScore();
                    amountScore.setText(String.format(Locale.GERMAN,"%1d" ,score));
                    if (BuildConfig.DEBUG) Log.d("SCORE",  String.valueOf(score));
                    break;
                default:
                    if (BuildConfig.DEBUG) Log.d("SCORE",  "Could not load Score");
                    break;
            }
        });
        amountTours.setText(String.format(Locale.GERMANY, "%1d",
                profileController.getAmountTours()));
        amountPOI.setText(String.format(Locale.GERMANY, "%1d",
                profileController.getAmountPoi()));
    }

    /**
     * Sets up tabs for the specific list views.
     * Tab positions:
     * 0 - Favoriten
     * 1 - Touren
     * 2 - POIs
     * 3 - Gespeicherte Touren
     *
     * @param view in which the list will be represented
     */
    private void setupTabs(View view) {

        tabLayout = view.findViewById(R.id.profileTabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        profileRV.setAdapter(profileFavoritesRVAdapter);
                        break;
                    case 1:
                        profileRV.setAdapter(profileTripRVAdapter);
                        break;
                    case 2:
                        profileRV.setAdapter(profilePOIRVAdapter);
                        break;
                    case 3:
                        profileRV.setAdapter(profileSavedRVAdapter);
                        break;
                    default:
                        profileRV.setAdapter(profileSavedRVAdapter);
                        break;
                }
                tvProfileNoContent.setVisibility((profileRV.getAdapter().getItemCount() == 0) ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void setupRVs(View view){
        //init textview if empty list
        tvProfileNoContent = view.findViewById(R.id.tvProfileNoContent);
        //set trip adapter
        profileTripRVAdapter = new ProfileTripRVAdapter(getActivity().getApplicationContext(),
                listTrips);
        profileTripRVAdapter.setClickListener(this::onRVItemClickTour);

        //set favorite adapter
        profileFavoritesRVAdapter =
                new ProfileFavoritesRVAdapter(getActivity().getApplicationContext(),
                        listFavorites);
        profileFavoritesRVAdapter.setClickListener(this::onRVItemClickTour);
        profileController.getFavorites(controllerEvent -> {
            switch (controllerEvent.getType()){
                case OK:
                    listFavorites.clear();
                    listFavorites.addAll((List) controllerEvent.getModel());
                    profileFavoritesRVAdapter.notifyDataSetChanged();
                    break;
            }
        });

        listTrips.clear();
        listTrips.addAll(profileController.getUserTours());
        profileTripRVAdapter.notifyDataSetChanged();

        //set saved adapter
        profileSavedRVAdapter = new ProfileSavedRVAdapter(getActivity().getApplicationContext(),
                listSaved);
        profileSavedRVAdapter.setClickListener(this::onRVItemClickSavedTour);
        listSaved.clear();
        listSaved.addAll(profileController.getSavedTours());
        profileSavedRVAdapter.notifyDataSetChanged();

        //set POI adapter
        profilePOIRVAdapter = new ProfilePOIRVAdapter(getActivity().getApplicationContext(),
                listPOIs);
        profilePOIRVAdapter.setClickListener(this::onRVItemClickPOI);
        listPOIs.clear();
        listPOIs.addAll(profileController.getPois());
        profilePOIRVAdapter.notifyDataSetChanged();

        //set Recyclerview
        profileRV = view.findViewById(R.id.listContent);
        profileRV.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        profileRV.setAdapter(profileFavoritesRVAdapter);
        
    }
    /**
     * handles click on an recycler view item
     * @param view representing the recycler view item
     * @param tour representing the tour of the clicked item
     */
    private void onRVItemClickTour(View view, Tour tour) {
        //distinguish what element was clicked by resource id
        switch (view.getId()) {
            case R.id.list_fav_icon:
                TourController tourController = new TourController(tour);
                tourController.unsetFavorite(controllerEvent -> {
                    switch (controllerEvent.getType()) {
                        case OK:
                            listFavorites.remove(tour);
                            profileFavoritesRVAdapter.notifyDataSetChanged();
                            break;
                        default:
                            Toast.makeText(getActivity(), R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
                break;
            case R.id.ListTourDelete:
                profileController.deleteTrip(tour, controllerEvent -> {
                    switch (controllerEvent.getType()) {
                        case OK:
                            setProfileStats();
                            listTrips.remove(tour);
                            profileTripRVAdapter.notifyDataSetChanged();
                            break;
                        default:
                            Toast.makeText(getActivity().getApplicationContext(), R.string.connection_fail, Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
                break;
            default:
                new AsyncCheckTourExists(tour,getActivity()).execute();
        }
    }
    /**
     * handles click on an recycler view item
     * @param view representing the recycler view item
     * @param tour representing the tour of the clicked item
     */
    private void onRVItemClickSavedTour(View view, SavedTour tour) {
        //distinguish what element was clicked by resource id
        switch (view.getId()) {
            case R.id.ListSavedIcon:
                profileController.deleteCommunityTour(tour);
                MapCacheHandler handler = new MapCacheHandler(getActivity(), tour.toTour());
                handler.deleteMap();
                listSaved.remove(tour);
                profileSavedRVAdapter.notifyDataSetChanged();
                break;
            default:
                new AsyncCheckTourExists(tour.toTour(),getActivity()).execute();
        }
    }

    /**
     * handles click on an recycler view item
     * @param view representing the recycler view item
     * @param poi
     */
    private void onRVItemClickPOI(View view, Poi poi) {
        //distinguish what element was clicked by resource id
        switch (view.getId()) {
            case R.id.ListTourEdit:
                PoiEditDialog.newInstance(poi).show(getActivity().getFragmentManager(), Constants.EDIT_POI_DIALOG);
                break;
            case R.id.ListTourDelete:
                ConfirmDeletePoiDialog deleteDialog = ConfirmDeletePoiDialog.newInstance(
                        getActivity().getApplicationContext(), poiController, poi, getActivity().getApplicationContext().getString(R.string.message_confirm_delete_poi));
                deleteDialog.setupForProfileList(this);
                deleteDialog.show(getFragmentManager(), Constants.CONFIRM_DELETE_POI_DIALOG);
                setProfileStats();
                break;
            default:
                PoiViewDialog.newInstance(poi).show(getFragmentManager(), Constants.DISPLAY_FEEDBACK_DIALOG);

                //new AsyncCheckPOIExists(poi,getActivity()).execute();
        }
    }

    /**
     * handles async backend request for performing a check if the tour is existing
     * this will keep the UI responsive
     *
     * @author Alexander Weinbeck
     * @license MIT
     */
    private class AsyncCheckTourExists extends AsyncTask<Void, Void, Void> {
        final ProgressDialog pdLoading;
        private final Tour tour;
        private final Activity activity;
        private Integer responseCode;
        private final TourOverviewController tourOverviewController;

        AsyncCheckTourExists(Tour tour, Activity activity){
            this.tour = tour;
            this.activity = activity;
            pdLoading = new ProgressDialog(this.activity);
            tourOverviewController = new TourOverviewController();
        }
        @Override
        protected void onPreExecute() {
            //this method will be running on UI thread
            pdLoading.setMessage("\t" + getResources().getString(R.string.msg_processing_open_tour));
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            setThreadPriority(-10);
            responseCode = tourOverviewController.checkIfTourExists(tour);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            setThreadPriority(-10);
            switch(EventType.getTypeByCode(responseCode)) {
                case OK:
                    if (BuildConfig.DEBUG) Log.d(TAG,"Server Response arrived -> OK Tour was found");
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, TourFragment.newInstance(tour), Constants.TOUR_FRAGMENT)
                            .addToBackStack(Constants.TOUR_FRAGMENT)
                            .commit();
                    //TODO: check if needed
                    //((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    break;
                case NOT_FOUND:
                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> Tour was not found");
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_tour_not_existing), Toast.LENGTH_LONG).show();
                    tourOverviewController.removeRecentTour(tour);
                    break;
                case SERVER_ERROR:
                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> SERVER ERROR");
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_server_error_get_tour), Toast.LENGTH_LONG).show();
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_LONG).show();
                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> NETWORK ERROR");
                    break;
                default:
                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> UNDEFINED ERROR");
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_general_error), Toast.LENGTH_LONG).show();
            }

            if (pdLoading.isShowing()) pdLoading.dismiss();
        }
    }

//    public void setupFavorites() {
//        profileRV.setAdapter(profileFavoritesRVAdapter);
//    }
//
//    /**
//     * This method is invoked when the tab at position 1 is selected. Sets up model with
//     * user tours and adapter to represent the users tours in a custom list view
//     */
//    public void setupMyTours(View view) {
//
//        profileRV.setAdapter(profileTripListAdapter);
//        profileRV.setOnItemClickListener((parent, view1, position, id) -> {
//            Tour tour = (Tour) profileRV.getItemAtPosition(position);
//            new AsyncCheckTourExists(tour,getActivity()).execute();
//        });
//    }

//    /**
//     * This method is invoked when the tab at position 2 is selected. Sets up the model with
//     * poi's and adapter to represent the users poi's in a custom list view
//     */
//    public void setupPOIs(View view) {
//
//        List<Poi> poiList = profileController.getPois();
//        List listPois = Collections.emptyList();
//        //only if there is at least one poi
//        if (poiList != null && poiList.size() > 0) {
//
//
//            //initialize list
//            listPois = poiList;
//
//            //set adapter
//            ProfilePoiListAdapter adapter =
//                    new ProfilePoiListAdapter(getActivity(),
//                            R.layout.recyclerview_profile_poi,
//                            R.id.ListTourTitle,
//                            listPois, this);
//
//            profileRV.setAdapter(adapter);
//            profileRV.setOnItemClickListener((parent, view1, position, id) -> {
//                Poi poi = (Poi) profileRV.getItemAtPosition(position);
//                PoiViewDialog viewDialog = PoiViewDialog.newInstance(poi);
//                viewDialog.show(getFragmentManager(), Constants.DISPLAY_FEEDBACK_DIALOG);
//            });
//
//        } else {
//
//            listPois = null;
//            profileRV.setAdapter(null);
//        }
//    }

//    /**
//     * This method is invoked when the tab at position 3 is selected. Sets up the model with
//     * saved tours and adapter to represent the users saved tours in a custom list view
//     * @param view
//     */
//    public void setupSaved(View view) {
//
//        List<SavedTour> communityTourList = profileController.getSavedTours();
//
//        //only if there is at least one saved tour
//        if (communityTourList != null && communityTourList.size() > 0) {
//
//            listSaved.clear();
//            //initialize list
//            listSaved.addAll(communityTourList);
//
//            //set adapter
//            ProfileSavedListAdapter adapter =
//                    new ProfileSavedListAdapter(getActivity(),
//                            R.layout.recyclerview_profile_saved,
//                            R.id.ListSavedTitle,
//                            listSaved, this);
//
//            profileRV.setAdapter(adapter);
//            profileRV.setOnItemClickListener((parent, view1, position, id) ->
//                new AsyncCheckTourExists(((SavedTour) profileRV.getItemAtPosition(position)).toTour(),getActivity()).execute()
//            );
//
//        }
//    }
//
//    /**
//     * Gets back the reference to the specific Controller of the profile UI
//     *
//     * @return the profile controller
//     */
//    public ProfileController getProfileController() {
//
//        return this.profileController;
//    }


}
