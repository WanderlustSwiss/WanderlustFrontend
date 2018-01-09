package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;


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


    // data is passed into the constructor, here as a UserTour
    public MyRecyclerViewAdapter(Context context, List<UserTour> parTours) {
        Log.d("ToursRecyclerview", "Copy Constructor");
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.userTours = parTours;
        /*this.mImages = images;
        this.mTitles = titles;*/

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
//        String ImagePath = mImages.get(position);
//        String title = mTitles.get(position);
        Log.d("ToursRecyclerview", "starting set properties");
            //set properties for each element
            holder.myView.setBackgroundColor(Color.WHITE);
            //holder.tvTitle.setText(this.userTours.get(position).getTitle());
            holder.tvDifficulty.setText("T " + String.valueOf(this.userTours.get(position).getDifficulty()));
            holder.tvTitle.setText(this.userTours.get(position).getTitle());

            //Profile picture, example
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.images);
            //TODO: profile picture from the database
            //Bitmap bitmap1 = BitmapFactory.decodeFile(profileController.getProfilePicture());

            //RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            //TODO: unknown properties so far:
            //holder.tvRating.setText("N/A");
//            holder.tvAscend.setText("N/A");
//            holder.tvDescend.setText("N/A");
            holder.tvDistance.setText("N/A");
//            Picasso.with(this.context)
//                    .load("URL")
//                    .into(holder.tvImage);

            //TODO set as specified in path
            //holder.myView.setBackground(ImagePath);
            holder.myTextView.setText("N/A");
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
        void onItemClick(View view, int id);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //properties list for each tour
        public View myView;
        public TextView myTextView;
        public TextView tvDistance;
        public TextView tvAscend;
        public TextView tvDescend;
        public TextView tvDifficulty;
        public TextView tvRegion;
        public TextView tvRating;
        public TextView tvTitle;
        public TextView tvTime;
        public ImageView tvImage;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView.findViewById(R.id.tvImage);
            myTextView = (TextView) itemView.findViewById(R.id.tourTitle);
            tvDistance = (TextView) itemView.findViewById(R.id.tourDistance);
            tvAscend = (TextView) itemView.findViewById(R.id.tourAscend);
            tvDescend = (TextView) itemView.findViewById(R.id.tourDescend);
            tvDifficulty = (TextView) itemView.findViewById(R.id.tourDifficulty);
            tvRegion = (TextView) itemView.findViewById(R.id.tourRegion);
            tvRating = (TextView) itemView.findViewById(R.id.tourRating);
            tvTitle = (TextView) itemView.findViewById(R.id.tourTitle);
            tvTime = (TextView) itemView.findViewById(R.id.tourTime);
            tvImage = (ImageView) itemView.findViewById(R.id.tourImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}