package eu.wise_iot.wanderlust.views.adapters;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.ResultFilterController;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.services.ServiceGenerator;

import static eu.wise_iot.wanderlust.views.MainActivity.activity;


/**
 * MyAdapter:
 * provides adapter for recyclerview which is used by the tourslist
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class ResultFilterRVAdapter extends RecyclerView.Adapter<ResultFilterRVAdapter.ViewHolder> {
    private List<Tour> tours = Collections.emptyList();
    private final LayoutInflater mInflater;
    @SuppressWarnings("WeakerAccess")
    public ItemClickListener mClickListener;
    private final Context context;
    private final ImageController imageController;
    private final ResultFilterController resultFilterController;

    /**
     * data is passed into the constructor, here as a Tour
     * @param context
     * @param parTours
     */
    public ResultFilterRVAdapter(Context context, List<Tour> parTours) {
        Log.d("ToursRecyclerview", "Copy Constructor");
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        if(parTours == null) parTours = new ArrayList<>();
        this.tours = parTours;
        //get which tour is favored
        this.imageController = ImageController.getInstance();
        this.resultFilterController = new ResultFilterController();
    }

    /**
     * inflates the row layout from xml when needed
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("ToursRecyclerview", "Creating View Holder");
        View view = mInflater.inflate(R.layout.recyclerview_tour_filter_result, parent, false);
        return new ViewHolder(view);
    }

    /**
     * binds the data to the view and textview in each row
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("ToursRecyclerview", "starting set properties");
        //set properties for each element
        Tour tour = this.tours.get(position);
//        holder.tvTitle.setTextColor(Color.BLACK);
        //difficulty calculations
        long difficulty = tour.getDifficulty();
        if (difficulty >= 6)
            holder.ivTourDifficulty.setImageDrawable(context.getResources().getDrawable(R.drawable.t6_15dp));
        else if (difficulty >= 4)
            holder.ivTourDifficulty.setImageDrawable(context.getResources().getDrawable(R.drawable.t4_t5_15dp));
        else if (difficulty >= 2)
            holder.ivTourDifficulty.setImageDrawable(context.getResources().getDrawable(R.drawable.t2_t3_15dp));
        else
            holder.ivTourDifficulty.setImageDrawable(context.getResources().getDrawable(R.drawable.t1_15dp));

        holder.tvDifficulty.setText("T " + String.valueOf(difficulty));
        holder.tvDifficulty.setTextSize(14);

        holder.tvTitle.setText(tour.getTitle());
        holder.tvDistance.setText(TourController.convertToStringDistance(tour.getDistance()));
        holder.tvRegion.setText(context.getString(R.string.tour_region) + " " + resultFilterController.getRegionbyID(tour.getRegion(),this.context));

        holder.tvDescending.setText(tour.getDescent() + " m");
        holder.tvAscending.setText(tour.getAscent() + " m");

        List<File> images = imageController.getImages(tour.getImagePaths());
        if (!images.isEmpty()){
            Picasso handler = imageController.getPicassoHandler(activity);
            //handler.setIndicatorsEnabled(true);
            String url = ServiceGenerator.API_BASE_URL + "/tour/" + tour.getTour_id() + "/img/" + tour.getImagePaths().get(0).getId();
            handler.load(url).fit().centerCrop().noFade().placeholder(R.drawable.progress_animation).into(holder.tvImage);
            Log.d("ResultFilterRVAdapter", "ImageInfo loaded: " + url);
        } else {
            Picasso.with(context).load(R.drawable.no_image_found).fit().centerCrop().noFade()
                    .placeholder(R.drawable.progress_animation).into(holder.tvImage);
            Log.d("ResultFilterRVAdapter", "Images not found");
        }
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
        private final TextView tvDistance, tvDifficulty, tvRegion, tvAscending, tvDescending, tvTitle, tvTime;
        private final ImageView tvImage;
        private final ImageView ivTourDifficulty;

        /**
         * copy constructor for each element which holds the view
         * @param itemView
         */
        ViewHolder(View itemView) {
            super(itemView);
            tvDistance = (TextView) itemView.findViewById(R.id.tourOVTourDistance);
            tvDifficulty = (TextView) itemView.findViewById(R.id.tourOVTourDifficulty);
            tvRegion = (TextView) itemView.findViewById(R.id.tour_region);
            tvTitle = (TextView) itemView.findViewById(R.id.tourOVTourTitle);
            tvTime = (TextView) itemView.findViewById(R.id.tour_time);
            tvAscending = (TextView) itemView.findViewById(R.id.tour_ascend);
            tvDescending = (TextView) itemView.findViewById(R.id.tour_descend);
            tvImage = (ImageView) itemView.findViewById(R.id.tourOVTourImage);
            ivTourDifficulty = (ImageView) itemView.findViewById(R.id.ivTourDifficulty);

            itemView.setOnClickListener(this);
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