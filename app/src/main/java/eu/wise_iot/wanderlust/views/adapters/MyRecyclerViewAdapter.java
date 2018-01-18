package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.app.Fragment;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;


/**
 * MyAdapter:
 * provides adapter for recycleview which is used by the tourslist
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

//    private List<String> mImages = Collections.emptyList();
//    private List<String> mTitles = Collections.emptyList();
    private List<UserTour> userTours = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    private FavoriteDao favoriteDao = new FavoriteDao();
    private TourController tourController = new TourController();
    private List<Long> favorizedTours = new ArrayList<>();


    // data is passed into the constructor, here as a UserTour
    public MyRecyclerViewAdapter(Context context, List<UserTour> parTours) {
        Log.d("ToursRecyclerview", "Copy Constructor");
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.userTours = parTours;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("ToursRecyclerview", "Creating View Holder");
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("ToursRecyclerview", "starting set properties");
        //set properties for each element
        UserTour userTour = this.userTours.get(position);
        holder.tvTitle.setTextColor(Color.BLACK);
        //difficulty calculations
        long difficulty = userTour.getDifficulty();
        if(difficulty > 4) holder.tvDifficultyIcon.setColorFilter(ContextCompat.getColor(this.context, R.color.redHard));
        else if(difficulty > 2) holder.tvDifficultyIcon.setColorFilter(ContextCompat.getColor(this.context, R.color.yellowMiddle));
        else holder.tvDifficultyIcon.setColorFilter(ContextCompat.getColor(this.context, R.color.greenEasy));
        holder.tvDifficulty.setText("T " + String.valueOf(difficulty));


        holder.ibShare.setColorFilter(ContextCompat.getColor(this.context, R.color.heading_icon_unselected));
        holder.ibSave.setColorFilter(ContextCompat.getColor(this.context, R.color.heading_icon_unselected));
        holder.ibFavorite.setColorFilter(ContextCompat.getColor(this.context, R.color.heading_icon_unselected));
        //button Favorite
        for(Favorite favorite : favoriteDao.find()) {
            if (favorite.getTour() == userTour.getTour_id()) {
                holder.ibFavorite.setColorFilter(ContextCompat.getColor(this.context, R.color.red));
                //add to favored tours
                this.favorizedTours.add(favorite.getTour());
            }
        }

        Double distance = tourController.getDistance(userTour.getPolyline());
        holder.tvTitle.setText(userTour.getTitle());
        holder.tvDistance.setText(String.valueOf(userTour.getDistance()/1000) + " km");


        File image = userTour.getImageById((byte)1);

        Picasso.with(context).load(image).into(holder.tvImage);

        Log.d("Toursoverview", "Image loaded: " + image.toString());
        //calc time can be way more accurate:
        String time = "~" + (int) Math.ceil(distance / tourController.WALKING_SPEED) + " h";
        holder.tvTime.setText(String.valueOf(userTour.getDuration()) + " mingit ");
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return this.userTours.size();
    }

    // convenience method for getting data at click position
    public UserTour getItem(int id) {
        return this.userTours.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int id, UserTour tour, List<Long> favorites);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //properties list for each tour
        public TextView tvDistance;
        public TextView tvDifficulty;
        public TextView tvRegion;
        public TextView tvTitle;
        public TextView tvTime;
        public ImageView tvImage;
        public ImageView tvDifficultyIcon;
        public ImageButton ibFavorite;
        public ImageButton ibSave;
        public ImageButton ibShare;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDistance = (TextView) itemView.findViewById(R.id.tourDistance);
            tvDifficulty = (TextView) itemView.findViewById(R.id.tourDifficulty);
            tvRegion = (TextView) itemView.findViewById(R.id.tourRegion);
            tvTitle = (TextView) itemView.findViewById(R.id.tourTitle);
            tvTime = (TextView) itemView.findViewById(R.id.tourTime);
            tvImage = (ImageView) itemView.findViewById(R.id.tourImage);
            tvDifficultyIcon = (ImageView) itemView.findViewById(R.id.imageDifficulty);
            ibFavorite = (ImageButton)itemView.findViewById(R.id.favoriteButton);
            ibSave = (ImageButton)itemView.findViewById(R.id.saveButton);
            ibShare = (ImageButton)itemView.findViewById(R.id.shareButton);

            itemView.setOnClickListener(this);
            ibFavorite.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(), getItem(getAdapterPosition()), favorizedTours);
        }
    }
}