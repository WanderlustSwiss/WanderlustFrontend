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
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.services.GlideApp;

/**
 * Adapter for the profile UI. Represents all favorites in a custom recycler view
 *
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */
public class ProfileFavoritesRVAdapter extends RecyclerView.Adapter<ProfileFavoritesRVAdapter.ViewHolder> {

    private ItemClickListener mClickListener;
    private final LayoutInflater mInflater;

    private static final String TAG = "PFRVAdapter";
    private final Context context;
    private final List<Tour> tours;

    private final ImageController imageController;

    public ProfileFavoritesRVAdapter(Context context, List<Tour> tours) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.tours = tours;
        imageController = ImageController.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Creating View Holder");
        View view = mInflater.inflate(R.layout.recyclerview_profile_favorite, parent, false);
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
        Tour fav = tours.get(position);

        holder.title.setText(fav.getTitle());
        holder.description.setText(fav.getDescription());

        GlideApp.with(context)
                .load(imageController.getURLImageTourSingle(fav))
                .error(GlideApp.with(context).load(R.drawable.no_image_found).centerCrop())
                .placeholder(R.drawable.progress_animation)
                .centerCrop()
                .into(holder.tripImage);
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    private Tour getItem(int id) {
        return tours.get(id);
    }

    public void setClickListener (ItemClickListener itemClickListener){
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, Tour tour);
    }

    /**
     * stores and recycles views as they are scrolled off screen
     * @author Alexander Weinbeck
     * @license GPL-3.0
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;
        private final TextView description;
        private final ImageView tripImage;
        private final ImageView favIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.list_fav_title);
            description = itemView.findViewById(R.id.list_fav_description);
            tripImage = itemView.findViewById(R.id.list_fav_image_view);
            favIcon = itemView.findViewById(R.id.list_fav_icon);
            favIcon.bringToFront();
            itemView.setOnClickListener(this);
            favIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getItem(getAdapterPosition()));
        }
    }
}

