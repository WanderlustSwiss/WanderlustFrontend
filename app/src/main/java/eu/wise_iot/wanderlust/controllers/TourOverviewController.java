package eu.wise_iot.wanderlust.controllers;

import android.util.Log;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.RecentTourDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;

import static android.util.Log.d;
import static eu.wise_iot.wanderlust.controllers.EventType.OK;

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
    private RecentTourDao recentTourDao;
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
        return recentTourDao.find();
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
    public boolean checkIfTourExists(Tour tour){
        final AtomicBoolean exists = new AtomicBoolean(false);
        try {
            CountDownLatch countDownLatchThread = new CountDownLatch(1);
            userTourDao.retrieve(tour.getTour_id(), controllerEvent -> {
                switch (controllerEvent.getType()) {
                    case OK:
                        exists.set(true);
                        d("RECENTTOUR UPDATE2", "OK");
                        countDownLatchThread.countDown();
                        break;
                    case NOT_FOUND:
                        exists.set(false);
                        d("RECENTTOUR UPDATE2", "NOT FOUND");
                        recentTourDao.remove(tour);
                        countDownLatchThread.countDown();
                        break;
                    default:
                        exists.set(false);
                        countDownLatchThread.countDown();
                }
            });
            countDownLatchThread.await();
            return exists.get();
        } catch (Exception e){

        }
        return exists.get();
    }

    public long getTourFavoriteId(long id){
        try {
            Favorite fav = favoriteDao.findOne(Favorite_.tour, id);
            if(fav != null) return fav.getFav_id();
        } catch (Exception e){
            d(TAG, e.getMessage());
        }
        return -1;
    }
}
