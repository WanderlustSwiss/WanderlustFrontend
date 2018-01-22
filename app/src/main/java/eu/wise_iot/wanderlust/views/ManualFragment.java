package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;

/**
 * ManualFragment:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class ManualFragment extends Fragment {

    public ManualFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manual, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backgroundImage = (ImageView) view.findViewById(R.id.background_image);
        Picasso.with(getActivity()).load(R.drawable.image_bg_welcome_screen).fit().centerCrop().into(backgroundImage);

        ImageView image1 = (ImageView) view.findViewById(R.id.image1);
//        Picasso.with(getActivity()).load(R.drawable.icon_map_feedback_positive).fit().centerCrop().into(image1);

        ImageView image2 = (ImageView) view.findViewById(R.id.image2);
//        Picasso.with(getActivity()).load(R.drawable.icon_map_feedback_negative).fit().centerCrop().into(image2);

        ImageView image3 = (ImageView) view.findViewById(R.id.image3);
//        Picasso.with(getActivity()).load(R.drawable.icon_map_feedback_alert).fit().centerCrop().into(image3);

        ImageView image4 = (ImageView) view.findViewById(R.id.image4);
//        Picasso.with(getActivity()).load(R.drawable.button_my_location).fit().centerCrop().into(image4);

        ImageView image5 = (ImageView) view.findViewById(R.id.image5);
        Picasso.with(getActivity()).load(R.drawable.ic_camera_black_24dp).fit().centerCrop().into(image5);

        Button goToMapButton = (Button) view.findViewById(R.id.go_to_manual_button);
        goToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment mapFragment = MapFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
