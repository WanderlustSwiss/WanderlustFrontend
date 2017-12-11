package eu.wise_iot.wanderlust.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ProfileController;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.views.adapters.ProfileFavoritesListAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfilePoiListAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfileSavedListAdapter;
import eu.wise_iot.wanderlust.views.adapters.ProfileTripListAdapter;

/**
 * ProfileFragment:
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileFragment extends Fragment {

    private ImageView profilePicture;
    private TextView amountPOI;
    private TextView amountScore;
    private TextView amountTours;
    private TextView birthday;

    private TabLayout tabLayout;

    private ListView listView;

    private List<String> testTourList;
    private List testFavList;
    private List testPOIList;
    private List testSaveList;


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
        //Profile picture
        profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.images);
        //Bitmap bitmap1 = BitmapFactory.decodeFile(profileController.getProfilePicture());

        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        drawable.setCircular(true);
        profilePicture.setImageDrawable(drawable);

        //initializing views
        birthday = (TextView) view.findViewById(R.id.profileBirthDay);
        amountPOI = (TextView) view.findViewById(R.id.profileAmountPOI);
        amountTours = (TextView) view.findViewById(R.id.profileAmountTours);
        amountScore = (TextView) view.findViewById(R.id.profileAmountScore);

        listView = (ListView) view.findViewById(R.id.listContent);

        //setting data
        /*amountScore.setText(profileController.getScore());
        amountTours.setText(Long.toString(profileController.getAmountTours()));
        amountPOI.setText(Long.toString(profileController.getAmountPoi()));

        birthday.setText(profileController.getBirthDate());*/

        //set nickname in App Bar
        //getActivity().setTitle(profileController.getNickName());
        getActivity().setTitle("nickname");

    }

    public void setupTabs(View view){
        tabLayout = (TabLayout) view.findViewById(R.id.profileTabs);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int id = tab.getPosition();

                switch(id){
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
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void setupFavorites(){
        //get list when Favorite Tours dataobject done
        /*ProfileFavoritesListAdapter adapter =
                new ProfileFavoritesListAdapter(getActivity(),
                                            R.layout.fragment_profile_list_favorites,
                                            R.id.ListFavTitle,
                                            testFavList);

        listView.setAdapter(adapter);*/

        Toast.makeText(getActivity(), "Deine Favoriten", Toast.LENGTH_SHORT).show();
    }

    public void setupMyTours(){
        //Test the UI with strings
        testTourList = new ArrayList<String>();

        String s1 = "Beispieltitel1";
        String s2 = "Beispielwanderung 2";
        String s3 = "Blablablawanderung";

        testTourList.add(s1);
        testTourList.add(s2);
        testTourList.add(s3);

        ProfileTripListAdapter adapter =
                new ProfileTripListAdapter(getActivity(),
                                            R.layout.fragment_profile_list_tour_poi,
                                            R.id.ListTourTitle,
                                            testTourList);

        listView.setAdapter(adapter);

        Toast.makeText(getActivity(), "Deine Touren", Toast.LENGTH_SHORT).show();
    }

    public void setupPOIs(){
        //get list with controller when POI dataobject done
        /*ProfilePoiListAdapter adapter =
                new ProfilePoiListAdapter(getActivity(),
                                            R.layout.fragment_profile_list_tour_poi,
                                            R.id.ListTourTitle,
                                            testPOIList);

        listView.setAdapter(adapter);*/

        Toast.makeText(getActivity(), "Deine POI's", Toast.LENGTH_SHORT).show();
    }

    public void setupSaved(){
        //get list with controller when Saved Tours dataobject done
        /*ProfileSavedListAdapter adapter =
                new ProfileSavedListAdapter(getActivity(),
                                            R.layout.fragment_profile_list_saved,
                                            R.id.ListSavedTitle,
                                            testSaveList);

        listView.setAdapter(adapter);*/

        Toast.makeText(getActivity(), "Deine gespeicherten Touren", Toast.LENGTH_SHORT).show();
    }


}
