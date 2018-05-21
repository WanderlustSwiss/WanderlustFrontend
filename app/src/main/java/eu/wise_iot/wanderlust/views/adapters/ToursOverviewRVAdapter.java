package eu.wise_iot.wanderlust.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.services.GlideApp;
import eu.wise_iot.wanderlust.services.GlideWL;
import eu.wise_iot.wanderlust.services.ServiceGenerator;


/**
 * MyAdapter:
 * provides adapter for recycler view which is used by the tourslist
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class ToursOverviewRVAdapter extends RecyclerView.Adapter<ToursOverviewRVAdapter.ViewHolder> {

    private final List<Tour> tours;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final Context context;
    private final Activity activity;
    private final ImageController imageController;
    private final FavoriteDao favoriteDao = FavoriteDao.getInstance();
    private final List<Long> favorizedTours = new ArrayList<>();


    /**
     * data is passed into the constructor, here as a Tour
     * @param context
     * @param parTours
     */
    public ToursOverviewRVAdapter(Context context, List<Tour> parTours, Activity activity) {
        if (BuildConfig.DEBUG) Log.d("ToursRecyclerview", "Copy Constructor");
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        if(parTours == null) parTours = new ArrayList<>();
        this.tours = parTours;
        //get which tour is favored
        this.imageController = ImageController.getInstance();
        this.activity = activity;
    }

    /**
     * inflates the row layout from xml when needed
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (BuildConfig.DEBUG) Log.d("ToursRecyclerview", "Creating View Holder");
        return new ViewHolder(mInflater.inflate(R.layout.recyclerview_tour_overview, parent, false));
    }

    /**
     * binds the data to the view and textview in each row
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (BuildConfig.DEBUG) Log.d("ToursRecyclerview", "starting set properties");
        //set properties for each element
        Tour tour = this.tours.get(position);

        TourController tourController = new TourController(tour);

        //difficulty calculations
        final long difficulty = tour.getDifficulty();
        if (difficulty >= 6)
            holder.tvDifficultyIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.t6));
        else if (difficulty >= 4)
            holder.tvDifficultyIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.t4_t5));
        else if (difficulty >= 2)
            holder.tvDifficultyIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.t2_t3));
        else
            holder.tvDifficultyIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.t1));

        holder.tvDifficulty.setText("T " + String.valueOf(difficulty));

        holder.ibShare.setColorFilter(ContextCompat.getColor(this.context, R.color.heading_icon_unselected));
        holder.ibSave.setColorFilter(ContextCompat.getColor(this.context, R.color.heading_icon_unselected));
        if(tourController.isSaved())
            holder.ibSave.setColorFilter(ContextCompat.getColor(this.context, R.color.medium));

        holder.ibFavorite.setColorFilter(ContextCompat.getColor(this.context, R.color.heading_icon_unselected));
        //button Favorite
        for (Favorite favorite : favoriteDao.find()) {
            if (favorite.getTour() == tour.getTour_id()) {
                holder.ibFavorite.setColorFilter(ContextCompat.getColor(this.context, R.color.highlight_main));
                //add to favored tours
                this.favorizedTours.add(favorite.getTour());
            }
        }
        holder.tvTitle.setText(tour.getTitle());
        holder.tvDistance.setText(TourController.convertToStringDistance(tour.getDistance()));

        GlideApp.with(context)
                .load(imageController.getURLForTourOVAdapterImage(tour))
                .error(R.drawable.no_image_found)
                .placeholder(R.drawable.progress_animation)
                .centerCrop()
                .into(holder.tvImage);

        holder.tvTime.setText(TourController.convertToStringDuration(tour.getDuration()));
    }

    /**
     * return total number of rows
      * @return
     */
    @Override
    public int getItemCount() {
        return this.tours.size();
    }

    /**
     * convenience method for getting data at click position
     * @param id
     * @return
     */
    private Tour getItem(int id) {
        return this.tours.get(id);
    }

    /**
     * allows clicks events to be caught
     * @param itemClickListener
     */
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    /**
     * parent activity will implement this interface to respond to click events
     */
    public interface ItemClickListener {
        void onItemClick(View view, Tour tour);
    }

    /**
     * stores and recycles views as they are scrolled off screen
     * @author Alexander Weinbeck
     * @license MIT
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //properties list for each tour
        private final TextView tvDistance, tvDifficulty, tvTitle, tvTime;
        private final ImageView tvImage, tvDifficultyIcon;
        private final ImageButton ibFavorite, ibSave, ibShare;

        /**
         * copy constructor for each element which holds the view
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            tvDistance = (TextView) itemView.findViewById(R.id.tourOVTourDistance);
            tvDifficulty = (TextView) itemView.findViewById(R.id.tourOVTourDifficulty);
            tvTitle = (TextView) itemView.findViewById(R.id.tourOVTourTitle);
            tvTime = (TextView) itemView.findViewById(R.id.tourOVTourTime);
            tvImage = (ImageView) itemView.findViewById(R.id.tourOVTourImage);
            tvDifficultyIcon = (ImageView) itemView.findViewById(R.id.tourOVImageDifficulty);
            ibFavorite = (ImageButton) itemView.findViewById(R.id.tourOVFavoriteButton);
            ibSave = (ImageButton) itemView.findViewById(R.id.tourOVSaveButton);
            ibShare = (ImageButton) itemView.findViewById(R.id.tourOVShareButton);

            itemView.setOnClickListener(this);
            ibFavorite.setOnClickListener(this);
            ibSave.setOnClickListener(this);
            ibShare.setOnClickListener(this);
        }

        /**
         * click event handler
         * @param view
         */
        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getItem(getAdapterPosition()));
        }
    }
}