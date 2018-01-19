package eu.wise_iot.wanderlust.controllers;

import android.location.Location;
import android.util.Log;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi_;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;

/**
 * ToursController:
 * handles the toursfragment and its in and output
 *
 * @author Alexander Weinbeck, Rilind Gashi
 * @license MIT
 */
public class TourController {

    private FavoriteDao favoriteDao;
    private UserTourDao userTourDao;
    private UserDao userDao;
    private ArrayList<GeoPoint> polyList;
    private Road road;
    public int WALKING_SPEED = 5000;

    public TourController(){
        userTourDao = new UserTourDao();
        userDao = new UserDao();
        favoriteDao = new FavoriteDao();

    }
    /*
    public void getDataViewServer(int tourID, FragmentHandler handler) {
        userTourDao.retrieve(tourID, handler);
    }
    */
    /**
     * get all tours out of db
     *
     * @return List of tours
     */
    /*
    public UserTour getDataView(int tourID) {

        UserTourDao userTourDao = new UserTourDao();
        return userTourDao.find().get(tourID);
    }
    */

    public boolean isFavorite(long tourID){
        try {
            Favorite fav = favoriteDao.findOne(Favorite_.tour, tourID);
            if(fav != null)
                return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setFavorite(long tourID, FragmentHandler handler){
        Favorite favorite = new Favorite(0,0, tourID,  userDao.getUser().getUser_id());
        if(favorite != null) {
            favoriteDao.create(favorite, handler);
            return true;
        }
        return false;
    }

//    public void deleteFavorite(long tourID, FragmentHandler handler){
//        try {
//            favoriteDao.delete(favoriteDao.findOne(Favorite_.tour, tourID), handler);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }

    public double getDistance(String polyLine){
        polyList = PolyLineEncoder.decode(polyLine, 10);
        road = new Road(polyList);
        double distance = 0;
        for(int i = 1; i < polyList.size(); i++){
            GeoPoint firstGeoPoint = polyList.get(i-1);
            GeoPoint secondGeoPoint = polyList.get(i);
            distance += calculateDistance(firstGeoPoint, secondGeoPoint);
        }
        //Road road = new Road(polyList);
        //Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

        BigDecimal bd = new BigDecimal(distance);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();

        //return distance;
    }

    public double getDuration(String polyLine){
        return new BigDecimal(String.valueOf((getDistance(polyLine) / 1000) / 5))
                .setScale(2,RoundingMode.HALF_UP).doubleValue();
    }

    public double getAscend(String polyLine){
        return 0;
    }

    public double getDescend(String polyLine){
        return 0;
    }

    public String getRegion(String polyLine){
        return null;
    }

    public String getStartPoint(){
        return null;
    }

    public double calculateDistance(GeoPoint firstGeoPoint, GeoPoint secondGeoPoint){
        double lat1 = firstGeoPoint.getLatitude();
        double lng1 = firstGeoPoint.getLongitude();
        double lat2 = secondGeoPoint.getLatitude();
        double lng2 = secondGeoPoint.getLongitude();
        float [] dist = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, dist);

        return new BigDecimal(String.valueOf((double) dist[0])).setScale(2,RoundingMode.HALF_UP).doubleValue();
    }

    public String convertToStringDistance(long distance) {
        int kilometers = (int) distance / 1000;
        int meters = (int) distance % 1000;
        String text = "";
        if (kilometers != 0) text += kilometers + "km ";
        text += meters + "m";
        return text;
    }

    public String convertToStringDuration(long time) {
        int hours = (int) Math.floor(time / 60);
        int minutes = (int) time % 60;

        String text = "";
        if (hours != 0) text += hours + "h ";
        text += minutes + "min";
        return text;
    }

}
