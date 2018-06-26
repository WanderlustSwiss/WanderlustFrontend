package eu.wise_iot.wanderlust.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.signature.ObjectKey;

import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.services.AsyncUITask;
import eu.wise_iot.wanderlust.services.GlideApp;


/**
 * Provides adapter for recycler view which is used by the tours list
 *
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */
public class ToursOverviewRVAdapter extends RecyclerView.Adapter<ToursOverviewRVAdapter.ViewHolder> {

    private final List<Tour> tours;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final Context context;
    private final ImageController imageController;
    private final FavoriteDao favoriteDao = FavoriteDao.getInstance();
   // private final List<Long> favorizedTours = new ArrayList<>();


    /**
     * data is passed into the constructor, here as a Tour
     * @param context
     * @param tours
     */
    public ToursOverviewRVAdapter(Context context, List<Tour> tours, Activity activity) {
        if (BuildConfig.DEBUG) Log.d("ToursRecyclerview", "Copy Constructor");
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.tours = (tours != null) ? tours : new ArrayList<>();
        //get which tour is favored
        imageController = ImageController.getInstance();
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
        final Tour tour = tours.get(position);

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

        holder.ibShare.setColorFilter(ContextCompat.getColor(context, R.color.heading_icon_unselected));
        holder.ibSave.setColorFilter(ContextCompat.getColor(context, R.color.heading_icon_unselected));
        AsyncUITask.getHandler().queueTask(() -> {
            if(new TourController(tour).isSaved())
                holder.ibSave.setColorFilter(ContextCompat.getColor(context, R.color.medium));


            holder.ibFavorite.setColorFilter(ContextCompat.getColor(context, R.color.heading_icon_unselected));
            //button Favorite
            for (Favorite favorite : favoriteDao.find())
                if (favorite.getTour() == tour.getTour_id())
                    holder.ibFavorite.setColorFilter(ContextCompat.getColor(context, R.color.highlight_main));
                    //add to favored tours
                    //favorizedTours.add(favorite.getTour());
            holder.notify();
        });
        holder.tvTitle.setText(tour.getTitle());
        holder.tvDistance.setText(TourController.convertToStringDistance(tour.getDistance()));

        holder.tvTime.setText(TourController.convertToStringDuration(tour.getDuration()));

        GlideApp.with(context)
                .load(imageController.getURLImageTourSingle(tour))
                .signature(new ObjectKey(System.currentTimeMillis() / (24 * 60 * 60 * 1000)))
                .error(GlideApp.with(context).load(R.drawable.no_image_found).centerCrop())
                .placeholder(R.drawable.progress_animation)
                .centerCrop()
                .into(holder.tvImage);
    }

    /**
     * return total number of rows
      * @return
     */
    @Override
    public int getItemCount() {
        return tours.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        GlideApp.with(context).clear(holder.tvImage);
    }

    /**
     * convenience method for getting data at click position
     * @param id
     * @return
     */
    private Tour getItem(int id) {
        return tours.get(id);
    }

    /**
     * allows clicks events to be caught
     * @param itemClickListener
     */
    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
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
     * @license GPL-3.0
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
            tvDistance = itemView.findViewById(R.id.tourOVTourDistance);
            tvDifficulty = itemView.findViewById(R.id.tourOVTourDifficulty);
            tvTitle = itemView.findViewById(R.id.tourOVTourTitle);
            tvTime = itemView.findViewById(R.id.tourOVTourTime);
            tvImage = itemView.findViewById(R.id.tourOVTourImage);
            tvDifficultyIcon = itemView.findViewById(R.id.tourOVImageDifficulty);
            ibFavorite = itemView.findViewById(R.id.tourOVFavoriteButton);
            ibSave = itemView.findViewById(R.id.tourOVSaveButton);
            ibShare = itemView.findViewById(R.id.tourOVShareButton);

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