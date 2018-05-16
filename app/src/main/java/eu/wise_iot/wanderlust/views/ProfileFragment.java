package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Locale;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ProfileController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Trip;
import eu.wise_iot.wanderlust.views.adapters.ProfileFavoritesListAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfilePoiListAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfileSavedListAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfileTripListAdapter;
import eu.wise_iot.wanderlust.views.animations.CircleTransform;
import eu.wise_iot.wanderlust.views.dialog.PoiViewDialog;

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

    private TabLayout tabLayout;

    private ListView listView;
    private List list;

    private final ProfileController profileController;

    public ProfileFragment() {
        // Required empty public constructor
        profileController = new ProfileController();
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

        setupTabs(view);
        setupProfileInfo(view);
        setProfileStats();

        //load default list
        tabLayout.getTabAt(0).select();
        setupFavorites(view);
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

        listView = (ListView) view.findViewById(R.id.listContent);

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
        profileController.getScore(new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                switch (controllerEvent.getType()){
                    case OK:
                        Profile profile = (Profile) controllerEvent.getModel();
                        int score = ((Profile) controllerEvent.getModel()).getScore();
                        amountScore.setText(String.format(Locale.GERMAN,"%1d" ,score));
                        Log.d("SCORE",  String.valueOf(score));
                        break;
                    default:
                        Log.d("SCORE",  "Could not load Score");
                        break;
                }
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
        //initializing views
        listView = (ListView) view.findViewById(R.id.listContent);
        tabLayout = (TabLayout) view.findViewById(R.id.profileTabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int id = tab.getPosition();

                switch (id) {
                    case 0:
                        setupFavorites(view);
                        break;
                    case 1:
                        setupMyTours(view);
                        break;
                    case 2:
                        setupPOIs(view);
                        break;
                    case 3:
                        setupSaved(view);
                        break;
                    default:
                        setupMyTours(view);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * This method is invoked when the tab at position 0 is selected. Sets up model with
     * favorites and adapter to represent the users favorites in a custom list view
     */
    public void setupFavorites(View view) {

        ProfileFragment fragment = this;
        profileController.getFavorites(controllerEvent -> {
            switch (controllerEvent.getType()){
                case OK:

                    list = (List) controllerEvent.getModel();

                    if (list != null && list.size() > 0) {

                        //set adapter
                        ProfileFavoritesListAdapter adapter =
                                new ProfileFavoritesListAdapter(getActivity(),
                                        R.layout.fragment_profile_list_favorites,
                                        R.id.list_fav_title,
                                        list, fragment);

                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
                                Tour tour = (Tour) listView.getItemAtPosition(position);


                                TourFragment tourFragment = TourFragment.newInstance(tour);
                                Fragment oldTourFragment = getFragmentManager().findFragmentByTag(Constants.TOUR_FRAGMENT);
                                if(oldTourFragment != null) {
                                    getFragmentManager().beginTransaction()
                                            .remove(oldTourFragment)
                                            .commit();
                                }
                                getFragmentManager().beginTransaction()
                                                    .add(R.id.content_frame, tourFragment, Constants.TOUR_FRAGMENT)
                                                    .addToBackStack(Constants.TOUR_FRAGMENT)
                                                    .commit();
                            }
                        });

                    } else {

                        list = null;
                        listView.setAdapter(null);
                        Toast.makeText(getActivity(), R.string.no_favorites, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    list = null;
                    listView.setAdapter(null);
                    break;
            }
        });
    }

    /**
     * This method is invoked when the tab at position 1 is selected. Sets up model with
     * user tours and adapter to represent the users tours in a custom list view
     */
    public void setupMyTours(View view) {

        list = profileController.getTrips();

        if(list.size() > 0 && list != null){
            List<Trip> trips = list;
            list.clear();
            for(Trip trip : trips){
                profileController.getTourToTrip(trip, controllerEvent -> {
                    switch (controllerEvent.getType()){
                        case OK:
                            if(controllerEvent.getModel() != null){
                                Tour tour = (Tour) controllerEvent.getModel();
                                list.add(tour);
                            }
                            break;
                        default:
                            break;
                    }
                });
                Toast.makeText(getActivity(), String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
            }
        }
        //only if there is at least one tour
        if (list != null && list.size() > 0) {

            //set adapter
            ProfileTripListAdapter adapter =
                    new ProfileTripListAdapter(getActivity(),
                            R.layout.fragment_profile_list_tour_poi,
                            R.id.ListTourTitle,
                            list, this);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                Tour tour = (Tour) listView.getItemAtPosition(position);

                TourFragment tourFragment = TourFragment.newInstance(tour);
                Fragment oldTourFragment = getFragmentManager().findFragmentByTag(Constants.TOUR_FRAGMENT);
                if(oldTourFragment != null) {
                    getFragmentManager().beginTransaction()
                            .remove(oldTourFragment)
                            .commit();
                }
                getFragmentManager().beginTransaction()
                                    .add(R.id.content_frame, tourFragment, Constants.TOUR_FRAGMENT)
                                    .addToBackStack(Constants.TOUR_FRAGMENT)
                                    .commit();
            });

        } else {

            list = null;
            listView.setAdapter(null);
        }
    }

    /**
     * This method is invoked when the tab at position 2 is selected. Sets up the model with
     * poi's and adapter to represent the users poi's in a custom list view
     */
    public void setupPOIs(View view) {

        List<Poi> poiList = profileController.getPois();

        //only if there is at least one poi
        if (poiList != null && poiList.size() > 0) {

            //initialize list
            list = poiList;

            //set adapter
            ProfilePoiListAdapter adapter =
                    new ProfilePoiListAdapter(getActivity(),
                            R.layout.fragment_profile_list_tour_poi,
                            R.id.ListTourTitle,
                            list, this);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                Poi poi = (Poi) listView.getItemAtPosition(position);
                PoiViewDialog viewDialog = PoiViewDialog.newInstance(poi);
                viewDialog.show(getFragmentManager(), Constants.DISPLAY_FEEDBACK_DIALOG);
            });

        } else {

            list = null;
            listView.setAdapter(null);
        }
    }

    /**
     * This method is invoked when the tab at position 3 is selected. Sets up the model with
     * saved tours and adapter to represent the users saved tours in a custom list view
     */
    public void setupSaved(View view) {

        List<Tour> communityTourList = profileController.getSavedTours();

        //only if there is at least one saved tour
        if (communityTourList != null && communityTourList.size() > 0) {

            //initialize list
            list = communityTourList;

            //set adapter
            ProfileSavedListAdapter adapter =
                    new ProfileSavedListAdapter(getActivity(),
                            R.layout.fragment_profile_list_saved,
                            R.id.ListSavedTitle,
                            list, this);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                Tour tour = (Tour) listView.getItemAtPosition(position);
                TourFragment tourFragment = TourFragment.newInstance(tour);
                Fragment oldTourFragment = getFragmentManager().findFragmentByTag(Constants.TOUR_FRAGMENT);
                if(oldTourFragment != null) {
                    getFragmentManager().beginTransaction()
                            .remove(oldTourFragment)
                            .commit();
                }
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, tourFragment, Constants.TOUR_FRAGMENT)
                        .addToBackStack(Constants.TOUR_FRAGMENT)
                        .commit();
            });

        } else {

            list = null;
            listView.setAdapter(null);
        }
    }

    /**
     * Gets back the reference to the specific Controller of the profile UI
     *
     * @return the profile controller
     */
    public ProfileController getProfileController() {

        return this.profileController;
    }


}
