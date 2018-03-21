package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.squareup.picasso.Picasso;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.PolyLineEncoder;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;

/**
 * TourController:
 *
 * @author Alexander Weinbeck, Rilind Gashi, Simon Kaspar
 * @license MIT
 */
public class TourFragment extends Fragment {
    private static final String TAG = "TourOverviewFragment";
    private Tour tour;
    private static TourController tourController;
    private Context context;
    private static Polyline polyline;

    private ImageView imageViewTourImage;
    private ImageButton favButton;
    private TextView tourRegion;
    private TextView tourTitle;
    private ImageButton tourSavedButton;
    private ImageButton tourSharedButton;
    private TextView textViewTourDistance;
    private TextView textViewAscend;
    private TextView textViewDuration;
    private TextView textViewDescend;
    private TextView textViewDifficulty;
    private TextView textViewDescription;
    private Button jumpToStartLocationButton;
    private static MapFragment mapFragment;

    private Favorite favorite;
    private boolean isFavoriteUpdate;
    private int[] highProfile;

    private XYPlot plot;

    public TourFragment() {
        // Required empty public constructor
    }


    /**
     * Static instance constructor.
     *
     * @return Fragment: TourFragment
     */
    public static TourFragment newInstance(Tour tour) {

        Bundle args = new Bundle();
        TourFragment fragment = new TourFragment();
        mapFragment = new MapFragment();
        fragment.setArguments(args);
        tourController = new TourController(tour);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour, container, false);
        initializeControls(view);
        fillControls();
        setupActionListeners();
        tourController.loadGeoData(new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                if (controllerEvent.getType() == EventType.OK){
                    drawChart();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void initializeControls(View view){
        imageViewTourImage = (ImageView) view.findViewById(R.id.tourImage);
        favButton = (ImageButton) view.findViewById(R.id.favButton);

        tourRegion = (TextView) view.findViewById(R.id.tourRegion);
        tourTitle = (TextView) view.findViewById(R.id.tourTitle);
        tourSavedButton = (ImageButton) view.findViewById(R.id.tourSaved);
        tourSharedButton = (ImageButton) view.findViewById(R.id.tourShared);
        textViewTourDistance = (TextView) view.findViewById(R.id.tourDistance);
        textViewAscend = (TextView) view.findViewById(R.id.tourAscend);
        textViewDuration = (TextView) view.findViewById(R.id.tourDuration);
        textViewDescend = (TextView) view.findViewById(R.id.tourDescend);
        textViewDifficulty = (TextView) view.findViewById(R.id.tourDifficulty);
        textViewDescription = (TextView) view.findViewById(R.id.tourDescription);
        jumpToStartLocationButton = (Button) view.findViewById(R.id.jumpToStartLocationButton);

        plot = (XYPlot) view.findViewById(R.id.plot);

        long difficulty = tourController.getLevel();
        Drawable drawable;
        if (difficulty >= 6)
            drawable = context.getResources().getDrawable(R.drawable.t6);
        else if (difficulty >= 4)
            drawable = context.getResources().getDrawable(R.drawable.t4_t5);
        else if (difficulty >= 2)
            drawable = context.getResources().getDrawable(R.drawable.t2_t3);
        else
            drawable = context.getResources().getDrawable(R.drawable.t1);
        textViewDifficulty.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }
    public void fillControls() {
        List<File> images = tourController.getImages();
        Log.d("Debug", "Images size:" + images.size());
        if (!images.isEmpty() && images.get(0).length() != 0){
            Picasso.with(context)
                    .load(images.get(0))
                    .into(this.imageViewTourImage);
        }else{
            Picasso.with(context)
                    .load(R.drawable.no_image_found)
                    .into(this.imageViewTourImage);
        }

        if (tourController.isFavorite()) {
            favButton.setImageResource(R.drawable.ic_favorite_red_24dp);
        } else {
            favButton.setImageResource(R.drawable.ic_favorite_white_24dp);
        }

        tourRegion.setText("");
        tourTitle.setText(tourController.getTitle());
        textViewDescription.setText(tourController.getDescription());

        textViewTourDistance.setText(tourController.getDistanceString());
        textViewDuration.setText(tourController.getDurationString());

        textViewAscend.setText(String.valueOf(tourController.getAscent()) + "m");
        textViewDescend.setText(String.valueOf(tourController.getDescent()) + "m");

        textViewDifficulty.setText(tourController.getDifficultyMark());
    }
    public void setupActionListeners(){
        jumpToStartLocationButton.setOnClickListener((View v) -> showMapWithTour());
        favButton.setOnClickListener((View v) -> toggleFavorite());
    }
    public void toggleFavorite() {
        if (isFavoriteUpdate){
            return;
        }
        if (tourController.isFavorite() && !isFavoriteUpdate) {
            isFavoriteUpdate = true;
            tourController.unsetFavorite(new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    favButton.setImageResource(R.drawable.ic_favorite_white_24dp);
                    isFavoriteUpdate = false;
                }
            });
        }else{
            isFavoriteUpdate = true;
            tourController.setFavorite(new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    favButton.setImageResource(R.drawable.ic_favorite_red_24dp);
                    isFavoriteUpdate = false;
                }
            });
        }
    }
    public void drawChart(){
        Number[] domainLabels = tourController.getElevationProfileXAxis();
        Number[] series1Numbers = tourController.getElevationProfileYAxis();

        float minElevation = Float.MAX_VALUE;
        float maxElevation = Float.MIN_VALUE;

        for (Number elev : series1Numbers){
            if (elev.floatValue() < minElevation){
                minElevation = elev.floatValue();
            }
            if (elev.floatValue() > maxElevation){
                maxElevation = elev.floatValue();
            }
        }

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(Color.DKGRAY, null, Color.LTGRAY, null);


        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(5, CatmullRomInterpolator.Type.Centripetal));


        // add a new series' to the xyplot:
        plot.clear();
        plot.addSeries(series1, series1Format);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        plot.getLegend().setVisible(false);
        float baseLine = ((minElevation - (minElevation % 100)) - 200.0f) < 0.0f ? 0.0f : minElevation - (minElevation % 100) - 200.0f;
        plot.setRangeLowerBoundary(baseLine, BoundaryMode.FIXED);
        plot.setRangeUpperBoundary((maxElevation - (maxElevation % 100)) + 100.0f, BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 100);
        //PanZoom.attach(plot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.STRETCH_HORIZONTAL);
        plot.redraw();
    }
    public void showMapWithTour() {
        if (tourController.getPolyline() == null){
            return;
        }
        ArrayList<GeoPoint> polyList = PolyLineEncoder.decode(tourController.getPolyline(), 10);
        Road road = new Road(polyList);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        roadOverlay.setColor(getResources().getColor(R.color.highlight_main_transparent75));
        MapFragment mapFragment = MapFragment.newInstance(roadOverlay);

        getFragmentManager().beginTransaction()
                .add(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                .commit();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }


}
