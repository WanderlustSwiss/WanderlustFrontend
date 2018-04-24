package eu.wise_iot.wanderlust.controllers;

import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType;
import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Rating;
import eu.wise_iot.wanderlust.models.DatabaseModel.Rating_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Region;
import eu.wise_iot.wanderlust.models.DatabaseModel.Region_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.ViolationType;
import eu.wise_iot.wanderlust.models.DatabaseObject.CommunityTourDao;
import eu.wise_iot.wanderlust.models.DatabaseModel.Trip;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.RecentTourDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.RegionDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.RatingDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.TripDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;
import eu.wise_iot.wanderlust.services.DifficultyTypeService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.ViolationService;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * TourController:
 * handles the tourfragment and its in and output
 *
 * @author Alexander Weinbeck, Rilind Gashi, Simon Kaspar
 */
public class TourController {

    public static String convertToStringDistance(long distance) {
        if (distance >= 1000) {
            return Math.round((float) distance / 10.0) / 100.0 + "km ";
        } else return distance + "m";
    }

    public static String convertToStringDuration(long time) {
        int hours = (int) Math.floor(time / 60);
        int minutes = (int) time % 60;

        String text = "";
        if (hours != 0) text += hours + "h ";
        text += minutes + "min";
        return text;
    }

    private final FavoriteDao favoriteDao;
    private RatingDao ratingDao;
    private UserDao userDao;
    private Tour tour;
    private UserTourDao userTourDao;
    private CommunityTourDao communityTourDao;
    private DifficultyTypeDao difficultyTypeDao;
    private RegionDao regionDao;
    private RecentTourDao recentTourDao;
    private ImageController imageController;

    private static final String TAG = "Tourcontroller";

    public TourController(Tour tour) {
        this.tour = tour;
        userDao = UserDao.getInstance();
        userTourDao = UserTourDao.getInstance();
        communityTourDao = CommunityTourDao.getInstance();
        favoriteDao = FavoriteDao.getInstance();
        difficultyTypeDao = DifficultyTypeDao.getInstance();
        ratingDao = RatingDao.getInstance();
        imageController = ImageController.getInstance();
        regionDao = RegionDao.getInstance();
        recentTourDao = RecentTourDao.getInstance();
    }


    public void addRecentTour(Tour tour){
        recentTourDao.create(tour);
    }
    /**
     * True if Favorite is set, otherwise false
     */
    public boolean isFavorite() {
        Favorite fav = favoriteDao.findOne(Favorite_.tour, tour.getTour_id());
        return fav != null;
    }

    /**
     * set favorite
     *
     * @param handler Fragment handler
     */
    public void setFavorite(FragmentHandler handler) {
        favoriteDao.create(tour, handler);
    }

    /**
     * unset favorite
     *
     * @param handler Fragment handler
     */
    public boolean unsetFavorite(FragmentHandler handler) {
        Favorite fav = favoriteDao.findOne(Favorite_.tour, tour.getTour_id());
        if (fav != null) {
            favoriteDao.delete(fav.getFav_id(), handler);
            return true;
        }
        return false;
    }

    public boolean isSaved(){
        for(Tour t : communityTourDao.find()){
            if(t.getTour_id() == tour.getTour_id()){
                return true;
            }
        }
        return false;
    }

    public boolean setSaved(){
        communityTourDao.create(tour);
        Log.d(TAG, "Is saved");
        return true;
    }

    public boolean unsetSaved(){
        communityTourDao.delete(tour);
        Log.d(TAG, "Is deleted");
        return true;
    }

    public boolean setRating(Tour tour, int starRating, FragmentHandler handler) {
        if (starRating > 0) {
            Rating tourRating = new Rating(0, 0, starRating, tour.getTour_id(),
                    userDao.getUser().getUser_id());
            ratingDao.create(tourRating, handler);
            return true;
        }
        return false;
    }

    public long alreadyRated(long tour_id) {
        Property property = Rating_.tour;
        Rating rating = null;
        rating = ratingDao.findOne(property, tour_id, userDao.getUser().getUser_id());
        if (rating != null)
            return rating.getRate();
        else
            return 0;

    }

    //Todo: Tour should be a parameter
    public void getRating(Tour tour, FragmentHandler handler) {
        ratingDao.retrieve(tour.getTour_id(), handler);
    }


    public void getRating(FragmentHandler handler) {
        ratingDao.retrieve(tour.getTour_id(), handler);
    }

    public String getRegion() {
        Log.d(TAG, "Region number: " + regionDao.find().size());
        Region region = regionDao.findOne(Region_.region_id, tour.getRegion());
        if (region == null) {
            return "";
        } else {
            return region.getName() + " " + region.getCountryCode();
        }
    }

    /**
     * Calculate Distance between two coordinates, sum them up for every element
     *
     * @return Array of distance numbers
     */
    public Number[] getElevationProfileXAxis() {
        ArrayList<GeoPoint> polyList = PolyLineEncoder.decode(tour.getPolyline(), 10);
        Number[] xAxis = new Number[polyList.size()];
        Iterator<GeoPoint> iter = polyList.iterator();
        GeoPoint first = iter.next();
        double totalDistance = 0.0D;
        xAxis[0] = totalDistance;
        int ct = 1;
        while (iter.hasNext()) {
            GeoPoint next = iter.next();
            double distance = first.distanceTo(next);
            xAxis[ct++] = Math.round(100.0 * ((totalDistance + distance) / 1000.0)) / 100.0;
            totalDistance += distance;
            first = next;
        }
        xAxis[ct - 1] = Math.round(100.0 * (tour.getDistance() / 1000.0)) / 100.0;
        return xAxis;
    }


    /**
     * Converts elveation string into number array
     *
     * @return Array of elevation numbers
     */
    public Number[] getElevationProfileYAxis() {
        float[] elevations = elevationDecode(tour.getElevation());
        Number[] elevationObj = new Number[elevations.length];
        for (int i = 0; i < elevations.length; i++) {
            elevationObj[i] = Math.round(elevations[i]);
        }
        return elevationObj;
    }

    public void loadGeoData(FragmentHandler handler) {
        userTourDao.retrieve(tour.getTour_id(), controllerEvent -> {
            if (controllerEvent.getType() == EventType.OK) {
                Tour TourWithGeoData = (Tour) controllerEvent.getModel();
                tour.setPolyline(TourWithGeoData.getPolyline());
                tour.setElevation(TourWithGeoData.getElevation());
                handler.onResponse(new ControllerEvent(EventType.OK, tour));
            } else {
                handler.onResponse(new ControllerEvent(controllerEvent.getType(), tour));
            }
        });
    }

    /**
     * Calculate duration string from absolut minute value
     *
     * @return string with format HH h MM min
     */
    public String getDurationString() {
        if (tour != null) {
            return convertToStringDuration(tour.getDuration());
        } else {
            return convertToStringDuration(0);
        }
    }

    /**
     * Calculate the duration to a specific point on a tour which is divided by 5
     *
     * @param point n/5th point on a tour
     * @return string with format HH h MM min
     */
    public String getDurationStringSpecificPoint(long point) {
        if (tour != null) {
            return convertToStringDuration((tour.getDuration() * point) / 5);
        } else {
            return convertToStringDuration(0);
        }
    }

    /**
     * Calculate distance string from absolut meter value
     *
     * @return string with format 0.9 km
     */
    public String getDistanceString() {
        if (tour != null) {
            return convertToStringDistance(tour.getDistance());
        } else {
            return convertToStringDistance(0);
        }
    }

    /**
     * Get distance in meter
     *
     * @return long value in meter
     */
    public long getDistance() {
        if (tour != null) {
            return tour.getDistance();
        } else {
            return 0;
        }
    }

    /**
     * Difficulty mark
     *
     * @return mark
     */
    public String getDifficultyMark() {
        DifficultyType difficultyType = difficultyTypeDao.findOne(DifficultyType_.difft_id, tour.getDifficulty());
        if (difficultyType == null) {
            return "T1";
        } else {
            return difficultyType.getMark();
        }
    }

    /**
     * Difficulty level
     *
     * @return level
     */
    public long getLevel() {
        DifficultyType difficultyType = difficultyTypeDao.findOne(DifficultyType_.difft_id, tour.getDifficulty());
        if (difficultyType == null) {
            return 1L;
        } else {
            return difficultyType.getLevel();
        }
    }


    /**
     * Decodes base64 and GZIP compressed JSON Array String of elevations into a float array
     *
     * @param elevation Base64 and GZIP compressed JSON Array String
     * @return elevations
     */
    private float[] elevationDecode(String elevation) {
        byte[] decodedByteArray;
        // Base64 decode of string
        try {
            decodedByteArray = Base64.decode(elevation.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            return new float[0];

        }
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(decodedByteArray))) {
            BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\"", "");
                sb.append(line);
            }
            Gson gson = new Gson();
            Type type = new TypeToken<float[]>() {
            }.getType();
            br.close();
            return gson.fromJson(sb.toString(), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new float[0];
    }


    /**
     * Formats Date to string
     *
     * @return date
     */
    public String getCreatedAtString() {
        DateTimeFormatter encodef = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateTime dt = encodef.parseDateTime(tour.getCreatedAt());
        DateTimeFormatter decodef = DateTimeFormat.forPattern("dd. MMMMM yyyy");
        return dt.toString(decodef);
    }


    public long getAscent() {
        return tour.getAscent();
    }

    public long getDescent() {
        return tour.getDescent();
    }

    public String getDescription() {
        return tour.getDescription();
    }

    public String getTitle() {
        return tour.getTitle();
    }

    public String getPolyline() {
        return tour.getPolyline();
    }

    public List<File> getImages() {
        return imageController.getImages(tour.getImagePaths());
    }

    public Tour getCurrentTour() {
        return tour;
    }

    public void createTour(FragmentHandler<Trip> handler) {
        TripDao dao = TripDao.getInstance();
        if (dao != null) {
            dao.create(this.tour, handler);
        }
    }

    public Region getRegionFromString(String state) {
        return regionDao.findOne(Region_.name, state);
    }

    public List<Region> getAllRegions(){
        return regionDao.find();
    }

    public void reportViolation(TourController.Violation violation, final FragmentHandler handler){
        Call<Void> call = ServiceGenerator.createService(ViolationService.class).sendTourViolation(violation);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful())
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * represents a tour violation
     * structure needs to be kept like this for retrofit
     * @author Alexander Weinbeck
     * @license MIT
     */
    public class Violation{
        int tour_id;
        int type;

        public Violation(){

        }

        public Violation(long tour_id, long violationType_id){
            this.tour_id = (int)tour_id;
            this.type = (int)violationType_id;
        }
    }
}

