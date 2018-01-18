package eu.wise_iot.wanderlust.controllers;

import android.location.Location;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi_;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour_;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;

/**
 * TourController:
 * handles the tourfragment and its in and output
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

    public TourController(){
        userTourDao = new UserTourDao();
        userDao = new UserDao();
        favoriteDao = new FavoriteDao();

    }

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

    public void deleteFavorite(long tourID, FragmentHandler handler){
        try {
            favoriteDao.delete(favoriteDao.findOne(Favorite_.tour, tourID), handler);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

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
        distance = distance / 1000;
        BigDecimal bd = new BigDecimal(distance);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String getDuration(int ascend, int descend, double distance){
        StringBuilder durationBuilder = new StringBuilder();
        double duration = ascend/300 + descend/600 + distance / 4.2;
        BigDecimal bd = new BigDecimal(duration);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        duration = bd.doubleValue();
        double minVal = duration % 1;
        double hourVal = duration - minVal;

        durationBuilder.append(hourVal + " h");
        durationBuilder.append(minVal + " min");
        return durationBuilder.toString();
    }

    public int[] getHighProfile(String elevaltion) throws IOException {
        int highProfile[] = new int[3];
        /*
        byte[] valueDecoded= new byte[0];
        try {
            valueDecoded = Base64.decode(elevaltion.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(valueDecoded);
        GZIPInputStream gis = null;
        gis = new GZIPInputStream(bis);
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            line = line.replace("[", "");
            line = line.replace("]", "");
            line = line.replace("\"", "");
            sb.append(line);
        }
        br.close();
        gis.close();
        bis.close();

        String s[] = sb.toString().split(",");
        int lowestPointBefore = 0;
        int highestPoint = 0;
        int lowestPointAfter = 0;
        int indexOfHighestPoint = 0;

        for(int i = 0; i < s.length; i++){
            if(highestPoint < Integer.parseInt(s[i])) {
                highestPoint = Integer.parseInt(s[i]);
                indexOfHighestPoint = i;
            }
        }

        lowestPointAfter = highestPoint;
        lowestPointBefore = highestPoint;

        for(int j = 0; j < indexOfHighestPoint; j++){
            if(lowestPointBefore > Integer.parseInt(s[j]))
                lowestPointBefore = Integer.parseInt(s[j]);
        }

        for(int k = indexOfHighestPoint; k < s.length; k++){
            if(lowestPointAfter > Integer.parseInt(s[k]))
                lowestPointAfter = Integer.parseInt(s[k]);
        }

        highProfile[0] = lowestPointBefore;
        highProfile[1] = lowestPointAfter;
        highProfile[2] = highestPoint;*/
        return highProfile;
    }

    public double calculateDistance(GeoPoint firstGeoPoint, GeoPoint secondGeoPoint){
        double lat1 = firstGeoPoint.getLatitude();
        double lng1 = firstGeoPoint.getLongitude();
        double lat2 = secondGeoPoint.getLatitude();
        double lng2 = secondGeoPoint.getLongitude();
        float [] dist = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, dist);

        return new BigDecimal(String.valueOf((double) dist[0])).setScale(2,RoundingMode.valueOf(2)).doubleValue();
    }

    public void getSelectedUserTour(UserTour userTour, FragmentHandler handler){
        userTourDao.retrieve(userTour.getTour_id(), handler);
    }
}
