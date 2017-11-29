package eu.wise_iot.wanderlust.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import eu.wise_iot.wanderlust.R;

/**
 * MyAdapter:
 * provides adapter for recycleview which is used by the tourslist
 * @author Alexander Weinbeck
 * @license MIT
 */
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private ArrayList<String> list;
    public MyAdapter(ArrayList<String> Data) {
        list = Data;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_items, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.titleTextView.setText("test");
        //holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
        //holder.coverImageView.setTag(list.get(position).getImageResourceId());
        holder.likeImageView.setTag(R.drawable.ic_layers_black_24dp);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}