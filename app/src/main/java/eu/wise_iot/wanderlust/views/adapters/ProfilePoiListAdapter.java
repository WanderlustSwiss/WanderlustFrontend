package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;

/**
 * Created by Baris Demirci on 06.12.2017.
 */

public class ProfilePoiListAdapter extends ArrayAdapter<Poi> {

    private TextView title;
    private TextView description;

    private ImageView poiImage;
    private ImageView editIcon;
    private ImageView deleteIcon;

    private Context context;
    private int resource;
    private int textResource;
    private List objects;

    public ProfilePoiListAdapter(Context context, int resource, int textResource, List objects) {
        super(context, resource, textResource, objects);
        this.context = context;
        this.resource = resource;
        this.textResource = textResource;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Poi getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(Poi item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the item for this row
        Poi poi = getItem(position);

        //inflate the row layout
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile_list_tour_poi, parent, false);

        //look up the view for elements
        title = (TextView) convertView.findViewById(R.id.ListTourTitle);
        description = (TextView) convertView.findViewById(R.id.ListTourDescription);

        poiImage = (ImageView) convertView.findViewById(R.id.ListTourImageView);
        editIcon = (ImageView) convertView.findViewById(R.id.ListTourEdit);
        deleteIcon = (ImageView) convertView.findViewById(R.id.ListTourDelete);

        //set data
        /*title.setText(poi.getTitle());
        description.setText(poi.getDescription());*/

        //Test data



        //set listeners

        return convertView;
    }
}
