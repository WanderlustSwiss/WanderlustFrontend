package eu.wise_iot.wanderlust.utils;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;

/**
 * MyGpxParser:
 * @author Fabian Schwander
 * @license MIT
 */
public class MyGpxParser {
    private static final String TAG = "MyGpxParser";
    private final GPXParser gpxParser = new GPXParser();
    private Context context;

    public MyGpxParser(Context context) {
        this.context = context;
    }

    public List<TrackPoint> getTrackPointList(int resourceId) {
        List<TrackPoint> trackPointList;
        Gpx gpx = getParseedGpx(resourceId);
        // todo: delete when finished with testing
        trackPointList = gpx.getTracks().get(0).getTrackSegments().get(0).getTrackPoints();
        return trackPointList;
    }

    private Gpx getParseedGpx(int ressourceId) {
        Gpx parsedGpx = null;
        try {
            InputStream in = context.getResources().openRawResource(ressourceId);
            parsedGpx = gpxParser.parse(in);
            // todo: delete when finished with testing
            Log.d(TAG, "parsed gpx WAYPOINTS: "+ parsedGpx.getWayPoints().size());
            Log.d(TAG, "parsed gpx TRACKS: "+ parsedGpx.getTracks().size());
            Log.d(TAG, "parsed gpx TRACKSSEGMENT SIZE: "+ parsedGpx.getTracks().get(0).getTrackSegments().size());
            Log.d(TAG, "parsed gpx TRACKSSEGMENT TRACKPOINTS SIZE: "+ parsedGpx.getTracks().get(0).getTrackSegments().get(0).getTrackPoints().size());
            Log.d(TAG, "parsed gpx ROUTES: "+ parsedGpx.getRoutes().size());

        } catch (IOException | XmlPullParserException e) {
            // do something with this exception
            e.printStackTrace();
        }
        try {
            return parsedGpx;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            throw new NullPointerException();
        }
    }
}
