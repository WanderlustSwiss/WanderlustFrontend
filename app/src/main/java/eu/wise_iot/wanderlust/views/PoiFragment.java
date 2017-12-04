package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;

/**
 * PoiFragment
 * @author Rilind Gashi
 * @license: MIT
 */

public class PoiFragment extends Fragment{
    private static final String TAG = "PoiFragment";
    private Context context;
    private List<Poi> poiTypeList = new ArrayList<>();
    private PoiController poiController;


    public static PoiFragment newInstance() {
        Bundle args = new Bundle();
        PoiFragment fragment = new PoiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        poiController = new PoiController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poi, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spinner poiTypeSpinner = (Spinner) view.findViewById(R.id.poiTypeSpinner);
        ArrayAdapter<String> poiTypeArrayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item);

        for(PoiType poiType : poiController.getAllPoiTypes()){
            poiTypeArrayAdapter.add(poiType.getName());
        }
        poiTypeSpinner.setAdapter(poiTypeArrayAdapter);

        Spinner poiPrivacySpinner = (Spinner) view.findViewById(R.id.poiIsPublicSpinner);
        ArrayAdapter<String> poiPrivacyArrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item);
        poiPrivacyArrayAdapter.add(getResources().getString(R.string.poi_fragment_privacy_private_item));
        poiPrivacyArrayAdapter.add(getResources().getString(R.string.poi_fragment_privacy_public_item));
        poiPrivacySpinner.setAdapter(poiPrivacyArrayAdapter);

        TextView poiTitleTextView = (TextView) view.findViewById(R.id.poiTitleTextView);
        TextView poiDescriptionTextView = (TextView) view.findViewById(R.id.poiDescriptionTextView);

        /*
        String poiTitleTextViewContent = poiTitleTextView.getText().toString();
        String poiDescriptionTextViewContent = poiDescriptionTextView.getText().toString();
        String poiTypeSpinnerSelected = poiTypeSpinner.getSelectedItem().toString();
        String privacySpinnerSelected = poiPrivacySpinner.getSelectedItem().toString();

        poiController.savePoiToDatabase(poiTitleTextViewContent, poiDescriptionTextViewContent,"");
        */

    }
}
