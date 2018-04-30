package eu.wise_iot.wanderlust.views.dialog;

import android.app.DialogFragment;
import android.content.res.Resources;
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
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.ViolationType;
import eu.wise_iot.wanderlust.models.DatabaseObject.ViolationTypeDao;

/**
 * TourReportDialog:
 *
 * @author Rilind Gashi
 * @license MIT
 */

public class TourReportDialog extends DialogFragment {

    private TourController tourController;
    private Tour tour;

    private static final String TAG = "TourRatingDialog";
    private ImageButton buttonSave, buttonCancel;
    private RadioButton rbHarassment, rbViolence, rbHatespeech, rbSpam, rbOther, rbNudity;

    private ViolationTypeDao violationTypeDao = ViolationTypeDao.getInstance();


    public TourReportDialog newInstance(Tour tour, TourController tourController) {
        TourReportDialog dialog = new TourReportDialog();
        Bundle args = new Bundle();
        dialog.setArguments(args);
        dialog.tour = tour;
        dialog.tourController = tourController;
        //this.violationTypeDao = ViolationTypeDao.getInstance();
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_report_tour, container, false);

        buttonCancel = (ImageButton) view.findViewById(R.id.violationTourCancelButton);
        buttonSave = (ImageButton) view.findViewById(R.id.violationTourSaveButton);
        rbHarassment = (RadioButton) view.findViewById(R.id.violationTourHarassment);
        rbHatespeech = (RadioButton) view.findViewById(R.id.violationTourHateSpeech);
        rbNudity = (RadioButton) view.findViewById(R.id.violationTourNudity);
        rbOther = (RadioButton) view.findViewById(R.id.violationTourOther);
        rbSpam = (RadioButton) view.findViewById(R.id.violationTourSpam);
        rbViolence = (RadioButton) view.findViewById(R.id.violationTourViolence);

        buttonCancel.setOnClickListener(v -> getDialog().dismiss());
        buttonSave.setOnClickListener(v -> reportTour());

        return view;
    }

    private void reportTour(){
        String violationName = null;
        if(rbHarassment.isChecked()) violationName = rbHarassment.getTag().toString();
        else if(rbHatespeech.isChecked()) violationName = rbHatespeech.getTag().toString();
        else if(rbNudity.isChecked()) violationName = rbNudity.getTag().toString();
        else if(rbOther.isChecked()) violationName = rbOther.getTag().toString();
        else if(rbSpam.isChecked()) violationName = rbSpam.getTag().toString();
        else if(rbViolence.isChecked()) violationName = rbViolence.getTag().toString();
        else if(violationName == null) violationName = "other";

        ViolationType violationType = violationTypeDao.getViolationTypebyName(violationName);

        tourController.reportViolation(tourController.new Violation(this.tour.getTour_id(), violationType.getViolationt_id()), controllerEvent -> {
            switch (controllerEvent.getType()){
                case OK:
                    getDialog().dismiss();
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.report_submit), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    getDialog().dismiss();
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet) + " " + controllerEvent.getType().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}

