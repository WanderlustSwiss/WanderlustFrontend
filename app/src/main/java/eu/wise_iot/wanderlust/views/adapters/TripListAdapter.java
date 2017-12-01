package eu.wise_iot.wanderlust.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Trip;

/**
 * Created by Ali Laptop on 01.12.2017.
 */

public class TripListAdapter extends ArrayAdapter<Trip>{


    public TripListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Trip getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(Trip item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get the item for this row
        Trip trip = getItem(position);

        convertView = LayoutInflater.from(getContext()).inflate()
        return super.getView(position, convertView, parent);
    }
}
