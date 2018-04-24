package eu.wise_iot.wanderlust.controllers;

import android.util.Log;

import java.util.List;

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
     * @param handler
     */
    public List<Tour> getRecentTours() {
        return recentTourDao.retrieveAll();
    }
    /**
     * get all tours out of db
     *
     * @return List of tours
     */
    public Tour getDataView(int tourID) {
        return userTourDao.find().get(tourID);
    }
    /**
     * get thumbnail of each tour
     *
     */
    public void downloadThumbnail(long tourID, int image_id, FragmentHandler handler) {
        userTourDao.downloadImage(tourID, image_id, handler);
    }
    /**
     * get all Favorites
     *
     */
    public void downloadFavorites(FragmentHandler handler) {
        favoriteDao.retrieveAllFavorites(handler);
    }
    /**
     * get all Favorites
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
