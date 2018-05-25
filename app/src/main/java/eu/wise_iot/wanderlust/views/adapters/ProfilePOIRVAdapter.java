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
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.services.GlideApp;

/**
 * Adapter for the profile UI. Represents all favorites in a custom list view
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class ProfilePOIRVAdapter extends RecyclerView.Adapter<ProfilePOIRVAdapter.ViewHolder> {

    private ItemClickListener mClickListener;
    private final LayoutInflater mInflater;

    private static final String TAG = "PPRVAdapter";
    private final Context context;
    private final List<Poi> pois;

    private final ImageController imageController;

    public ProfilePOIRVAdapter(Context context, List<Poi> pois) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.pois = pois;
        imageController = ImageController.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Creating View Holder");
        View view = mInflater.inflate(R.layout.recyclerview_profile_poi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (BuildConfig.DEBUG) Log.d(TAG, "starting set properties");

        //get the item for this row
        Poi poi = pois.get(position);

        holder.title.setText(poi.getTitle());
        holder.description.setText(poi.getDescription());

        GlideApp.with(context)
                .load(imageController.getURLImagePOISingle(poi))
                .error(R.drawable.no_image_found)
                .placeholder(R.drawable.progress_animation)
                .centerCrop()
                .into(holder.tripImage);
    }

    @Override
    public int getItemCount() {
        return pois.size();
    }

    private Poi getItem(int id) {
        return pois.get(id);
    }

    public void setClickListener (ItemClickListener itemClickListener){
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, Poi poi);
    }

    /**
     * stores and recycles views as they are scrolled off screen
     * @author Alexander Weinbeck
     * @license MIT
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;
        private final TextView description;
        private final ImageView tripImage;
        private final ImageView editIcon;
        private final ImageView deleteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ListTourTitle);
            description = itemView.findViewById(R.id.ListTourDescription);
            tripImage = itemView.findViewById(R.id.ListTourImageView);
            editIcon = itemView.findViewById(R.id.ListTourEdit);
            deleteIcon = itemView.findViewById(R.id.ListTourDelete);
            itemView.setOnClickListener(this);
            editIcon.setOnClickListener(this);
            deleteIcon.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getItem(getAdapterPosition()));
        }
    }
}


