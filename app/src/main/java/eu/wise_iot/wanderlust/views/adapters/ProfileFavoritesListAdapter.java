package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.views.ProfileFragment;

/**
 * Adapter for the profile UI. Represents all favorites in a custom list view
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileFavoritesListAdapter extends ArrayAdapter<Tour> {

    private TextView title;

    private ImageView tripImage;
    private ImageView favIcon;

    private final Context context;

    private final ProfileFragment profileFragment;
    private final ImageController imageController;

    public ProfileFavoritesListAdapter(Context context, int resource, int textResource, List objects, ProfileFragment fragment) {
        super(context, resource, textResource, objects);
        this.context = context;
        this.profileFragment = fragment;
        this.imageController = ImageController.getInstance();
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
    public Tour getItem(int position) {
        return super.getItem(position);
    }

    /**
     * Gets the position of a specific favorite
     *
     * @param item the favorite to look for
     * @return position of the favorite
     */
    @Override
    public int getPosition(Tour item) {
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
        Tour fav = getItem(position);
        TourController tourController = new TourController(fav);
        //inflate the row layout
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile_list_favorites, parent, false);

        //look up the view for elements
        title = (TextView) convertView.findViewById(R.id.ListFavTitle);
        tripImage = (ImageView) convertView.findViewById(R.id.ListFavImageView);
        favIcon = (ImageView) convertView.findViewById(R.id.ListFavIcon);

        //set data
        if (fav != null) {
            String t = StringUtils.abbreviate(fav.getTitle(), 30);
            title.setText(t);

            List<ImageInfo> imageinfos = fav.getImagePaths();
            List<File> imagefiles = imageController.getImages(imageinfos);
            if (!imagefiles.isEmpty() && imagefiles.get(0).length() != 0) {
                Picasso.with(context)
                        .load(imagefiles.get(0))
                        .into(tripImage);
            } else {
                tripImage.setImageResource(R.drawable.example_image);
            }

            favIcon.setOnClickListener(e -> tourController.unsetFavorite(controllerEvent -> {
                EventType type = controllerEvent.getType();
                switch (type) {
                    case OK:
                        View v = profileFragment.getView();
                        profileFragment.setupFavorites(v);
                        break;
                    default:
                        Toast.makeText(context, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                        break;
                }
            }));

        }
        return convertView;
    }
}

