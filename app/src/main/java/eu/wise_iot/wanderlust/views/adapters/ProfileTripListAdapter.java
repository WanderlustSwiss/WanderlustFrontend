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
 * Adapter for the profile UI. Represents all user tours in a custom list view
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileTripListAdapter extends ArrayAdapter<UserTour> {

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

    /**
     * Gets amount of user tours in list view
     *
     * @return amount of user tours
     */
    @Override
    public int getCount() {
        return super.getCount();
    }

    /**
     * Gets the user tour at a specific position back
     *
     * @param position index of user tour
     * @return user tour at position
     */
    @Override
    public UserTour getItem(int position) {
        return super.getItem(position);
    }

    /**
     * Gets the position of a specific user tour
     *
     * @param item the user tour to look for
     * @return position of the user tour
     */
    @Override
    public int getPosition(UserTour item) {
        return super.getPosition(item);
    }

    /**
     * Gets the Id of a user tour at a specific position
     *
     * @param position index of user tour
     * @return Id of user tour
     */
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    /**
     * Gets the custom representation of one user tour in the list view back
     *
     * @param position index of user tour
     * @param convertView view of fragment
     * @param parent view where list element is represented
     * @return view of one user tour
     */
    @Nonnull
    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
        //get the item for this row
        UserTour userTour = getItem(position);

        //inflate the row layout
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile_list_tour_poi, parent, false);

        //look up the view for elements
        title = (TextView) convertView.findViewById(R.id.ListTourTitle);
        description = (TextView) convertView.findViewById(R.id.ListTourDescription);

        tripImage = (ImageView) convertView.findViewById(R.id.ListTourImageView);
        editIcon = (ImageView) convertView.findViewById(R.id.ListTourEdit);
        deleteIcon = (ImageView) convertView.findViewById(R.id.ListTourDelete);

        //set data
        if(userTour != null){
            title.setText(userTour.getTitle());
            description.setText(userTour.getDescription());

            //TODO: set the image
        }

        //set listeners
        //TODO: implement listeners for delete and edit icon, as well click listener for elements

        return convertView;
    }
}
