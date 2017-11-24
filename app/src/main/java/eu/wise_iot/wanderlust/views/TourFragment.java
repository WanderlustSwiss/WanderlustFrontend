package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.Old.JsonParser;
import eu.wise_iot.wanderlust.models.Old.Tour;
import eu.wise_iot.wanderlust.constants.Constants;

/**
 * TourFragment:
 * @author Fabian Schwander
 * @license MIT
 */
public class TourFragment extends Fragment {
    private static final String TAG = "TourFragment";
    private Tour tour;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTourModel();
        context = getActivity().getApplicationContext();
        tour = initTourModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tour, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView nameView = (TextView) view.findViewById(R.id.tour_title);
        nameView.setText(tour.getName());

        TextView difficultyView = (TextView) view.findViewById(R.id.difficulty_view);
        difficultyView.setText(tour.getDifficultyWithExplainingText());

        ImageView teaserImageView = (ImageView) view.findViewById(R.id.teaser_image);
        int imageId = context.getResources().getIdentifier(tour.getTeaserImageWithoutSuffix(), "drawable", context.getPackageName());
        Picasso.with(context).load(imageId).fit().into(teaserImageView);

        TextView durationView = (TextView) view.findViewById(R.id.duration_view);
        durationView.setText(tour.getDuration());

        TextView distanceUpView = (TextView) view.findViewById(R.id.distanzeUp_view);
        distanceUpView.setText(tour.getDistanceUpInMeters());

        TextView distanceDownView = (TextView) view.findViewById(R.id.distanzeDown_view);
        distanceDownView.setText(tour.getDistanceDownInMeters());

        TextView trackSegmentView = (TextView) view.findViewById(R.id.track_segment_view);
        trackSegmentView.setText(tour.getTrackSegment());

        TextView descriptionView = (TextView) view.findViewById(R.id.description_view);
        descriptionView.setText(tour.getDescription());

        TextView linkSourceView = (TextView) view.findViewById(R.id.link_source_view);
        linkSourceView.setText(tour.getLinkSource());
        linkSourceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = tour.getLinkSource();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // todo: add on click listener here and open MapFragment on specified start location
        Button goToStartLocationButton = (Button) view.findViewById(R.id.go_to_start_location_button);
        goToStartLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, R.string.msg_no_action_defined_on_go_to_map_button, Toast.LENGTH_LONG).show();
                getActivity().getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, MapFragment.newInstance())
                        .commit();
            }
        });

    }

    private Tour initTourModel() {
        JsonParser<Tour> parser = new JsonParser<>(Tour.class, context);
        String transmittedTourDataAsJsonString = getArguments().getString(Constants.CLICKED_TOUR);
        Tour tour = parser.getObjectFromJsonString(transmittedTourDataAsJsonString);
        return tour;
    }
}
