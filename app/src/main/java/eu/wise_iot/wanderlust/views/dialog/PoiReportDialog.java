package eu.wise_iot.wanderlust.views.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.ViolationType;
import eu.wise_iot.wanderlust.models.DatabaseObject.ViolationTypeDao;

/**
 * poiReportDialog:
 *
 * @author Rilind Gashi
 * @license MIT
 */

public class PoiReportDialog extends DialogFragment {

    private PoiController poiController;
    private Poi poi;

    private static final String TAG = "poiRatingDialog";
    private ImageButton buttonSave, buttonCancel;
    private RadioButton rbHarassment, rbViolence, rbHatespeech, rbSpam, rbOther, rbNudity;

    private ViolationTypeDao violationTypeDao = ViolationTypeDao.getInstance();


    public PoiReportDialog newInstance(Poi poi, PoiController poiController) {
        PoiReportDialog fragment = new PoiReportDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        this.poi = poi;
        this.poiController = poiController;
        //this.violationTypeDao = ViolationTypeDao.getInstance();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_report_poi, container, false);

        buttonCancel = (ImageButton) view.findViewById(R.id.violationPoiCancelButton);
        buttonSave = (ImageButton) view.findViewById(R.id.violationPoiSaveButton);
        rbHarassment = (RadioButton) view.findViewById(R.id.violationPoiHarassment);
        rbHatespeech = (RadioButton) view.findViewById(R.id.violationPoiHateSpeech);
        rbNudity = (RadioButton) view.findViewById(R.id.violationPoiNudity);
        rbOther = (RadioButton) view.findViewById(R.id.violationPoiOther);
        rbSpam = (RadioButton) view.findViewById(R.id.violationPoiSpam);
        rbViolence = (RadioButton) view.findViewById(R.id.violationPoiViolence);

        buttonCancel.setOnClickListener(v -> getDialog().dismiss());
        buttonSave.setOnClickListener(v -> reportPoi());

        return view;
    }

    private void reportPoi(){
        String violationName = null;
        if(rbHarassment.isChecked()) violationName = rbHarassment.getText().toString();
        else if(rbHatespeech.isChecked()) violationName = rbHatespeech.getText().toString();
        else if(rbNudity.isChecked()) violationName = rbNudity.getText().toString();
        else if(rbOther.isChecked()) violationName = rbOther.getText().toString();
        else if(rbSpam.isChecked()) violationName = rbSpam.getText().toString();
        else if(rbViolence.isChecked()) violationName = rbViolence.getText().toString();
        else if(violationName == null) violationName = "failure";
        ViolationType violationType = violationTypeDao.getViolationTypebyName(violationName);


        poiController.reportViolation(poiController.new Violation(this.poi.getPoi_id(), violationType), controllerEvent -> {
            switch (controllerEvent.getType()){
                case OK:
                    getDialog().dismiss();
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.report_submit), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    getDialog().dismiss();
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}

