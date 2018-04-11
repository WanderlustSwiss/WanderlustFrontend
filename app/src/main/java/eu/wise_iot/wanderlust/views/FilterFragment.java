package eu.wise_iot.wanderlust.views;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.w3c.dom.Text;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;

/**
 * Fragment that contains the filtering functionality for all tours
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class FilterFragment extends Fragment {

    private RangeSeekBar rsbDifficulty, rsbDistance;
    private Button btnSearch;
    private TextInputEditText tiName, tiRegion;
    private CheckBox cbT1, cbT2, cbT3, cbT4, cbT5, cbT6;

    public static FilterFragment newInstance() {
        Bundle args = new Bundle();
        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_filtertours, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rsbDifficulty = (RangeSeekBar)view.findViewById(R.id.rsbDifficulty);
        rsbDistance = (RangeSeekBar)view.findViewById(R.id.rsbDistance);
        btnSearch = (Button)view.findViewById(R.id.btnSearch);
        tiName = (TextInputEditText)view.findViewById(R.id.tiTourNameInput);
        tiRegion = (TextInputEditText)view.findViewById(R.id.tiTourRegionInput);
        cbT1 = (CheckBox)view.findViewById(R.id.checkboxT1);
        cbT2 = (CheckBox)view.findViewById(R.id.checkboxT2);
        cbT3 = (CheckBox)view.findViewById(R.id.checkboxT3);
        cbT4 = (CheckBox)view.findViewById(R.id.checkboxT4);
        cbT5 = (CheckBox)view.findViewById(R.id.checkboxT5);
        cbT6 = (CheckBox)view.findViewById(R.id.checkboxT6);

        btnSearch.setOnClickListener((View v) -> performSearch());
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
        setting.distanceS = (int)rsbDistance.getSelectedMinValue();
        setting.distanceE = (int)rsbDistance.getSelectedMaxValue();
        setting.region = tiRegion.getText().toString();
        setting.name = tiName.getText().toString();

        ResultFilterFragment resultFilterFragment = ResultFilterFragment.newInstance(setting);
        getFragmentManager().beginTransaction()
                .add(R.id.content_frame, resultFilterFragment, Constants.RESULT_FILTER_FRAGMENT)
                .addToBackStack(Constants.RESULT_FILTER_FRAGMENT)
                .commit();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    /**
     * settings model for filter
     */
    public final class FilterSetting {
        boolean cbT1, cbT2, cbT3, cbT4, cbT5, cbT6;
        String region, name;
        int distanceS, distanceE;
    }
}
