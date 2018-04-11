package eu.wise_iot.wanderlust.controllers;

import android.util.Log;

import org.apache.commons.lang3.builder.Diff;

import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour_;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;
import io.objectbox.Property;

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
     * get all required data for the view and does following request:
     * HTTP request, Expected Param: page, Optional: durationS (minutes), durationE (minutes), region (id), title (string), difficulties (list of comma separated ids)
     * @param handler
     * @param page
     */
    public void getFilteredTours(FragmentHandler handler, int page, int durationS, int durationE, int regionID, String title, String difficulties) {
        userTourDao.retrieveAllFiltered(handler, page, durationS, durationE, regionID, title, difficulties);
    }
    public int getRegionIdByString(String region) {
        //userTourDao.findOne(Tour_)
    }
    public String getDifficultiesByArray(boolean t1, boolean t2, boolean t3, boolean t4, boolean t5, boolean t6) {
        StringBuilder sb = new StringBuilder();
        if(t1) sb.append(difficultyType.findOne(DifficultyType_.level,1));
        else if(t2) sb.append(difficultyType.findOne(DifficultyType_.level,2));
        else if(t3) sb.append(difficultyType.findOne(DifficultyType_.level,3));
        else if(t4) sb.append(difficultyType.findOne(DifficultyType_.level,4));
        else if(t5) sb.append(difficultyType.findOne(DifficultyType_.level,5));
        else if(t6) sb.append(difficultyType.findOne(DifficultyType_.level,6));
        return sb.toString();
    }
}
