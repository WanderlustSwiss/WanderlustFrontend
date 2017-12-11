package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;

/**
 * Created by Baris Demirci on 06.12.2017.
 */

public class ProfileFavoritesListAdapter extends ArrayAdapter{

    private TextView title;
    private TextView description;

    private ImageView tripImage;
    private ImageView favIcon;

    private Context context;
    private int resource;
    private int textResource;
    private List objects;

    public ProfileFavoritesListAdapter(Context context, int resource, int textResource, List objects) {
        super(context, resource, textResource, objects);
        this.context = context;
        this.resource = resource;
        this.textResource = textResource;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(Object item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the item for this row
        Object fav = getItem(position);

        //inflate the row layout
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile_list_favorites, parent, false);

        //look up the view for elements
        title = (TextView) convertView.findViewById(R.id.ListFavTitle);
        description = (TextView) convertView.findViewById(R.id.ListFavDescription);

        tripImage = (ImageView) convertView.findViewById(R.id.ListTourImageView);
        favIcon = (ImageView) convertView.findViewById(R.id.ListFavIcon);

        //set data


        //set listeners

        return convertView;
    }
}
