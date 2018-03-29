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
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.views.ProfileFragment;

/**
 * Adapter for the profile UI. Represents all user tours in a custom list view
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileTripListAdapter extends ArrayAdapter<Tour> {

    private TextView title;

    private ImageView tripImage;
    private ImageView editIcon;
    private ImageView deleteIcon;

    private Context context;

    private final ProfileFragment profileFragment;
    private final ImageController imageController;

    public ProfileTripListAdapter(Context context, int resource, int textResource, List objects, ProfileFragment fragment) {
        super(context, resource, textResource, objects);
        this.context = context;
        this.imageController = ImageController.getInstance();
        this.profileFragment = fragment;
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
    public Tour getItem(int position) {
        return super.getItem(position);
    }

    /**
     * Gets the position of a specific user tour
     *
     * @param item the user tour to look for
     * @return position of the user tour
     */
    @Override
    public int getPosition(Tour item) {
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
     * @param position    index of user tour
     * @param convertView view of fragment
     * @param parent      view where list element is represented
     * @return view of one user tour
     */
    @Nonnull
    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
        //get the item for this row
        Tour tour = getItem(position);

        //inflate the row layout
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile_list_tour_poi, parent, false);

        //look up the view for elements
        title = (TextView) convertView.findViewById(R.id.ListTourTitle);

        tripImage = (ImageView) convertView.findViewById(R.id.ListTourImageView);
        editIcon = (ImageView) convertView.findViewById(R.id.ListTourEdit);
        deleteIcon = (ImageView) convertView.findViewById(R.id.ListTourDelete);

        //set data
        if (tour != null) {
            String t = StringUtils.abbreviate(tour.getTitle(), 30);
            title.setText(t);

            List<ImageInfo> imageinfos = tour.getImagePaths();
            List<File> imagefiles = imageController.getImages(imageinfos);
            if(!imagefiles.isEmpty() && imagefiles.get(0).length() != 0){
                Picasso.with(context)
                        .load(imagefiles.get(0))
                        .into(tripImage);
            }else{
                tripImage.setImageResource(R.drawable.example_image);
            }

            deleteIcon.setOnClickListener(e -> profileFragment.getProfileController().deleteTrip(tour, controllerEvent -> {
                switch (controllerEvent.getType()){
                    case OK:
                        profileFragment.setProfileStats();
                        profileFragment.setupMyTours(profileFragment.getView());
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
