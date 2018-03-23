package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.views.ProfileFragment;

/**
 * Adapter for the profile UI. Represents all saved tours in a custom list view
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileSavedListAdapter extends ArrayAdapter<Tour> {

    private TextView title;

    private ImageView tripImage;
    private ImageView deleteIcon;

    private Context context;
    private int resource;
    private int textResource;
    private List objects;

    private ImageController imageController;
    private ProfileFragment profileFragment;

    public ProfileSavedListAdapter(Context context, int resource, int textResource, List objects, ProfileFragment fragment) {
        super(context, resource, textResource, objects);
        this.context = context;
        this.resource = resource;
        this.textResource = textResource;
        this.objects = objects;
        this.imageController = ImageController.getInstance();
        this.profileFragment = fragment;
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
    public Tour getItem(int position) {
        return super.getItem(position);
    }

    /**
     * Gets the position of a specific saved tour
     *
     * @param item the saved tour to look for
     * @return position of the saved tour
     */
    @Override
    public int getPosition(Tour item) {
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
        Tour communityTour = getItem(position);

        //inflate the row layout
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile_list_saved, parent, false);

        //look up the view for elements
        title = (TextView) convertView.findViewById(R.id.ListSavedTitle);

        tripImage = (ImageView) convertView.findViewById(R.id.ListSavedImageView);
        deleteIcon = (ImageView) convertView.findViewById(R.id.ListSavedIcon);

        //set data
        if (communityTour != null) {
            String t = StringUtils.abbreviate(communityTour.getTitle(), 30);
            title.setText(t);

            List<ImageInfo> imageinfos = communityTour.getImagePaths();
            List<File> imagefiles = imageController.getImages(imageinfos);
            if(!imagefiles.isEmpty() && imagefiles.get(0).length() > 0){
                Picasso.with(context)
                        .load(imagefiles.get(0))
                        .into(tripImage);
            }else{
                tripImage.setImageResource(R.drawable.example_image);
            }
            
            deleteIcon.setOnClickListener(e -> {
                profileFragment.getProfileController().deleteCommunityTour(communityTour);
                View v = profileFragment.getView();
                profileFragment.setupSaved(v);
            });
        }
        return convertView;
    }
}
