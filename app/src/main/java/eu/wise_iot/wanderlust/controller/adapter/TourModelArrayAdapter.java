package eu.wise_iot.wanderlust.controller.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.model.Tour;

/**
 * TourModelArrayAdapter:
 * @author Fabian Schwander
 * @license MIT
 */
public class TourModelArrayAdapter extends ArrayAdapter<Tour> {
    private static final String TAG = "TourModelArrayAdapter";
    private Context context;

    private List<Tour> allTours;

    public TourModelArrayAdapter(Context context, int resource, List<Tour> objects) {
        super(context, resource, objects);
        this.context = context;
        this.allTours = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // get the Tour we are displaying
        Tour tour = allTours.get(position);

        // get the Inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item, parent, false);

        TextView title = (TextView) view.findViewById(R.id.tour_title);
        TextView subtitle = (TextView) view.findViewById(R.id.tour_subtitle);
        ImageView image = (ImageView) view.findViewById(R.id.tour_teaser_image);

        // sets the text to each view in one item
        title.setText(String.valueOf(tour.getName()));
        subtitle.setText(String.valueOf(tour.getDuration() + ", " + tour.getDifficultyWithPrefix()));

        // compresses the img before passing in the view
        int imageId = context.getResources().getIdentifier(tour.getTeaserImageWithoutSuffix(), "drawable", context.getPackageName());
        Picasso.with(context).load(imageId).fit().into(image);

        return view;
    }
}
