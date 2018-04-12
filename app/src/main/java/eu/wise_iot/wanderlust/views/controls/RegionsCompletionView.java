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
import eu.wise_iot.wanderlust.models.DatabaseModel.Region;

public class RegionsCompletionView extends TokenCompleteTextView<Region> {
    public RegionsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Region Region) {

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) l.inflate(R.layout.region_token, (ViewGroup) getParent(), false);
        view.setText(Region.getName());

        return view;
    }

    @Override
    protected Region defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        int index = completionText.indexOf('@');
        /*
        if (index == -1) {
            return new Region(completionText, completionText.replace(" ", "") + "@example.com");
        } else {
            return new Region(completionText.substring(0, index), completionText);
        }*/
        return new Region(0,0, "test","8888");
    }
}