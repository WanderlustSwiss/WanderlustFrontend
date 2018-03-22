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
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.views.ProfileFragment;
import eu.wise_iot.wanderlust.views.dialog.ConfirmDeletePoiDialog;
import eu.wise_iot.wanderlust.views.dialog.PoiEditDialog;



/**
 * Adapter for the profile UI. Represents all poi's in a custom list view
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfilePoiListAdapter extends ArrayAdapter<Poi> {

    private TextView title;

    private ImageView poiImage;
    private ImageView editIcon;
    private ImageView deleteIcon;

    private Context context;
    private int resource;
    private int textResource;
    private List objects;

    private PoiController poiController;
    private ImageController imageController;

    private ProfileFragment profileFragment;

    public ProfilePoiListAdapter(Context context, int resource, int textResource, List objects, ProfileFragment fragment) {
        super(context, resource, textResource, objects);
        this.context = context;
        this.resource = resource;
        this.textResource = textResource;
        this.objects = objects;
        this.profileFragment = fragment;
        poiController = new PoiController();
        imageController = ImageController.getInstance();
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

        poiImage = (ImageView) convertView.findViewById(R.id.ListTourImageView);
        editIcon = (ImageView) convertView.findViewById(R.id.ListTourEdit);
        deleteIcon = (ImageView) convertView.findViewById(R.id.ListTourDelete);

        //set data
        if (poi != null) {
            String t = StringUtils.abbreviate(poi.getTitle(), 30);
            title.setText(t);

            List<ImageInfo> imagepaths = poi.getImagePaths();
            List<File> imagefiles = imageController.getImages(imagepaths);
            if (!imagefiles.isEmpty() && imagefiles.get(0).length() != 0) {
                Picasso.with(context)
                        .load(imagefiles.get(0))
                        .into(poiImage);
            } else {
                poiImage.setImageResource(R.drawable.example_image);
            }

            //listeners for deleting and editing
            editIcon.setOnClickListener(e -> {
                PoiEditDialog editDialog = PoiEditDialog.newInstance(poi);
                if (editDialog != null) {
                    editDialog.show(profileFragment.getFragmentManager(), Constants.EDIT_POI_DIALOG);
                }
            });


            deleteIcon.setOnClickListener(e -> {
                ConfirmDeletePoiDialog deleteDialog = ConfirmDeletePoiDialog.newInstance(
                        getContext(), poiController, poi, context.getString(R.string.message_confirm_delete_poi));
                deleteDialog.setupForProfileList(profileFragment);
                deleteDialog.show(profileFragment.getFragmentManager(), Constants.CONFIRM_DELETE_POI_DIALOG);
            });

        }

        return convertView;
    }


}
