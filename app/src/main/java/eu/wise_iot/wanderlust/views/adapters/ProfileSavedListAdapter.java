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
import eu.wise_iot.wanderlust.models.DatabaseModel.CommunityTour;

/**
 * Adapter for the profile UI. Represents all saved tours in a custom list view
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileSavedListAdapter extends ArrayAdapter<CommunityTour> {

    private TextView title;
    private TextView description;

    private ImageView tripImage;
    private ImageView deleteIcon;

    private Context context;
    private int resource;
    private int textResource;
    private List objects;

    public ProfileSavedListAdapter(Context context, int resource, int textResource, List objects) {
        super(context, resource, textResource, objects);
        this.context = context;
        this.resource = resource;
        this.textResource = textResource;
        this.objects = objects;
    }

    /**
     * Gets amount of saved tours in list view
     *
     * @return amount of saved tours
     */
    @Override
    public int getCount() {
        return super.getCount();
    }

    /**
     * Gets the saved tour at a specific position back
     *
     * @param position index of saved tour
     * @return saved tour at position
     */
    @Override
    public CommunityTour getItem(int position) {
        return super.getItem(position);
    }

    /**
     * Gets the position of a specific saved tour
     *
     * @param item the saved tour to look for
     * @return position of the saved tour
     */
    @Override
    public int getPosition(CommunityTour item) {
        return super.getPosition(item);
    }

    /**
     * Gets the Id of a saved tour at a specific position
     *
     * @param position index of saved tour
     * @return Id of saved tour
     */
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    /**
     * Gets the custom representation of one saved tour in the list view back
     *
     * @param position    index of saved tour
     * @param convertView view of fragment
     * @param parent      view where list element is represented
     * @return view of one saved tour
     */
    @Nonnull
    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
        //get the item for this row
        CommunityTour communityTour = getItem(position);

        //inflate the row layout
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile_list_saved, parent, false);

        //look up the view for elements
        title = (TextView) convertView.findViewById(R.id.ListSavedTitle);
        description = (TextView) convertView.findViewById(R.id.ListSavedDescription);

        tripImage = (ImageView) convertView.findViewById(R.id.ListSavedImageView);
        deleteIcon = (ImageView) convertView.findViewById(R.id.ListSavedIcon);

        //set data
        if (communityTour != null) {
            title.setText(communityTour.getTitle());
            description.setText(communityTour.getDescription());

            //TODO: set the image
        }

        //set listeners
        //TODO: implement listener for delete icon and click listener for element

        return convertView;
    }
}
