package eu.wise_iot.wanderlust.controllers;

import android.app.Activity;

import org.osmdroid.util.GeoPoint;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.Old.Feedback;
import eu.wise_iot.wanderlust.models.Old.FileHandler;
import eu.wise_iot.wanderlust.models.Old.JsonParser;

//TODO move or delete, should not exist anymore

/**
 * FeedbackController:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class FeedbackController {
    private static final String TAG = "FeedbackController";
    private FileHandler fileHandler = new FileHandler("feedbacks_json.txt");
    //    private List<Feedback> feedbackList = new ArrayList<>();
    private JsonParser parser;

    public FeedbackController(Activity activity) {
        parser = new JsonParser(Feedback.class, activity, R.raw.feedbacks);
//        feedbackList =  parser.getListFromResourceFile(R.raw.feedbacks);
    }

    public void saveFeedbackInFile(GeoPoint myLocation, int displayMode, int feedbackType, String description, String imageFileName) { // todo: move to addFeedbackToList()
        String message = getFeedbackString(myLocation, displayMode, feedbackType, description, imageFileName);
        fileHandler.saveDataInFile(message);
    }

//    public List<Feedback> getFeedbackList() {
//        return feedbackList;
//    }

    private String getFeedbackString(GeoPoint myLocation, int displayMode, int feedbackType, String description, String imageFileName) { // todo: only get string from list in this method
        double lat = myLocation.getLatitude();
        double lon = myLocation.getLongitude();
        Feedback feedback = new Feedback(displayMode, feedbackType, lat, lon, imageFileName, description);

//        addFeedbackToList(feedback);
        return parser.getJsonStringFromObject(feedback) + ",\n";
    }

//    public void addFeedbackToList(Feedback feedback) { // TODO: single public method, add functionality from getFeedbackString() here
//        feedbackList.add(feedback);
//    }
}
