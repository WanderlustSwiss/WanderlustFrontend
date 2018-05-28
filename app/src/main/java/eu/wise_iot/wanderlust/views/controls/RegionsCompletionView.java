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
import eu.wise_iot.wanderlust.models.DatabaseObject.RegionDao;

/**
 * Represents a token auto complete text-view used for putting in regions with usage of third party library
 * see TokenCompleteTextView for reference inside the filter
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
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
        RegionDao regionDao = RegionDao.getInstance();
        for(Region region : regionDao.find())
           if(region.getName().toLowerCase().contains(completionText) || region.getName().contains(completionText))
               return region;
        return null;
    }
}