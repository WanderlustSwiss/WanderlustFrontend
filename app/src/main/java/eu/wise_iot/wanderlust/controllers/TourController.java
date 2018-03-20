package eu.wise_iot.wanderlust.controllers;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType;
import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.models.DatabaseModel.GetWeatherTask;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.TourKit;
import eu.wise_iot.wanderlust.models.DatabaseModel.Weather;
import eu.wise_iot.wanderlust.models.DatabaseModel.WeatherKeys;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.EquipmentDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.TourKitDao;
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
    private EquipmentController equipmentController;
    private final TourKitDao tourKitDao;

    public TourController(Tour tour){
        this.tour = tour;
        userDao = UserDao.getInstance();
        userTourDao = UserTourDao.getInstance();
        favoriteDao = FavoriteDao.getInstance();
        difficultyTypeDao = DifficultyTypeDao.getInstance();
        imageController = ImageController.getInstance();
        weatherController = WeatherController.getInstance();
        equipmentController = EquipmentController.getInstance();
        loadGeoData();
        tourKitDao = TourKitDao.getInstance();
    }

    /**
     * get list of equipment for given tour
     * @param parTour
     * @return
     */
    public List<Equipment> getEquipmentOfTour(Tour parTour){
        //TODO add implementation according to equipment structure
        //return tourKitDao.findOne(tour,parTour);
        return null;
    }
    public void retrieveRecommendedEquipment(FragmentHandler handler){
        //Get 5 Points of tour
        ArrayList<GeoPoint> polyList = PolyLineEncoder.decode(tour.getPolyline(), 10);
        List<GeoPoint> weatherPoints = new ArrayList<>();

        if(polyList.size() >= 5){
            //Add 5 Points: startPoint, 25%, 50%, 75%, endPoint
            for(int i = 0; i <= 4; i++) {
                weatherPoints.add(polyList.get((polyList.size()/4)*i));
            }

            //get weather from points
            GetWeatherTask getWeatherTask = new GetWeatherTask(weatherController, new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    switch (controllerEvent.getType()){
                        case OK:
                            List<Weather> weather = (List<Weather>) controllerEvent.getModel();
                            List<Equipment> equipment = equipmentController.getEquipmentList();

                            //TODO find out which equip should be picked

                            //Calculate the score of each weather type
                            int maxTemp = 0;
                            int minTemp = 0;
                            List<WeatherKeys> weatherKeys = weatherController.getWeatherKeys();
                            int[] weatherScore = new int[weatherKeys.size()];
                            for(Weather w  : weather){
                                weatherScore[w.getCategory()]++;
                                maxTemp += w.getMaxTemp();
                                minTemp += w.getMinTemp();
                            }

                            maxTemp /= 5;
                            minTemp /= 5;


                            //safe equipment at array pos = type of equipment
                            Equipment[] recommendedEquipment = new Equipment[equipmentController.getTypeCount()];

                            for(Equipment e : equipment){

                                //Check if type of equipment is already present
                                if(recommendedEquipment[e.getType()] != null){

                                    //Check if better than current recommended
                                    Equipment current = recommendedEquipment[e.getType()];

                                    int scoreCurrent = 0;
                                    int scoreNew = 0;

                                    //Calculate score from weather
                                    for (int i = 0; i < current.getWeather().length; i++){
                                        if(current.getWeather()[i] == 1){
                                            scoreCurrent += weatherScore[i];
                                        }
                                    }

                                    for (int i = 0; i < e.getWeather().length; i++){
                                        if(e.getWeather()[i] == 1){
                                            scoreNew += weatherScore[i];
                                        }
                                    }

                                    //TODO look at other parameters aswell

                                    //set Equipment e to the recommended if it is better suited
                                    if(scoreNew > scoreCurrent){
                                        recommendedEquipment[e.getType()] = e;
                                    }
                                }
                                //Else set it recommended
                                else{
                                    recommendedEquipment[e.getType()] = e;
                                }
                            }

                            break;
                        default:

                    }
                }
            });

            getWeatherTask.execute(weatherPoints);

        } else{
            //only add startPoint
            weatherPoints.add(polyList.get(0));
        }


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

    public int[] getHighProfile() throws IOException {
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

    public void loadGeoData(){
        userTourDao.retrieve(tour.getTour_id(), new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                Tour TourWithGeoData = (Tour) controllerEvent.getModel();
                tour.setPolyline(TourWithGeoData.getPolyline());
                tour.setElevation(TourWithGeoData.getElevation());
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

    public long getAscent(){ return tour.getAscent(); }
    public long getDescent() { return tour.getDescent(); }
    public String getDescription(){ return tour.getDescription(); }
    public String getTitle(){ return tour.getTitle(); }
    public String getPolyline(){ return tour.getPolyline(); }
    public List<File> getImages(){
        return imageController.getImages(tour.getImagePaths());
    }
}
