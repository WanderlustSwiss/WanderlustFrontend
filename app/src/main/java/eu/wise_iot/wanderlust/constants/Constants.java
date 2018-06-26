package eu.wise_iot.wanderlust.constants;

import java.util.Arrays;
import java.util.List;

/**
 * Defines all constants app-wide used
 *
 * @author Fabian Schwander
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */
public interface Constants{

    /**
     * POI TYPES
     */
    int TYPE_VIEW = 1;
    int TYPE_RESTAURANT = 2;
    int TYPE_REST_AREA = 3;
    int TYPE_FLORA_FAUNA = 4;

    /**
     * GEO OBJECT TYPES
     */
    long TYPE_SAC = -1;

    /**
     * ACTIVITIES
     */
    String MAIN_ACTIVITY = "MainActivity";
    String CAMERA_ACTIVITY = "Camera";

    /**
     * FRAGMENTS
     * Add a new fragment here, DO NOT FORGET to also add it to the pool (see below)
     */
    String MAP_FRAGMENT = "MapFragment";
    String TOUR_FRAGMENT = "TourFragment";
    String FILTER_FRAGMENT = "FilterFragment";
    String TOUROVERVIEW_FRAGMENT = "TourOverviewFragment";
    String RESULT_FILTER_FRAGMENT = "ResultFilterFragment";
    String PROFILE_FRAGMENT = "ProfileFragment";
    String LOGIN_FRAGMENT = "StartupLoginFragment";
    String WEB_LOGIN_FRAGMENT = "WebLoginFragment";
    String USER_GUIDE_FRAGMENT = "UserGuideFragment";
    String REGISTRATION_FRAGMENT = "RegistrationFragment";
    String RESET_PASSWORD_FRAGMENT = "ResetPasswordFragment";
    String PROFILE_EDIT_FRAGMENT = "ProfileEditFragment";
    String DISCLAIMER_FRAGMENT = "DisclaimerFragment";
    String PROCESSING_FRAGMENT = "ProcessingFragment";
    String BACKGROUND_FRAGMENT = "BackgroundFragment";

    /**
     * FRAGMENT POOL
     * Always add fragment also here
     * else the fragment switching in drawer menu WILL NOT WORK!
     */
    List<String> fragmentPool = Arrays.asList(
            DISCLAIMER_FRAGMENT,
            PROFILE_EDIT_FRAGMENT,
            MAP_FRAGMENT,
            PROFILE_FRAGMENT,
            TOUROVERVIEW_FRAGMENT,
            USER_GUIDE_FRAGMENT,
            TOUR_FRAGMENT,
            FILTER_FRAGMENT,
            RESULT_FILTER_FRAGMENT,
            LOGIN_FRAGMENT,
            RESET_PASSWORD_FRAGMENT,
            REGISTRATION_FRAGMENT);

    String MY_MAP_OVERLAYS = "MyMapOverlays";

    /**
     * DIALOGS
     */
    String DISPLAY_FEEDBACK_DIALOG = "DisplayFeedbackDialog";
    String EDIT_POI_DIALOG = "PoiFeedbackDialog";
    String RATE_TOUR_DIALOG = "TourRatingDialog";
    String RATE_TOUR_FILTER_DIALOG = "RateTourFilterDialog";
    String EQUIPMENT_DIALOG = "EquipmentDialog";
    String REPORT_TOUR_DIALOG = "TourReportDialog";
    String REPORT_POI_DIALOG = "PoiReportDialog";
    String CREATE_TOUR_DIALOG = "CreateTourDialog";
    String CONFIRM_DELETE_POI_DIALOG = "YesNoDialog";

    /**
     * PHOTO INTENTS
     */
    String IMAGE_FILE_NAME = "imageFileName";
    String IMAGE_PATH = "imagePath";
    String PREFERENCE_FILE_POSITIONS = "preference_file_positions";
    String LAST_MAP_CENTER_LAT = "last_map_center_lat";
    String LAST_MAP_CENTER_LON = "last_map_center_lon";
    String LAST_ZOOM_LEVEL = "last_zoom_level";
    String LAST_POS_LAT = "last_position_lat";
    String LAST_POS_LON = "last_position_lon";
    String POI_ID = "feedback_id";
    String DISPLAY_MODE = "feedback_display_mode";
    String FEEDBACK_DESCRIPTION = "feedback_description";
    String POI_IS_NEW = "isNew";


    /**
     * PREFERENCES
     */
    String PREFERENCE_MY_LOCATION_ENABLED = "buttonLocationToggler";
    String PREFERENCE_BUTTON_LOCATION_CLICKED = "buttonLocationFirstClick";
    String PREFERENCE_POITYPE_RESTAURANT_ACTIVE = "PoiTypeRestaurantActive";
    String PREFERENCE_POITYPE_FLORAFAUNA_ACTIVE = "PoiTypeFloraFaunaActive";
    String PREFERENCE_POITYPE_RESTAREA_ACTIVE = "PoiTypeRestareaActive";
    String PREFERENCE_POITYPE_VIEW_ACTIVE = "PoiTypeViewActive";
    String PREFERENCE_POI_LAYER_ACTIVE = "PoiTypeViewActive";
    String PREFERENCE_SAC_LAYER_ACTIVE = "PoiTypeViewActive";
    String PREFERENCE_PUBLICTRANSPORT_LAYER_ACTIVE = "PoiTypeViewActive";


    /**
     * INTENTS
     */
    int TAKE_PHOTO = 1;
    int REQUEST_FOR_MULTIPLE_PERMISSIONS = 2;

    /**
     * INTENTS
     */
    String CREATE_TOUR_INTENT = "gpsTrackingFinished";
    String CREATE_TOUR_TRACK = "tourTrackGeoPoints";
    String CREATE_TOUR_BUNDLE = "tourTrackBundle";
    String CREATE_TOUR_UPDATE_MYOVERLAY = "tourTrackUpdateMyOverlay";
    String CREATE_TOUR_ADDING_GEOPOINT = "tourTrackAddingGeopoint";
    String CREATE_TOUR_ADDING_GEOPOINTS = "tourTrackAddingGeopoints";
    String CREATE_TOUR_WHOLE_ROUTE_REQUIRED = "tourTrackWholeRouteRequired";
    String CREATE_TOUR_IS_NEW = "tourTrackNewTOur";

}
