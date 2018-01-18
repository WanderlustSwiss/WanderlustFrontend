package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.annotation.Nonnull;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;

/**
 * Adapter for the profile UI. Represents all poi's in a custom list view
 *
 * @author Baris Demirci
 * @license MIT
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

    /**
     * Gets amount of poi's in list view
     *
     * @return amount of poi's
     */
    @Override
    public int getCount() {
        return super.getCount();
    }

    /**
     * Gets the poi at a specific position back
     *
     * @param position index of poi
     * @return poi at position
     */
    @Override
    public Poi getItem(int position) {
        return super.getItem(position);
    }

    /**
     * Gets the position of a specific poi
     *
     * @param item the poi to look for
     * @return position of the poi
     */
    @Override
    public int getPosition(Poi item) {
        return super.getPosition(item);
    }

    /**
     * Gets the Id of a poi at a specific position
     *
     * @param position index of poi
     * @return Id of poi
     */
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    /**
     * Gets the custom representation of one poi in the list view back
     *
     * @param position    index of poi
     * @param convertView view of fragment
     * @param parent      view where list element is represented
     * @return view of one poi
     */
    @Nonnull
    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
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
        if (poi != null) {
            title.setText(poi.getTitle());
            description.setText(poi.getDescription());

            //TODO: set image
            poiImage.setImageResource(R.drawable.example_image);
        }

        //set listeners
        //TODO: implement listeners for delete and edit icon, as well click listener for element

        return convertView;
    }
}
