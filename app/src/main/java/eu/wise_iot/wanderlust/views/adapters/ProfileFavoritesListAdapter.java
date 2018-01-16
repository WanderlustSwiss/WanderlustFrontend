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

/**
 * Adapter for the profile UI. Represents all favorites in a custom list view
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileFavoritesListAdapter extends ArrayAdapter {

    private TextView title;
    private TextView description;

    private ImageView tripImage;
    private ImageView favIcon;

    private Context context;
    private int resource;
    private int textResource;
    private List objects;

    public ProfileFavoritesListAdapter(Context context, int resource, int textResource, List objects) {
        super(context, resource, textResource, objects);
        this.context = context;
        this.resource = resource;
        this.textResource = textResource;
        this.objects = objects;
    }

    /**
     * Gets amount of favorites in list view
     *
     * @return amount of favorites
     */
    @Override
    public int getCount() {
        return super.getCount();
    }

    /**
     * Gets the favorite at a specific position back
     *
     * @param position index of favorite
     * @return favorite at position
     */
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    /**
     * Gets the position of a specific favorite
     *
     * @param item the favorite to look for
     * @return position of the favorite
     */
    @Override
    public int getPosition(Object item) {
        return super.getPosition(item);
    }

    /**
     * Gets the Id of a favorite at a specific position
     *
     * @param position index of favorite
     * @return Id of favorite
     */
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    /**
     * Gets the custom representation of one favorite in the list view back
     *
     * @param position    index of favorite
     * @param convertView view of fragment
     * @param parent      view where list element is represented
     * @return view of one favorite
     */
    @Nonnull
    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
        //get the item for this row
        //TODO: as soon favorite entity is defined, replace all "object" references with entity
        Object fav = (String) getItem(position);

        //inflate the row layout
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile_list_favorites, parent, false);

        //look up the view for elements
        title = (TextView) convertView.findViewById(R.id.ListFavTitle);
        description = (TextView) convertView.findViewById(R.id.ListFavDescription);

        tripImage = (ImageView) convertView.findViewById(R.id.ListFavImageView);
        favIcon = (ImageView) convertView.findViewById(R.id.ListFavIcon);

        //set data
        //TODO: set data from entity
        title.setText("Beispiel-Favorite");
        description.setText("Das ist nur ein Beispiel-Favorit.");

        tripImage.setImageResource(R.drawable.example_image);

        //set listeners
        //TODO: implement listeners for favorite icon as well click listener for elements

        return convertView;
    }
}
