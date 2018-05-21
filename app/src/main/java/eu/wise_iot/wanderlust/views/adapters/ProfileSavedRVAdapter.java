package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.models.DatabaseModel.SavedTour;
import eu.wise_iot.wanderlust.services.GlideApp;

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
    private final Context context;
    private final List<SavedTour> tours;

    private final ImageController imageController;

    public ProfileSavedRVAdapter(Context context, List<SavedTour> tours) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.tours = tours;
        this.imageController = ImageController.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Creating View Holder");
        View view = mInflater.inflate(R.layout.recyclerview_profile_saved, parent, false);
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

            GlideApp.with(context)
                    .load(imageController.getURLImageTourSingle(tour.toTour()))
                    .error(R.drawable.no_image_found)
                    .placeholder(R.drawable.progress_animation)
                    .centerCrop()
                    .into(holder.savedImage);
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

        private final TextView title;
        private final TextView description;
        private final ImageView savedImage;
        private final ImageView savedIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ListSavedTitle);
            description = itemView.findViewById(R.id.ListSavedDescription);
            savedImage = itemView.findViewById(R.id.ListSavedImageView);
            savedIcon = itemView.findViewById(R.id.ListSavedIcon);
            itemView.setOnClickListener(this);
            savedIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getItem(getAdapterPosition()));
        }
    }
}


