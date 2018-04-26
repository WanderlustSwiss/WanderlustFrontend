package eu.wise_iot.wanderlust.views.controls;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.EquipmentController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;

public class EquipmentCompletionView extends TokenCompleteTextView<Equipment> {

    private EquipmentController controller;

    public EquipmentCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Equipment equipment) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) l.inflate(R.layout.equipment_token, (ViewGroup) getParent(), false);
        view.setText(equipment.getName());
        return view;
    }


    @Override
    protected Equipment defaultObject(String completionText) {
        controller = EquipmentController.createInstance(getContext());
        for (Equipment equipment : controller.getExtraEquipmentList()){
            if (equipment.getName().toLowerCase().contains(completionText) || equipment.getName().contains(completionText)){
                return equipment;
            }
        }

        return null;
    }
}
