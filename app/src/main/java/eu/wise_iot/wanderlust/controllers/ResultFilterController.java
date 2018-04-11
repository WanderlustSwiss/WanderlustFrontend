package eu.wise_iot.wanderlust.controllers;

import android.util.Log;

import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;

/**
 * ToursController:
 * handles the toursfragment and its in and output
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class ResultFilterController {

    private static final String TAG = "TourOverviewController";
    private final UserTourDao userTourDao;
    private FavoriteDao favoriteDao;
    private final DifficultyTypeDao difficultyType;
    private final ImageController imageController;

    public ResultFilterController(){
        userTourDao = UserTourDao.getInstance();
        favoriteDao = FavoriteDao.getInstance();
        favoriteDao = FavoriteDao.getInstance();
        difficultyType = DifficultyTypeDao.getInstance();
        imageController = ImageController.getInstance();
    }

    /**
     * get all required data for the view
     * HTTP request, Expected Param: page, Optional: durationS (minutes), durationE (minutes), region (id), title (string), difficulties (list of comma separated ids)
     * @param handler
     * @param page
     */
    public void getFilteredTours(FragmentHandler handler, int page, int durationS, int durationE, int regionID, String title, String difficulties) {
        userTourDao.retrieveAllFiltered(handler, page, durationS, durationE, regionID, title, difficulties);
    }

}
