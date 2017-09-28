package eu.wise_iot.wanderlust.controller;

import android.app.Activity;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.model.Feedback;
import eu.wise_iot.wanderlust.utils.MyFileManager;
import eu.wise_iot.wanderlust.utils.MyJsonParser;

/**
 * Created by fabianschwander on 17.08.17.
 */

public class FeedbackController {
    private static final String TAG = "FeedbackController";
    private MyFileManager myFileManager = new MyFileManager("feedbacks_json.txt");
//    private List<Feedback> feedbackList = new ArrayList<>();
    private MyJsonParser parser;

    public FeedbackController(Activity activity) {
        parser = new MyJsonParser(Feedback.class, activity, R.raw.feedbacks);
//        feedbackList =  parser.getListFromResourceFile(R.raw.feedbacks);
    }

    public void saveFeedbackInFile(GeoPoint myLocation, int displayMode, int feedbackType,  String description, String imageFileName) { // todo: move to addFeedbackToList()
        String message = getFeedbackString(myLocation, displayMode, feedbackType, description, imageFileName);
        myFileManager.saveDataInFile(message);
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
