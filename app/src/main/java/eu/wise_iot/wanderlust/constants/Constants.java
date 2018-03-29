package eu.wise_iot.wanderlust.constants;

/**
 * Constants:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public interface Constants {

    /* DISPLAY MODE */
    boolean MODE_PUBLIC = true;
    boolean MODE_PRIVATE = false;

    // new types
    int TYPE_VIEW = 0;
    int TYPE_RESTAURANT = 1;
    int TYPE_REST_AREA = 2;
    int TYPE_FLORA_FAUNA = 3;

    // GeoObjectTypes
    long TYPE_SAC = -1;


    /* ACTIVITIES */
    String MAIN_ACTIVITY = "MainActivity";

    /* FRAGMENTS */
    String MAP_FRAGMENT = "MapFragment";
    String TOUR_FRAGMENT = "TourFragment";
    String TOUROVERVIEW_FRAGMENT = "TourOverviewFragment";
    String PROFILE_FRAGMENT = "ProfileFragment";
    String LOGIN_FRAGMENT = "SartupLoginFragment";
    String USER_GUIDE_FRAGMENT = "UserGuideFragment";

    String MY_MAP_OVERLAYS = "MyMapOverlays";
    String CAMERA_ACTIVITY = "Camera";
    String DISPLAY_FEEDBACK_DIALOG = "DisplayFeedbackDialog";
    String EDIT_POI_DIALOG = "PoiFeedbackDialog";
    String RATE_TOUR_DIALOG = "TourRatingDialog";
    String CONFIRM_DELETE_POI_DIALOG = "YesNoDialog";

    /* PHOTO INTENT */
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

    /* TOUR */
    String CLICKED_TOUR = "clicked_tour";

    /* PREFERENCES */
    String MY_LOCATION_ENABLED = "buttonLocationToggler";
    String BUTTON_LOCATION_CLICKED = "buttonLocationFirstClick";

    /* INTENTS */
    int TAKE_PHOTO = 1;
    int REQUEST_FOR_MULTIPLE_PERMISSIONS = 2;
}
