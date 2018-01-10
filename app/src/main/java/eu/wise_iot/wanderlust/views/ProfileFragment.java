package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ProfileController;
import eu.wise_iot.wanderlust.views.adapters.ProfileFavoritesListAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfilePoiListAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfileSavedListAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfileTripListAdapter;

/**
 * Fragment which represents the UI of the profile of a user.
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileFragment extends Fragment {

    private ImageView profilePicture;
    private TextView amountPOI;
    private TextView amountScore;
    private TextView amountTours;
    //private TextView birthday;

    private TabLayout tabLayout;

    private ListView listView;
    private List list;

    private ProfileController profileController;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        //inflate view from xml-file
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setupProfileInfo(view);
        setupTabs(view);

        return view;
    }

    /**
     * Sets up profile picture, birthdate, amount of poi's, amount of tours and the score
     * in the profile UI. Additionally, the nickname of user is set in the toolbar.
     *
     * @param view in which the data is set
     */
    public void setupProfileInfo(View view) {
        //initializing views
        //birthday = (TextView) view.findViewById(R.id.profileBirthDay);
        amountPOI = (TextView) view.findViewById(R.id.profileAmountPOI);
        amountTours = (TextView) view.findViewById(R.id.profileAmountTours);
        amountScore = (TextView) view.findViewById(R.id.profileAmountScore);

        profilePicture = (ImageView) view.findViewById(R.id.profilePicture);

        listView = (ListView) view.findViewById(R.id.listContent);

        //Profile picture, example
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.images);
        //TODO: profile picture from the database
        //Bitmap bitmap1 = BitmapFactory.decodeFile(profileController.getProfilePicture());

        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        drawable.setCircular(true);
        profilePicture.setImageDrawable(drawable);

        //setting data
        if (profileController.profileExists()) {
            amountScore.setText(String.format(Locale.GERMANY, "%2d",
                    profileController.getScore()));
            amountTours.setText(String.format(Locale.GERMANY, "%2d",
                    profileController.getAmountTours()));
            amountPOI.setText(String.format(Locale.GERMANY, "%2d",
                    profileController.getAmountPoi()));
            //birthday.setText(profileController.getBirthDate());


            //set nickname in App Bar
            getActivity().setTitle(profileController.getNickName());
        }
    }

    /**
     * Sets up tabs for the specific list views.
     * Tab positions:
     * 0 - Favoriten
     * 1 - Touren
     * 2 - POI's
     * 3 - Gespeicherte Touren
     *
     * @param view in which the list will be represented
     */
    public void setupTabs(View view) {
        //initializing views
        tabLayout = (TabLayout) view.findViewById(R.id.profileTabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int id = tab.getPosition();

                switch (id) {
                    case 0:
                        setupFavorites();
                        break;
                    case 1:
                        setupMyTours();
                        break;
                    case 2:
                        setupPOIs();
                        break;
                    case 3:
                        setupSaved();
                        break;
                    default:
                        setupMyTours();
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
    public void setupFavorites() {
        //only if there is at least one favorite
        if (profileController.getFavorites() != null) {

            //initialize list
            list = profileController.getFavorites();

            //set adapter
            ProfileFavoritesListAdapter adapter =
                    new ProfileFavoritesListAdapter(getActivity(),
                            R.layout.fragment_profile_list_favorites,
                            R.id.ListFavTitle,
                            list);

            listView.setAdapter(adapter);
        } else {
            listView = null;
        }

        Toast.makeText(getActivity(), "Deine Favoriten", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is invoked when the tab at position 1 is selected. Sets up model with
     * user tours and adapter to represent the users tours in a custom list view
     */
    public void setupMyTours() {
        //only if there is at least one tour
        if (profileController.getTours() != null) {

            //initialize list
            list = profileController.getTours();

            //set adapter
            ProfileTripListAdapter adapter =
                    new ProfileTripListAdapter(getActivity(),
                            R.layout.fragment_profile_list_tour_poi,
                            R.id.ListTourTitle,
                            list);

            listView.setAdapter(adapter);
        } else {
            listView = null;
        }

        Toast.makeText(getActivity(), "Deine Touren", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is invoked when the tab at position 2 is selected. Sets up the model with
     * poi's and adapter to represent the users poi's in a custom list view
     */
    public void setupPOIs() {
        //only if there is at least one poi
        if (profileController.getPois() != null) {

            //initialize list
            list = profileController.getPois();

            //set adapter
            ProfilePoiListAdapter adapter =
                    new ProfilePoiListAdapter(getActivity(),
                            R.layout.fragment_profile_list_tour_poi,
                            R.id.ListTourTitle,
                            list);

            listView.setAdapter(adapter);
        } else {
            listView = null;
        }

        Toast.makeText(getActivity(), "Deine POI's", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is invoked when the tab at position 3 is selected. Sets up the model with
     * saved tours and adapter to represent the users saved tours in a custom list view
     */
    public void setupSaved() {
        //only if there is at least one saved tour
        if (profileController.getSavedTours() != null) {

            //initialize list
            list = profileController.getSavedTours();

            //set adapter
            ProfileSavedListAdapter adapter =
                    new ProfileSavedListAdapter(getActivity(),
                            R.layout.fragment_profile_list_saved,
                            R.id.ListSavedTitle,
                            list);

            listView.setAdapter(adapter);
        } else {
            listView = null;
        }

        Toast.makeText(getActivity(), "Deine gespeicherten Touren", Toast.LENGTH_SHORT).show();
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
