package eu.wise_iot.wanderlust.views;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.PolyLineEncoder;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.controllers.WeatherController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Weather;

/**
 * TourController:
 *
 * @author Alexander Weinbeck, Rilind Gashi, Baris Demirci
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

    //weather related controlls
    private static WeatherController weatherController;
    private List<Weather> weatherList;
    private Button selectDayButton;
    private TextView selectedDay;
    private DateTime selectedDateTime;
    private LinearLayout weatherInfos;

    private List<ImageView> weatherIcons;
    private ImageView firstWeatherIcon;
    private ImageView secondWeatherIcon;
    private ImageView thirdWeatherIcon;
    private ImageView forthWeatherIcon;
    private ImageView fifthWeatherIcon;

    private List<TextView> weatherDegrees;
    private TextView firstWeatherDegree;
    private TextView secondWeatherDegree;
    private TextView thirdWeatherDegree;
    private TextView forthWeatherDegree;
    private TextView fifthWeatherDegree;

    private List<TextView> timePoints;
    private TextView firstTimePoint;
    private TextView secondTimePoint;
    private TextView thirdTimePoint;
    private TextView forthTimePoint;
    private TextView fifthTimePoint;

    private Favorite favorite;
    private boolean isFavoriteUpdate;
    private int[] highProfile;

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
        weatherController = WeatherController.getInstance();
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        tour = tourController.getCurrentTour();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tour, container, false);
        initializeControls(view);
        fillControls();
        setupActionListeners();
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

        //weather
        selectDayButton = (Button) view.findViewById(R.id.datepickerButton);
        selectedDay = (TextView) view.findViewById(R.id.selectedDateTime);
        weatherInfos = (LinearLayout) view.findViewById(R.id.weatherInfo);
        firstWeatherIcon = (ImageView) view.findViewById(R.id.firstPointIcon);
        secondWeatherIcon = (ImageView) view.findViewById(R.id.secondPointIcon);
        thirdWeatherIcon = (ImageView) view.findViewById(R.id.thirdPointIcon);
        forthWeatherIcon = (ImageView) view.findViewById(R.id.forthPointIcon);
        fifthWeatherIcon = (ImageView) view.findViewById(R.id.fifthPointIcon);
        firstWeatherDegree = (TextView) view.findViewById(R.id.degreeFirstPoint);
        secondWeatherDegree = (TextView) view.findViewById(R.id.degreeSecondPoint);
        thirdWeatherDegree = (TextView) view.findViewById(R.id.degreeThirdPoint);
        forthWeatherDegree = (TextView) view.findViewById(R.id.degreeForthPoint);
        fifthWeatherDegree = (TextView) view.findViewById(R.id.degreeFifthPoint);
        firstTimePoint = (TextView) view.findViewById(R.id.timeFirstPoint);
        secondTimePoint = (TextView) view.findViewById(R.id.timeSecondPoint);
        thirdTimePoint = (TextView) view.findViewById(R.id.timeThirdPoint);
        forthTimePoint = (TextView) view.findViewById(R.id.timeForthPoint);
        fifthTimePoint = (TextView) view.findViewById(R.id.timeFifthPoint);
        selectedDateTime = DateTime.now();

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

        try {
            highProfile = tourController.getHighProfile();
        } catch (IOException e) {
            e.printStackTrace();
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

        setupDateAndTimeDialogs();
    }

    private void setupDateAndTimeDialogs(){
        //time picker listener, which triggers weather service
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedDateTime = selectedDateTime.withTime(hourOfDay, minute, 0, 0);

                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd. MMM HH:mm");
                String dateTime = selectedDateTime.toString(formatter);
                String preText = getString(R.string.wanderung_beginn);
                selectedDay.setText(preText + " " + dateTime);

                Log.d("GEOPOINT-LATITUDE", String.valueOf(tour.getGeoPoints().get(0).getLatitude()));
                Log.d("GEOPOINT-LONGITUDE", String.valueOf(tour.getGeoPoints().get(0).getLongitude()));

                weatherList = null;

                weatherController.getWeatherFromTour(tour, selectedDateTime, new FragmentHandler() {
                    @Override
                    public void onResponse(ControllerEvent controllerEvent) {
                        switch (controllerEvent.getType()){
                            case OK:
                                weatherList = (List<Weather>) controllerEvent.getModel();

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(weatherList != null){
                                            weatherInfos.setVisibility(View.VISIBLE);
                                            initializeWeather();
                                        }else{
                                            Toast.makeText(context, R.string.keine_wetterdaten, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                break;
                            default:
                                Log.d("WETTER", "service problem");
                                break;
                        }
                    }
                });

            }
        };

        //date picker listener, which triggers time picker
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDateTime = selectedDateTime.withDate(year, month, dayOfMonth);

                TimePickerDialog tdialog = new TimePickerDialog(context ,timeSetListener,
                                                            0, 0, true);
                tdialog.show();
            }
        };

        //button click listener to select day, which triggers date picker
        selectDayButton.setOnClickListener(e -> {
            Calendar date = Calendar.getInstance();
            int today = date.get(Calendar.DAY_OF_MONTH);
            int month = date.get(Calendar.MONTH);
            int year = date.get(Calendar.YEAR);

            DatePickerDialog dialog = new DatePickerDialog(context, dateSetListener,
                    year, month, today);

            dialog.getDatePicker().setMinDate(date.getTimeInMillis());
            date.add(Calendar.DAY_OF_MONTH, 5);
            dialog.getDatePicker().setMaxDate(date.getTimeInMillis());
            dialog.show();
        });
    }

    private void initializeWeather(){

        weatherIcons = new ArrayList<>();
        weatherIcons.add(firstWeatherIcon);
        weatherIcons.add(secondWeatherIcon);
        weatherIcons.add(thirdWeatherIcon);
        weatherIcons.add(forthWeatherIcon);
        weatherIcons.add(fifthWeatherIcon);

        weatherDegrees = new ArrayList<>();
        weatherDegrees.add(firstWeatherDegree);
        weatherDegrees.add(secondWeatherDegree);
        weatherDegrees.add(thirdWeatherDegree);
        weatherDegrees.add(forthWeatherDegree);
        weatherDegrees.add(fifthWeatherDegree);

        timePoints = new ArrayList<>();
        timePoints.add(firstTimePoint);
        timePoints.add(secondTimePoint);
        timePoints.add(thirdTimePoint);
        timePoints.add(forthTimePoint);
        timePoints.add(fifthTimePoint);

        timePoints.get(0).setText("Start");
        timePoints.get(4).setText("Ende");

        if(weatherList.size() <= 5){
            for(int i = 0; i < weatherList.size(); ++i){
                Weather weather = weatherList.get(i);

                //set temperature
                String temp = String.format(Locale.GERMAN, "%d", (int) weather.getTemp());
                weatherDegrees.get(i).setText(temp + "°C");

                //set time of tour
                if(i > 0 && i < 4){
                    String time = tourController.getDurationStringSpecificPoint(i+1);
                    timePoints.get(i).setText(time);
                }


                //set icon
                String icon = weather.getIcon();
                switch (icon){
                    case "01d":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_clear_sky_day);
                        break;
                    case "01n":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_clear_sky_night);
                        break;
                    case "02d":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_few_clouds_day);
                        break;
                    case "02n":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_few_clouds_night);
                        break;
                    case "03d":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_clouds);
                        break;
                    case "03n":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_clouds);
                        break;
                    case "04d":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_clouds);
                        break;
                    case "04n":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_clouds);
                        break;
                    case "09d":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_shower_rain);
                        break;
                    case "09n":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_shower_rain);
                        break;
                    case "10d":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_rain_day);
                        break;
                    case "10n":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_rain_night);
                        break;
                    case "11d":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_storm);
                        break;
                    case "11n":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_storm);
                        break;
                    case "13d":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_snow);
                        break;
                    case "13n":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_snow);
                        break;
                    case "50d":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_mist);
                        break;
                    case "50n":
                        weatherIcons.get(i).setImageResource(R.drawable.ic_mist);
                        break;
                    default:
                        weatherIcons.get(i).setImageResource(R.drawable.ic_clear_sky_night);
                        break;
                }
            }
        }
    }
    public void toggleFavorite() {

        //tests
 /*       WeatherController weatherController = WeatherController.getInstance();
        List<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(new GeoPoint(47.3768866, 8.541694, 0));
        geoPoints.add(new GeoPoint(47.3768866, 8.541694, 0));
        geoPoints.add(new GeoPoint(47.3768866, 8.541694, 0));
        geoPoints.add(new GeoPoint(47.3768866, 8.541694, 0));
        geoPoints.add(new GeoPoint(47.3768866, 8.541694, 0));
        weatherController.getWeatherFromGeoPointList(geoPoints, new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                controllerEvent.getModel();
            }
        });*/
//        weatherController.getWeatherFromGeoPoint(new GeoPoint(47.3768866, 8.541694, 0), new FragmentHandler() {
//            @Override
//            public void onResponse(ControllerEvent controllerEvent) {
//
//                controllerEvent.getModel();
//            }
//        });



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

    public void showMapWithTour() {
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
