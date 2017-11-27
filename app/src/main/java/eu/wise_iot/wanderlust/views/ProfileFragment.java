package eu.wise_iot.wanderlust.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.wise_iot.wanderlust.R;

/**
 * ProfileFragment:
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileFragment extends FragmentActivity {


    private TabLayout tabLayout;
    private TabItem tabMyTours;
    private TabItem tabFavorites;
    private TabItem tabPOI;
    private TabItem tabSaved;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tabMyTours = (TabItem) findViewById(R.id.tab_mytours);
        tabMyTours.setOnClickListener(e -> {
            showMyTours();
        });

    }

    private void showMyTours(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.profileTabContent, new ProfileMyToursFragment());
        ft.commit();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
