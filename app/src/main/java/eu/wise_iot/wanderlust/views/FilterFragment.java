package eu.wise_iot.wanderlust.views;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.FilterController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Region;
import eu.wise_iot.wanderlust.services.FragmentService;
import eu.wise_iot.wanderlust.views.controls.RegionsCompletionView;
import eu.wise_iot.wanderlust.views.dialog.TourRatingDialogFilter;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Contains the filtering functionality for all tours
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class FilterFragment extends Fragment {

    private RangeSeekBar rsbDistance, rsbDuration;
    private Button btnSearch;
    private RegionsCompletionView tiRegion;
    private AutoCompleteTextView tiName;
    private CheckBox cbT1, cbT2, cbT3, cbT4, cbT5, cbT6;
    private FilterController filterController;
    private RatingBar ratingBar;

    public static FilterFragment newInstance() {
        Bundle args = new Bundle();
        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        filterController = new FilterController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tour_filter, container, false);

        ratingBar = view.findViewById(R.id.tourRatingFilter);
        rsbDistance = view.findViewById(R.id.rsbDistance);
        rsbDuration = view.findViewById(R.id.rsbDuration);
        btnSearch = view.findViewById(R.id.btnSearch);
        tiName = view.findViewById(R.id.tiTourNameInput);
        tiRegion = view.findViewById(R.id.tiTourRegionInput);
        cbT1 = view.findViewById(R.id.checkboxT1);
        cbT2 = view.findViewById(R.id.checkboxT2);
        cbT3 = view.findViewById(R.id.checkboxT3);
        cbT4 = view.findViewById(R.id.checkboxT4);
        cbT5 = view.findViewById(R.id.checkboxT5);
        cbT6 = view.findViewById(R.id.checkboxT6);

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        cbT1.setChecked(sharedPreferences.getBoolean("filter_cbt1", true));
        cbT2.setChecked(sharedPreferences.getBoolean("filter_cbt2", true));
        cbT3.setChecked(sharedPreferences.getBoolean("filter_cbt3", true));
        cbT4.setChecked(sharedPreferences.getBoolean("filter_cbt4", true));
        cbT5.setChecked(sharedPreferences.getBoolean("filter_cbt5", true));
        cbT6.setChecked(sharedPreferences.getBoolean("filter_cbt6", true));
        ratingBar.setRating(sharedPreferences.getFloat("filter_rating",0.0f));
        rsbDistance.setSelectedMaxValue(sharedPreferences.getFloat("filter_distancee",40f));
        rsbDistance.setSelectedMinValue(sharedPreferences.getFloat("filter_distances",0.0f));
        rsbDuration.setSelectedMaxValue(sharedPreferences.getFloat("filter_duratione",40f));
        rsbDuration.setSelectedMinValue(sharedPreferences.getFloat("filter_durations",0.0f));
        tiName.setText(sharedPreferences.getString("filter_name", ""));
        tiRegion.setText(sharedPreferences.getString("filter_region", ""));

        return view;
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        getActivity().invalidateOptionsMenu();
        if(menu.findItem(R.id.filterIcon) != null)
            menu.findItem(R.id.filterIcon).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //handle keyboard closing
        view.findViewById(R.id.filterRootLayout).setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            return true;
        });
        
        ratingBar.setOnTouchListener((View v, MotionEvent e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                TourRatingDialogFilter dialog = new TourRatingDialogFilter().newInstance(filterController, ratingBar);
                dialog.show(getFragmentManager(), Constants.RATE_TOUR_FILTER_DIALOG);
            }
            return true;
        });

        btnSearch.setOnClickListener((View v) -> performSearch());
        tiRegion.setThreshold(1);

        ArrayAdapter<Region> regionAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, filterController.getRegions());
        tiRegion.setAdapter(regionAdapter);
    }

    /**
     * performs search with given input values on tours given by server
     */
    private void performSearch(){
        //read settings
        FilterSetting setting = new FilterSetting();
        setting.cbT1 = cbT1.isChecked();
        setting.cbT2 = cbT2.isChecked();
        setting.cbT3 = cbT3.isChecked();
        setting.cbT4 = cbT4.isChecked();
        setting.cbT5 = cbT5.isChecked();
        setting.cbT6 = cbT6.isChecked();
        setting.distanceS = ((int)rsbDistance.getSelectedMinValue() * 1000);
        setting.distanceE = ((int)rsbDistance.getSelectedMaxValue() * 1000);
        setting.durationS = ((int)rsbDuration.getSelectedMinValue() * 60);
        setting.durationE = ((int)rsbDuration.getSelectedMaxValue() * 60);
        setting.rating = ratingBar.getRating();

        //build query for regions
        StringBuilder sb = new StringBuilder();
        for(Region r : tiRegion.getObjects())
            sb.append(r.getRegion_id()).append(',');
        if(!sb.toString().isEmpty())sb.deleteCharAt(sb.length() - 1);
        setting.region = sb.toString();
        setting.name = tiName.getText().toString();
        SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean("filter_cbt1",setting.cbT1);
        editor.putBoolean("filter_cbt2",setting.cbT2);
        editor.putBoolean("filter_cbt3",setting.cbT3);
        editor.putBoolean("filter_cbt4",setting.cbT4);
        editor.putBoolean("filter_cbt5",setting.cbT5);
        editor.putBoolean("filter_cbt6",setting.cbT6);
        editor.putFloat("filter_rating",setting.rating);
        editor.putFloat("filter_distances", rsbDistance.getSelectedMinValue().floatValue());
        editor.putFloat("filter_distancee", rsbDistance.getSelectedMaxValue().floatValue());
        editor.putFloat("filter_durations", rsbDuration.getSelectedMinValue().floatValue());
        editor.putFloat("filter_duratione", rsbDuration.getSelectedMaxValue().floatValue());
        editor.putString("filter_name", tiName.getText().toString());
        editor.putString("filter_region", tiRegion.getText().toString());
        editor.apply();

        FragmentService
                .getInstance(getActivity())
                .performTraceTransaction(true, Constants.RESULT_FILTER_FRAGMENT, ResultFilterFragment.newInstance(setting),this);
/*        getFragmentManager().beginTransaction().hide(this).commit();

        Fragment resultFragment = getFragmentManager().findFragmentByTag(Constants.RESULT_FILTER_FRAGMENT);
        if (resultFragment != null) getFragmentManager().beginTransaction().remove(resultFragment).commit();

        ResultFilterFragment resultFilterFragment = ResultFilterFragment.newInstance(setting);
        getFragmentManager().beginTransaction()
                .add(R.id.content_frame, resultFilterFragment, Constants.RESULT_FILTER_FRAGMENT)
                //.addToBackStack(Constants.FILTER_FRAGMENT)
                .commit();*/
        //((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    /**
     * settings model for filter
     */
    public final class FilterSetting {
        boolean cbT1, cbT2, cbT3, cbT4, cbT5, cbT6;
        String name, region;
        int distanceS, distanceE, durationS, durationE;
        float rating;
    }
}
