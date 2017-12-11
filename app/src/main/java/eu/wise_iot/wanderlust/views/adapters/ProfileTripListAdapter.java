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

import javax.annotation.Nonnull;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;

/**
 * Created by Ali Laptop on 01.12.2017.
 */

public class ProfileTripListAdapter extends ArrayAdapter<String> {

    private TextView title;
    private TextView description;

    private ImageView tripImage;
    private ImageView editIcon;
    private ImageView deleteIcon;

    private Context context;
    private int resource;
    private int textResource;
    private List objects;

    public ProfileTripListAdapter(Context context, int resource, int textResource, List objects) {
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
    public String getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(String item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Nonnull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the item for this row
        String s = getItem(position);

        //inflate the row layout
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile_list_tour_poi, parent, false);

        //look up the view for elements
        title = (TextView) convertView.findViewById(R.id.ListTourTitle);
        description = (TextView) convertView.findViewById(R.id.ListTourDescription);

        tripImage = (ImageView) convertView.findViewById(R.id.ListTourImageView);
        editIcon = (ImageView) convertView.findViewById(R.id.ListTourEdit);
        deleteIcon = (ImageView) convertView.findViewById(R.id.ListTourDelete);

        //set data
        /*title.setText(tour.getTitle());
        description.setText(tour.getDescription());
        Uri uri = Uri.parse(tour.getImagePath());
        tripImage.setImageURI(uri);*/

        title.setText(s);
        description.setText(s);

        tripImage.setImageResource(R.drawable.img_302);

        //set listeners

        return convertView;
    }
}
