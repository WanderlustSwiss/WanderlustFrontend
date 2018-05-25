package eu.wise_iot.wanderlust.controllers;

import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.RecentTourDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;

/**
 * ToursController:
 * handles the toursfragment and its in and output
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class TourOverviewController {

    private static final String TAG = "TourOverviewController";
    private final UserTourDao userTourDao;
    private FavoriteDao favoriteDao;
    private final RecentTourDao recentTourDao;
    private final DifficultyTypeDao difficultyType;
    private final ImageController imageController;

    public TourOverviewController(){
        userTourDao = UserTourDao.getInstance();
        favoriteDao = FavoriteDao.getInstance();
        favoriteDao = FavoriteDao.getInstance();
        difficultyType = DifficultyTypeDao.getInstance();
        imageController = ImageController.getInstance();
        recentTourDao = RecentTourDao.getInstance();
    }

    /**
     * get all required data for the view
     * @param handler
     * @param page
     */
    public void getAllTours(int page, FragmentHandler handler) {
        userTourDao.retrieveAll(handler, page);
    }

    /**
     * get all Favorites for the view
     * @param handler
     */
    public void getAllFavoriteTours(FragmentHandler handler) {
        favoriteDao.retrieveAllFavoriteTours(handler);
    }

    /**
     * get all Favorites for the view
     * @return list of recent tours
     */
    public List<Tour> getRecentTours() {
        List<Tour> shallowCopy = recentTourDao.find();
        Collections.reverse(shallowCopy.subList(0, shallowCopy.size()));
        return shallowCopy;
    }

    /**
     * get all Favorites for the view
     * @return list of recent tours
     */
    public void removeRecentTour(Tour tour) {
        recentTourDao.remove(tour);
    }
    /**
     * get thumbnail of each tour
     *
     */
    public void downloadThumbnail(long tourID, int image_id, FragmentHandler handler) {
        userTourDao.downloadImage(tourID, image_id, handler);
    }
    /**
     * set Favorite
     *
     */
    public void setFavorite(Tour tour, FragmentHandler handler) {
        favoriteDao.create(tour,handler);
    }
    /**
     * get all Favorites
     *
     */
    public void deleteFavorite(long favorite_id, FragmentHandler handler) {
        favoriteDao.delete(favorite_id,handler);
    }
    public Integer checkIfTourExists(Tour tour){
        final AtomicInteger responseCode = new AtomicInteger(0);
        try {
            CountDownLatch countDownLatchThread = new CountDownLatch(1);
            userTourDao.retrieve(tour.getTour_id(), controllerEvent -> {
                responseCode.set(controllerEvent.getType().code);
                countDownLatchThread.countDown();

            });
            countDownLatchThread.await();
            return responseCode.get();
        } catch (Exception e){
            if (BuildConfig.DEBUG) Log.d(TAG,"failure while processing request");
        }
        return responseCode.get();
    }

    public void checkIfTourExists(Tour tour, FragmentHandler handler){
        userTourDao.retrieve(tour.getTour_id(), handler);
    }

    public long getTourFavoriteId(long id){
        try {
            Favorite fav = favoriteDao.findOne(Favorite_.tour, id);
            if(fav != null) return fav.getFav_id();
        } catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
        return -1;
    }
}
