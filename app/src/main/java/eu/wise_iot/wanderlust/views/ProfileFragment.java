package eu.wise_iot.wanderlust.views;

import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ProfileController;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;

/**
 * ProfileFragment:
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileFragment extends Fragment {

    private ImageView profilePicture;
    private TextView nickName;
    private TextView score;
    private TextView createdTours;

    private TabLayout tabLayout;
    private TabItem myToursTab;
    private TabItem favoritesTab;
    private TabItem poiTab;
    private TabItem savedTab;

    private User user;

    private ProfileController profileController;

    public ProfileFragment() {
        // Required empty public constructor
        profileController = new ProfileController(this);
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
        /*UserDao userDao = new UserDao(MainActivity.boxStore);
        this.user = userDao.getUser();

        initializeProfileData();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setupProfileInfo(view);
        setupTabs(view);

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void setupProfileInfo(View view){
        profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
        nickName = (TextView) view.findViewById(R.id.profileName);
        score = (TextView) view.findViewById(R.id.profileScore);
        createdTours = (TextView) view.findViewById(R.id.profileCreatedTours);

        //TODO:: get infos from database
    }

    public void setupTabs(View view){
        tabLayout = (TabLayout) view.findViewById(R.id.profileTabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int id = tab.getPosition();

                switch(id){
                    case 0:
                        setupMyTours();
                        break;
                    case 1:
                        setupFavorites();
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
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void initializeProfileData(){
        nickName.setText(profileController.getNickName());

    }

    public void setupMyTours(){
        ProfileMyToursFragment profileMyToursFragment = new ProfileMyToursFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.profileTabContent, profileMyToursFragment)
                .commit();

        Toast.makeText(getActivity(), "Deine Touren", Toast.LENGTH_SHORT).show();
    }

    public void setupFavorites(){
        ProfileFavoritesFragment profileFavoritesFragment = new ProfileFavoritesFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.profileTabContent, profileFavoritesFragment)
                .commit();

        Toast.makeText(getActivity(), "Deine Favoriten", Toast.LENGTH_SHORT).show();
    }

    public void setupPOIs(){
        ProfilePOIFragment profilePOIFragment = new ProfilePOIFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.profileTabContent, profilePOIFragment)
                .commit();

        Toast.makeText(getActivity(), "Deine POI's", Toast.LENGTH_SHORT).show();
    }

    public void setupSaved(){
        ProfileSavedFragment profileSavedFragment = new ProfileSavedFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.profileTabContent, profileSavedFragment)
                .commit();

        Toast.makeText(getActivity(), "Deine gespeicherten Touren", Toast.LENGTH_SHORT).show();
    }


}
