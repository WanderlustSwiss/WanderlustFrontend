package eu.wise_iot.wanderlust.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView titleTextView;
    public ImageView coverImageView;
    public ImageView likeImageView;
    public ImageView shareImageView;

    public MyViewHolder(View v) {
        super(v);
        titleTextView = (TextView) v.findViewById(R.id.titleTextView);
        coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
        likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
        shareImageView = (ImageView) v.findViewById(R.id.shareImageView);
        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int)likeImageView.getTag();
                if( id == R.drawable.ic_layers_black_24dp){

                    likeImageView.setTag(R.drawable.ic_layers_black_24dp);
                    likeImageView.setImageResource(R.drawable.ic_layers_black_24dp);

                    //Toast.makeText(getActivity(),titleTextView.getText()+"added to favourites",Toast.LENGTH_SHORT).show();

                }else{
                    likeImageView.setTag(R.drawable.ic_layers_black_24dp);
                    likeImageView.setImageResource(R.drawable.ic_layers_black_24dp);
                    //Toast.makeText(getActivity(),titleTextView.getText()+"removed from favourites",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}