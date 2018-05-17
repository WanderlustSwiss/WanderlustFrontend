package eu.wise_iot.wanderlust.views;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import at.blogc.android.views.ExpandableTextView;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.EquipmentController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.PolyLineEncoder;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.controllers.WeatherController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.TourRate;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserComment;
import eu.wise_iot.wanderlust.models.DatabaseModel.Weather;
import eu.wise_iot.wanderlust.views.adapters.EquipmentRVAdapter;
import eu.wise_iot.wanderlust.views.dialog.EquipmentDialog;
import eu.wise_iot.wanderlust.views.adapters.TourCommentRVAdapter;
import eu.wise_iot.wanderlust.views.dialog.TourRatingDialog;
import eu.wise_iot.wanderlust.views.dialog.TourReportDialog;

import static eu.wise_iot.wanderlust.controllers.EventType.OK;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * TourController:
 *
 * @author Alexander Weinbeck, Rilind Gashi, Baris Demirci, Simon Kaspar
 * @license MIT
 */
public class TourFragment extends Fragment {
    private static final String TAG = "TourFragment";
    private static Tour tour;
    private static TourController tourController;
    private static EquipmentController equipmentController;
    private Context context;
    private ImageView imageViewTourImage, firstWeatherIcon, secondWeatherIcon, thirdWeatherIcon, forthWeatherIcon, fifthWeatherIcon;
    private ImageButton favButton, tourSavedButton, tourSharedButton, tourReportButton, backbutton, tourDescriptionToggler;
    private TextView selectedDay, tourRegion, tourTitle, tourExecutionDate, textViewTourDistance, textViewAscend, textViewDuration, textViewDescend, textViewDifficulty,
                     firstTimePoint, secondTimePoint, thirdTimePoint, forthTimePoint, fifthTimePoint, tourRatingInNumbers,
                     firstWeatherDegree, secondWeatherDegree, thirdWeatherDegree, forthWeatherDegree, fifthWeatherDegree;
    private ExpandableTextView tourDescriptionTextView;

    private Button goToMapButton, selectDayButton;
    //weather related controls
    private static WeatherController weatherController;
    private List<Weather> weatherList;
    private DateTime selectedDateTime;
    private LinearLayout weatherInfos;


    private ProgressBar commentProgressBar;
    private RecyclerView commentRecyclerView;
    private TextView commentPlaceholder;
    private TourCommentRVAdapter adapterComments;
    private List<UserComment> listUserComment;
    private int currentPage;

    private RatingBar tourRating;
    private boolean isFavoriteUpdate;

    private EquipmentRVAdapter adapterEquip;
    private List<Equipment> listEquipment;

    private XYPlot plot;

    private RatingBar rbTourCommentAverageRating, rbCommentUserSpecificRating;
    private TextView tvTourCommentAverageRating, tourCommentRatingCount;
    private ImageView tourCommentRating1, tourCommentRating2, tourCommentRating3, tourCommentRating4, tourCommentRating5;
    private ImageButton sendCommentButton;
    private EditText commentText;

    public TourFragment() {
        // Required empty public constructor
    }


    /**
     * Static instance constructor.
     *
     * @return Fragment: TourFragment
     */
    public static TourFragment newInstance(Tour paramTour) {

        Bundle args = new Bundle();
        TourFragment fragment = new TourFragment();
        fragment.setArguments(args);
        tour = paramTour;
        tourController = new TourController(tour);
        equipmentController = EquipmentController.getInstance();
        weatherController = WeatherController.getInstance();
        return fragment;
    }


    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        tour = tourController.getCurrentTour();
        listUserComment = new LinkedList<>();
        listEquipment = new ArrayList<>();
        currentPage = 0;
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu) {
        getActivity().invalidateOptionsMenu();
        if(menu.findItem(R.id.filterIcon) != null)
            menu.findItem(R.id.filterIcon).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_tour, container, false);
        Calendar currentCalendar = GregorianCalendar.getInstance();
        selectedDateTime = new DateTime(currentCalendar);
        selectedDateTime = DateTime.now();
        initializeControls(view);
        tourController.loadGeoData(controllerEvent -> {
            if (controllerEvent.getType() == OK) {
                tour = (Tour) controllerEvent.getModel();
                setupEquipment(tour);
                setupWeather();
                drawChart();
            }
        });

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillControls();
        setupActionListeners();
    }

    private void initializeControls(View view) {
        rbTourCommentAverageRating = (RatingBar) view.findViewById(R.id.rbTourCommentAverageRating);
        tvTourCommentAverageRating = (TextView) view.findViewById(R.id.tvTourCommentAverageRating);
        tourCommentRatingCount = (TextView) view.findViewById(R.id.tourCommentRatingCount);
        tourCommentRating1 = (ImageView) view.findViewById(R.id.tourCommentRating1);
        tourCommentRating2 = (ImageView) view.findViewById(R.id.tourCommentRating2);
        tourCommentRating3 = (ImageView) view.findViewById(R.id.tourCommentRating3);
        tourCommentRating4 = (ImageView) view.findViewById(R.id.tourCommentRating4);
        tourCommentRating5 = (ImageView) view.findViewById(R.id.tourCommentRating5);
        rbCommentUserSpecificRating = (RatingBar) view.findViewById(R.id.rbCommentUserSpecificRating);

        imageViewTourImage = (ImageView) view.findViewById(R.id.tourOVTourImage);

        favButton = (ImageButton) view.findViewById(R.id.favourite_tour_button);

        tourRegion = (TextView) view.findViewById(R.id.tour_region);
        tourTitle = (TextView) view.findViewById(R.id.tourOVTourTitle);
        tourExecutionDate = (TextView) view.findViewById(R.id.tour_execution_date);
        tourSavedButton = (ImageButton) view.findViewById(R.id.save_tour_button);
        tourSharedButton = (ImageButton) view.findViewById(R.id.share_tour_button);
        backbutton = (ImageButton) view.findViewById(R.id.tour_back_button);
        textViewTourDistance = (TextView) view.findViewById(R.id.tourOVTourDistance);
        textViewAscend = (TextView) view.findViewById(R.id.tour_ascend);
        textViewDuration = (TextView) view.findViewById(R.id.tour_duration);
        textViewDescend = (TextView) view.findViewById(R.id.tour_descend);
        textViewDifficulty = (TextView) view.findViewById(R.id.tourOVTourDifficulty);

        tourDescriptionTextView = (ExpandableTextView) view.findViewById(R.id.tour_description);
        tourDescriptionToggler = (ImageButton) view.findViewById(R.id.tour_descrition_toggler);

        goToMapButton = (Button) view.findViewById(R.id.go_to_map_button);

        tourRatingInNumbers = (TextView) view.findViewById(R.id.tour_rating_in_numbers);
        tourRating = (RatingBar) view.findViewById(R.id.tour_rating);

        plot = (XYPlot) view.findViewById(R.id.plot);

        //weather
        selectDayButton = (Button) view.findViewById(R.id.weather_date_picker_button);
        selectedDay = (TextView) view.findViewById(R.id.weather_date_and_time);
        weatherInfos = (LinearLayout) view.findViewById(R.id.weather_info);
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


        tourReportButton = (ImageButton) view.findViewById(R.id.report_tour_button);

        sendCommentButton = (ImageButton) view.findViewById(R.id.tour_comment_send);
        commentText = (EditText) view.findViewById(R.id.tour_comment_description);


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


        Drawable tourRatingDrawable = tourRating.getProgressDrawable();
        tourRatingDrawable.setColorFilter(Color.parseColor("#FFFFFF"),
                PorterDuff.Mode.SRC_ATOP);


        tourController.getRating(tour, controllerEvent -> {
            switch (controllerEvent.getType()) {
                case OK:
                    tourRating.setRating(((Integer)controllerEvent.getModel()).floatValue());
            }
        });

        //equipment section
        setupRVComments(view);
        setupRVEquipment(view);
    }
    private void setupRVEquipment(View view){
        RecyclerView rvEquipment = (RecyclerView) view.findViewById(R.id.rvEquipment);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvEquipment.setLayoutManager(horizontalLayoutManager);
        adapterEquip = new EquipmentRVAdapter(context, listEquipment);
        adapterEquip.setClickListener(this::onItemClickImages);
        rvEquipment.setAdapter(adapterEquip);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_horizontal));
        rvEquipment.addItemDecoration(itemDecorator);
    }

    private void setupRVComments(View view){
        commentProgressBar = (ProgressBar) view.findViewById(R.id.tour_comment_progressbar);
        commentRecyclerView = (RecyclerView) view.findViewById(R.id.tour_comment_recyclerview);
        commentPlaceholder = (TextView) view.findViewById(R.id.tour_comment_placeholder);

        commentRecyclerView.setPadding(5, 5, 5, 5);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        commentRecyclerView.setLayoutManager(verticalLayoutManager);
        adapterComments = new TourCommentRVAdapter(context, listUserComment, this);
        commentRecyclerView.setAdapter(adapterComments);

        updateRVComments(false);
    }
    /**
     * @param tour
     */
    private void setupEquipment(Tour tour) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd. MMMM, HH:mm");
        String dateTime = selectedDateTime.toString(formatter);
        String preText = getString(R.string.wanderung_beginn);
        String completeString = preText + " " + dateTime + " " + getString(R.string.o_clock);
        selectedDay.setText(completeString);

        Log.d("GEOPOINT-LATITUDE", String.valueOf(tour.getGeoPoints().get(0).getLatitude()));
        Log.d("GEOPOINT-LONGITUDE", String.valueOf(tour.getGeoPoints().get(0).getLongitude()));
        //TODO add recommended method for getting equipment
        //listEquipment = tourController.getEquipmentOfTour(this.tour);
        equipmentController.retrieveRecommendedEquipment(tour, selectedDateTime, controllerEvent -> {
            switch (controllerEvent.getType()) {
                case OK:
                    Log.d(TAG, "got equipment for tour");
                    TourFragment.this.listEquipment.clear();
                    TourFragment.this.listEquipment.addAll((List<Equipment>) controllerEvent.getModel());
                    getActivity().runOnUiThread(() -> adapterEquip.notifyDataSetChanged());
                    break;
                default:
                    Log.d(TAG, "failure getting equipment for tour");
                    break;
            }
        });
    }

    /**
     *
     */
    private void fillControls() {
        List<File> images = tourController.getImages();
        Log.d("Debug", "Images size:" + images.size());
        if (!images.isEmpty() && images.get(0).length() != 0) {
            Picasso.with(context)
                    .load(images.get(0))
                    .fit()
                    .centerCrop()
                    .into(this.imageViewTourImage);
        } else {
            Picasso.with(context)
                    .load(R.drawable.no_image_found)
                    .fit().centerCrop().placeholder(R.drawable.progress_animation)
                    .into(this.imageViewTourImage);
        }

        if (tourController.isFavorite()) {
            favButton.setImageResource(R.drawable.ic_favorite_red_24dp);
        } else {
            favButton.setImageResource(R.drawable.ic_favorite_white_24dp);
        }
        tourRegion.setText(getString(R.string.tour_region) + " " + tourController.getRegion());

        if(tourController.isSaved()){
            tourSavedButton.setColorFilter(ContextCompat.getColor(context, R.color.medium));
        }else{
            tourSavedButton.setColorFilter(ContextCompat.getColor(context, R.color.white));
        }

        // TODO: add tour region here
//        tourRegion.setText("Region <Namen>");

        tourTitle.setText(tourController.getTitle());

        tourController.getTourRating(controllerEvent -> {
           if (controllerEvent.getType() == EventType.OK){
               updateRating((TourRate) controllerEvent.getModel());
           }else{
               updateRating(null);
           }
        });

        tourDescriptionTextView.setText(tourController.getDescription());

        tourExecutionDate.setText(tourController.getCreatedAtString());

        textViewTourDistance.setText(tourController.getDistanceString());
        textViewDuration.setText(tourController.getDurationString());

        textViewAscend.setText(String.valueOf(tourController.getAscent()) + "m");
        textViewDescend.setText(String.valueOf(tourController.getDescent()) + "m");

        textViewDifficulty.setText(tourController.getDifficultyMark());
    }

    /**
     *
     */
    private void setupActionListeners() {
        tourSharedButton.setOnClickListener((View v) -> shareTour());
        tourReportButton.setOnClickListener((View v) -> reportTour());
        goToMapButton.setOnClickListener((View v) -> showMapWithTour());
        backbutton.setOnClickListener((View v) -> getFragmentManager().popBackStack());
        favButton.setOnClickListener((View v) -> toggleFavorite());
        tourSavedButton.setOnClickListener((View v) -> toggleSaved());
        sendCommentButton.setOnClickListener((View v) -> createComment());
        tourRating.setOnTouchListener((View v, MotionEvent e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                if (tourController.alreadyRated(tour.getTour_id()) == 0L) {
                    TourRatingDialog dialog = new TourRatingDialog().newInstance(tour, tourController,this);
                    dialog.show(getFragmentManager(), Constants.RATE_TOUR_DIALOG);
                } else {
                    Toast.makeText(context, R.string.already_rated, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
        setupDateAndTimeDialogs();
        setupExpandableTextView(tourDescriptionTextView, tourDescriptionToggler);
    }

    /**
     * Takes an ExpandableTextView and a ImageButton and handles creates the behaviour.
     *
     * @param textView ExpandableTextView
     * @param toggler  ImageButton
     */
    private void setupExpandableTextView(ExpandableTextView textView, ImageButton toggler) {
        textView.setInterpolator(new OvershootInterpolator());

        textView.post(() -> {
            if(textView.getLineCount() < textView.getMaxLines()){
                toggler.setVisibility(View.GONE);
            } else {
                toggler.setOnClickListener(v -> {
                    Drawable arrowDown = getActivity().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    Drawable arrowUp = getActivity().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp);

                    if (textView.isExpanded()) {
                        toggler.setBackground(arrowDown);
                    } else {
                        toggler.setBackground(arrowUp);
                    }
                    textView.toggle();
                });
            }
        });
    }
    public void deleteComment(UserComment userComment){
        tourController.deleteComment(userComment, event -> {
            if (event.getType() == EventType.OK){
                listUserComment.remove(userComment);
                adapterComments.notifyDataSetChanged();
            }else{
                Toast.makeText(this.context,getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT);
            }
        });
    }
    private void createComment(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        tourController.createComment(commentText.getText().toString(), (controllerEvent) -> {
            Toast.makeText(this.context,getResources().getText(R.string.msg_comment_create_successfull), Toast.LENGTH_SHORT);
            updateRVComments(true);
        });
    }
    private void updateRVComments(boolean doClear){
        tourController.getComments(0, event -> {
            switch (event.getType()) {
                case OK:
                    commentText.getText().clear();
                    List<UserComment> list = (List<UserComment>) event.getModel();
                    currentPage++;
                    if (doClear) listUserComment.clear();
                    listUserComment.addAll(list);
                    adapterComments.notifyDataSetChanged();
                    if(adapterComments.getItemCount() > 0) {
                        commentRecyclerView.setVisibility(View.VISIBLE);
                        commentPlaceholder.setVisibility(View.GONE);
                        commentProgressBar.setVisibility(View.GONE);
                    } else {
                        commentRecyclerView.setVisibility(View.GONE);
                        commentPlaceholder.setVisibility(View.VISIBLE);
                        commentProgressBar.setVisibility(View.GONE);
                    }
                    break;
                case NOT_FOUND:
                    commentRecyclerView.setVisibility(View.GONE);
                    commentPlaceholder.setVisibility(View.VISIBLE);
                    commentProgressBar.setVisibility(View.GONE);
                    break;
                default:
                    Log.d(TAG, "Server response ERROR: " + event.getType().name());
                    Toast.makeText(this.context,getResources().getText(R.string.msg_no_internet), Toast.LENGTH_SHORT);
            }
        });
    }
    /**
     * Method sets up listeners for TimePickerDialog and DatePickerDialog which are needed to
     * save selected DateTime to aquire the weather objects to the specific route and date/time
     */
    private void setupDateAndTimeDialogs() {
        //time picker listener, which triggers weather service
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            selectedDateTime = selectedDateTime.withTime(hourOfDay, minute, 0, 0);
            weatherList = null;
            setupEquipment(tour);
            setupWeather();
        };

        //date picker listener, which triggers time picker
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDateTime = selectedDateTime.withDate(year, month + 1, dayOfMonth);

                TimePickerDialog tdialog = new TimePickerDialog(context, timeSetListener,
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

            //select calendar language as local language
            Locale locale = getResources().getConfiguration().locale;
            Locale.setDefault(locale);

            DatePickerDialog dialog = new DatePickerDialog(context, dateSetListener,
                    year, month, today);

            dialog.getDatePicker().setFirstDayOfWeek(1);
            dialog.getDatePicker().setMinDate(date.getTimeInMillis());
            date.add(Calendar.DAY_OF_MONTH, 5);
            dialog.getDatePicker().setMaxDate(date.getTimeInMillis());
            dialog.show();
        });
    }
    private void setupWeather() {

        weatherController.getWeatherFromTour(tour, selectedDateTime, controllerEvent -> {
            switch (controllerEvent.getType()) {
                case OK:
                    weatherList = (List<Weather>) controllerEvent.getModel();

                    getActivity().runOnUiThread(() -> {
                        if (weatherList != null) {
                            weatherInfos.setVisibility(View.VISIBLE);
                            initializeWeather();
                        } else {
                            Toast.makeText(context, R.string.keine_wetterdaten, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                default:
                    Log.d("WETTER", "service problem");
                    break;
            }
        });

    }

    /**
     * Method fills controlls in weather-info area of tour fragment with the required data, for Example
     * the degrees, the icon's and the time points of the routes.
     */
    private void initializeWeather() {
        List<ImageView> weatherIcons = new ArrayList<>();
        weatherIcons.add(firstWeatherIcon);
        weatherIcons.add(secondWeatherIcon);
        weatherIcons.add(thirdWeatherIcon);
        weatherIcons.add(forthWeatherIcon);
        weatherIcons.add(fifthWeatherIcon);

        List<TextView> weatherDegrees = new ArrayList<>();
        weatherDegrees.add(firstWeatherDegree);
        weatherDegrees.add(secondWeatherDegree);
        weatherDegrees.add(thirdWeatherDegree);
        weatherDegrees.add(forthWeatherDegree);
        weatherDegrees.add(fifthWeatherDegree);

        List<TextView> timePoints = new ArrayList<>();
        timePoints.add(firstTimePoint);
        timePoints.add(secondTimePoint);
        timePoints.add(thirdTimePoint);
        timePoints.add(forthTimePoint);
        timePoints.add(fifthTimePoint);

        timePoints.get(0).setText(R.string.start);
        timePoints.get(4).setText(R.string.end);

        if (weatherList.size() <= 5) {
            for (int i = 0; i < weatherList.size(); ++i) {
                Weather weather = weatherList.get(i);

                //set temperature
                String temp = String.format(Locale.GERMAN, "%d", (int) weather.getTemp());
                String degreeString = temp + getString(R.string.temperature_abbrevation);
                weatherDegrees.get(i).setText(degreeString);

                //set time of tour
                if (i > 0 && i < 4) {
                    String time = tourController.getDurationStringSpecificPoint(i + 1);
                    timePoints.get(i).setText(time);
                }

                //set icon
                String icon = weather.getIcon();
                switch (icon) {
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
                        weatherIcons.get(i).setImageResource(R.drawable.ic_clouds);
                        break;
                }
            }
        }
    }

    /**
     *
     */
    private void toggleFavorite() {
        if (isFavoriteUpdate) {
            return;
        }
        if (tourController.isFavorite() && !isFavoriteUpdate) {
            isFavoriteUpdate = true;
            tourController.unsetFavorite(controllerEvent -> {
                favButton.setImageResource(R.drawable.ic_favorite_white_24dp);
                isFavoriteUpdate = false;
            });
        } else {
            isFavoriteUpdate = true;
            tourController.setFavorite(controllerEvent -> {
                favButton.setImageResource(R.drawable.ic_favorite_red_24dp);
                isFavoriteUpdate = false;
            });
        }
    }

    private void toggleSaved(){
        if (tourController.isSaved()){
            tourController.unsetSaved(context, controllerEvent -> {
                switch (controllerEvent.getType()){
                    case OK:
                        tourSavedButton.setColorFilter(ContextCompat.getColor(context, R.color.heading_icon_unselected));
                        break;
                    default:
                }
            });
        }else{
            tourController.setSaved(context , controllerEvent -> {
                switch (controllerEvent.getType()){
                    case OK:
                        tourSavedButton.setColorFilter(ContextCompat.getColor(context, R.color.medium));
                        break;
                    default:
                        Toast.makeText(context, R.string.connection_fail, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void updateRating(TourRate tourRate){
        if (tourRate != null){
            ImageView[] tourCommentRatings = {tourCommentRating1, tourCommentRating2,
                    tourCommentRating3, tourCommentRating4, tourCommentRating5};
            final float scale = context.getResources().getDisplayMetrics().density;
            float rateAvgRound = Float.parseFloat(String.format("%.1f", Math.round(tourRate.getRateAvg() * 2) / 2.0));

            float deltaDPSize = 100 / tourRate.getRateTotal();
            int c = 0;
            for (Integer rateValue : tourRate.getRateByValue()){
                int width = Math.round(rateValue * deltaDPSize);
                tourCommentRatings[c++].getLayoutParams().width = (int) (width * scale + 0.5f);
            }
            //refresh layout
            tourCommentRating1.requestLayout();
            tourCommentRating2.requestLayout();
            tourCommentRating3.requestLayout();
            tourCommentRating4.requestLayout();
            tourCommentRating5.requestLayout();

            rbCommentUserSpecificRating.setRating(tourRate.getUserRate());
            rbTourCommentAverageRating.setRating(tourRate.getRateAvg());

            String tourComment;
            int ratingCount = tourRate.getRateTotal();
            if(ratingCount == 0) {
                tourComment = getResources().getString(R.string.tour_comment_no_ratings);
            } else if (ratingCount == 1) {
                tourComment = ratingCount + " " + getResources().getString(R.string.tour_comment_rating);
            } else {
                tourComment = ratingCount + " " + getResources().getString(R.string.tour_comment_ratings);
            }
            tourCommentRatingCount.setText(tourComment);

            tvTourCommentAverageRating.setText(String.valueOf(rateAvgRound));
            tourRatingInNumbers.setText(String.valueOf(rateAvgRound));
            tourRating.setRating(rateAvgRound);
        }else{
            tvTourCommentAverageRating.setText("0.0");
            tvTourCommentAverageRating.setText("0.0");
            tourRatingInNumbers.setText("0");
        }
    }
    public void drawChart() {
        Number[] domainLabels = tourController.getElevationProfileXAxis();
        Number[] rangeLabels = tourController.getElevationProfileYAxis();

        float minElevation = Integer.MAX_VALUE;
        float maxElevation = Integer.MIN_VALUE;

        for (Number elev : rangeLabels) {
            if (elev.floatValue() < minElevation) {
                minElevation = elev.floatValue();
            }
            if (elev.floatValue() > maxElevation) {
                maxElevation = elev.floatValue();
            }
        }

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(rangeLabels), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(getResources().getColor(R.color.black), null, getResources().getColor(R.color.heading_icon), null);


        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(5, CatmullRomInterpolator.Type.Centripetal));


        // add a new series' to the xyplot:
        plot.clear();
        plot.addSeries(series1, series1Format);

        // setting labels of x-axis
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i].intValue() + "km");
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        // setting labels of y-axis
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {

            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                int i = Math.round(((Number) obj).intValue());
                return toAppendTo.append(i);
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });

        plot.getLegend().setVisible(false);
        float baseLine = ((minElevation - (minElevation % 100)) - 100) < 0 ? 0 : minElevation - (minElevation % 100) - 100;
        plot.setRangeLowerBoundary(baseLine, BoundaryMode.FIXED);
        plot.setRangeUpperBoundary((maxElevation - (maxElevation % 100)) + 100, BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 100);
        //PanZoom.attach(plot, PanZoom.Pan.HORIZONTAL, PanZoom.Zoom.STRETCH_HORIZONTAL);

        plot.redraw();
    }

    public void showMapWithTour() {
        //handle recent tours
        tourController.addRecentTour(this.tour);

        if (tourController.getPolyline() == null) {
            return;
        }
        Log.d(TAG, tourController.getPolyline());
        ArrayList<GeoPoint> polyList = PolyLineEncoder.decode(tourController.getPolyline(), 10);
        Road road = new Road(polyList);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

        roadOverlay.setColor(getResources().getColor(R.color.highlight_main_transparent));


        //Disable my location
        getActivity().getPreferences(Context.MODE_PRIVATE).edit().putBoolean(Constants.PREFERENCE_MY_LOCATION_ENABLED, false).apply();

        MapFragment mapFragment = MapFragment.newInstance(roadOverlay);
        Fragment oldMapFragment = getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT);
        if(oldMapFragment != null) {
            getFragmentManager().beginTransaction()
                    .remove(oldMapFragment)
                    .commit();
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                .addToBackStack(Constants.MAP_FRAGMENT)
                .commit();

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }

    /**
     * shares the tour with other apps
     */
    private void shareTour(){
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        String description = tour.getDescription() + getResources().getString(R.string.app_domain);
        shareIntent.putExtra(Intent.EXTRA_TEXT, description);
        shareIntent.putExtra(Intent.EXTRA_TITLE, tour.getTitle());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, tour.getTitle());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title_tour)));
    }

    private void reportTour(){
        TourReportDialog dialog = new TourReportDialog().newInstance(tour, tourController);
        dialog.show(getFragmentManager(), Constants.REPORT_TOUR_DIALOG);
    }
    /**
     * handles click in Recyclerview of equipment
     *
     * @param view
     * @param parEquipment
     */
    private void onItemClickImages(View view, Equipment parEquipment) {
        EquipmentDialog equipmentDialog = new EquipmentDialog().newInstance(context, parEquipment);
        equipmentDialog.show(getFragmentManager(), Constants.EQUIPMENT_DIALOG);
    }
}
