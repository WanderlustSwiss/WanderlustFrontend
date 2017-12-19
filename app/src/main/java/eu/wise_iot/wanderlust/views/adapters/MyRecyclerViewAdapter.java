package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import eu.wise_iot.wanderlust.R;

/**
 * MyAdapter:
 * provides adapter for recycleview which is used by the tourslist
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> mImages = Collections.emptyList();
    private List<String> mTitles = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<String> images, List<String> titles) {
        this.mInflater = LayoutInflater.from(context);
        this.mImages = images;
        this.mTitles = titles;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String ImagePath = mImages.get(position);
        String title = mTitles.get(position);
        holder.myView.setBackgroundColor(Color.GRAY);
        //TODO set as specified in path
        //holder.myView.setBackground(ImagePath);
        holder.myTextView.setText(title);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mTitles.get(id);
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
        public View myView;
        public TextView myTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView.findViewById(R.id.tvImage);
            myTextView = (TextView) itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}