package eu.wise_iot.wanderlust.controllers;

import org.joda.time.DateTime;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType;
import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.models.DatabaseModel.GetWeatherTask;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Weather;
import eu.wise_iot.wanderlust.models.DatabaseModel.WeatherKeys;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;

/**
 * TourController:
 * handles the tourfragment and its in and output
 *
 * @author Alexander Weinbeck, Rilind Gashi, Simon Kaspar
 * @license MIT
 */
public class TourController {

    public static String convertToStringDistance(long distance) {
        if (distance >= 1000) {
            return Math.round((float)distance / 10.0) / 100.0 + "km ";
        }
        else return distance + "m";
    }

    public static String convertToStringDuration(long time) {
        int hours = (int) Math.floor(time / 60);
        int minutes = (int) time % 60;

        String text = "";
        if (hours != 0) text += hours + "h ";
        text += minutes + "min";
        return text;
    }

    private FavoriteDao favoriteDao;
    private UserDao userDao;
    private Tour tour;
    private UserTourDao userTourDao;
    private DifficultyTypeDao difficultyTypeDao;
    private ImageController imageController;
    private WeatherController weatherController;
    private ArrayList<GeoPoint> polyList;
    private EquipmentController equipmentController;

    public TourController(Tour tour){
        this.tour = tour;
        userDao = UserDao.getInstance();
        userTourDao = UserTourDao.getInstance();
        favoriteDao = FavoriteDao.getInstance();
        difficultyTypeDao = DifficultyTypeDao.getInstance();
        imageController = ImageController.getInstance();
        weatherController = WeatherController.getInstance();
        equipmentController = EquipmentController.getInstance();
    }


    /**
     * True if Favorite is set, otherwise false
     */
    public boolean isFavorite(){
        try {
            Favorite fav = favoriteDao.findOne(Favorite_.tour, tour.getTour_id());
            if(fav != null)
                return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * set favorite
     * @param handler Fragment handler
     */
    public boolean setFavorite(FragmentHandler handler){
        favoriteDao.create(tour, handler);
        return true;
    }

    /**
     * unset favorite
     * @param handler Fragment handler
     */
    public boolean unsetFavorite(FragmentHandler handler){
        try {
            Favorite fav = favoriteDao.findOne(Favorite_.tour, tour.getTour_id());
            if (fav != null){
                favoriteDao.delete(fav.getFav_id(), handler);
                return true;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Number[] getElevationProfileXAxis(){
        ArrayList<GeoPoint> polyList  = PolyLineEncoder.decode(tour.getPolyline(),10);
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
        Log.d("DEBUG", "" + (tour.getDistance() / 1000.0));
        xAxis[ct - 1] = Math.round(100.0 * (tour.getDistance() / 1000.0)) / 100.0;
        return xAxis;
    }
    public Number[] getElevationProfileYAxis(){
        float[] elevations = elevationDecode(tour.getElevation());
        Number[] elevationObj = new Number[elevations.length];
        for (int i = 0; i < elevations.length; i++){
            elevationObj[i] = Math.round(elevations[i]);
        }
        return elevationObj;
    }

    public void loadGeoData(FragmentHandler handler){
        userTourDao.retrieve(tour.getTour_id(), new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                Tour TourWithGeoData = (Tour) controllerEvent.getModel();
                tour.setPolyline(TourWithGeoData.getPolyline());
                tour.setElevation(TourWithGeoData.getElevation());
                handler.onResponse(new ControllerEvent(EventType.OK, tour));
            }
        });
    }

    /**
     * Calculate duration string from absolut minute value
     * @return string with format HH h MM min
     */
    public String getDurationString(){
        if (tour != null){
            return convertToStringDuration(tour.getDuration());
        }else{
            return convertToStringDuration(0);
        }
    }

    /**
     * Calculate distance string from absolut meter value
     * @return string with format 0.9 km
     */
    public String getDistanceString(){
        if (tour != null){
            return convertToStringDistance(tour.getDistance());
        }else{
            return convertToStringDistance(0);
        }
    }
    /**
     * Get distance in meter
     * @return long value in meter
     */
    public long getDistance(){
        if (tour != null){
            return tour.getDistance();
        }else{
            return 0;
        }
    }
    /**
     * Difficulty mark
     * @return mark
     */
    public String getDifficultyMark(){
        try {
            DifficultyType difficultyType =  difficultyTypeDao.findOne(DifficultyType_.difft_id, tour.getDifficulty());
            return difficultyType.getMark();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return "T1";
        }
    }
    /**
     * Difficulty level
     * @return level
     */
    public long getLevel(){
        try {
            DifficultyType difficultyType =  difficultyTypeDao.findOne(DifficultyType_.difft_id, tour.getDifficulty());
            return difficultyType.getLevel();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return 0;
        }
    }
    public float[] elevationDecode(String elevation){
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
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            Gson gson = new Gson();
            Type type = new TypeToken<float[]>() {}.getType();
            br.close();
            return gson.fromJson(sb.toString(), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new float[0];
    }
    public long getAscent(){ return tour.getAscent(); }
    public long getDescent() { return tour.getDescent(); }
    public String getDescription(){ return tour.getDescription(); }
    public String getTitle(){ return tour.getTitle(); }
    public String getPolyline(){ return tour.getPolyline(); }
    public List<File> getImages(){
        return imageController.getImages(tour.getImagePaths());
    }
}
