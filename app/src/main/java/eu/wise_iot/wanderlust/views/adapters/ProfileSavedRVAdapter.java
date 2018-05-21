package eu.wise_iot.wanderlust.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.models.DatabaseModel.SavedTour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.services.ServiceGenerator;

/**
 * Adapter for the profile UI. Represents all favorites in a custom list view
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class ProfileSavedRVAdapter extends RecyclerView.Adapter<ProfileSavedRVAdapter.ViewHolder> {

    private ItemClickListener mClickListener;
    private final LayoutInflater mInflater;

    private static final String TAG = "PSRVAdapter";
    private final Activity activity;
    private final Context context;
    private final List<SavedTour> tours;

    private final ImageController imageController;

    public ProfileSavedRVAdapter(Context context, List<SavedTour> tours, Activity activity) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.tours = tours;
        this.activity = activity;
        this.imageController = ImageController.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Creating View Holder");
        View view = mInflater.inflate(R.layout.fragment_profile_list_saved, parent, false);
        return new ViewHolder(view);
    }

    /**
     * binds the data to the view and textview in each row
     *
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (BuildConfig.DEBUG) Log.d(TAG, "starting set properties");

        //get the item for this row
        SavedTour tour = this.tours.get(position);

        //set data
        if (tour != null) {
            holder.title.setText(tour.getTitle());
            holder.description.setText(tour.getDescription());

            List<File> imagefiles = imageController.getImages(tour.getImagePaths());
            if (!imagefiles.isEmpty() && imagefiles.get(0).length() != 0) {
                //handler.setIndicatorsEnabled(true);
                String url = ServiceGenerator.API_BASE_URL + "/tour/" + tour.getTour_id() + "/img/" + tour.getImagePaths().get(0).getId();
                imageController.getPicassoHandler(activity)
                        .load(url)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.tripImage);
            } else {
                Picasso.with(context)
                        .load(R.drawable.example_image).placeholder(R.drawable.progress_animation)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.progress_animation)
                        .into(holder.tripImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.tours.size();
    }

    private SavedTour getItem(int id) {
        return this.tours.get(id);
    }

    public void setClickListener (ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, SavedTour tour);
    }

    /**
     * stores and recycles views as they are scrolled off screen
     * @author Alexander Weinbeck
     * @license MIT
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title, description;
        private ImageView tripImage;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.ListSavedTitle);
            description = (TextView) itemView.findViewById(R.id.ListSavedDescription);
            tripImage = (ImageView) itemView.findViewById(R.id.ListSavedImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getItem(getAdapterPosition()));
        }
    }
}


