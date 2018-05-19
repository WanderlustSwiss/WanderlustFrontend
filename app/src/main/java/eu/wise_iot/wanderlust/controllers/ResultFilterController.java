package eu.wise_iot.wanderlust.controllers;

import android.content.Context;

import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Region;
import eu.wise_iot.wanderlust.models.DatabaseModel.Region_;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.RegionDao;
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
    private final RegionDao regionDao;
    private final DifficultyTypeDao difficultyType;
    private final ImageController imageController;

    public ResultFilterController(){
        userTourDao = UserTourDao.getInstance();
        regionDao = RegionDao.getInstance();
        difficultyType = DifficultyTypeDao.getInstance();
        imageController = ImageController.getInstance();
    }

    public void getFilteredTours(FragmentHandler handler, float rating, int distanceS, int distanceE, int page, int durationS, int durationE, String regionID, String title, String difficulties) {
        userTourDao.retrieveAllFiltered(handler, rating, page, distanceS, distanceE, durationS, durationE, regionID, title, difficulties);
    }

    public int getRegionIdByString(String region) {
        //should be limited to 20 values
        List<Region> result = regionDao.find(Region_.name,region);
        if(result != null && result.size() > 0) return (int)result.get(0).getRegion_id();
        else return 0;
    }
    public String getRegionbyID(long region, Context context) {
        //should be limited to 20 values
        Region r = regionDao.findOne(Region_.region_id,region);
        if(r != null) return r.getName();
        else return context.getResources().getString(R.string.unknown);

    }
    public String getDifficultiesByArray(boolean t1, boolean t2, boolean t3, boolean t4, boolean t5, boolean t6) {
        StringBuilder sb = new StringBuilder();
        if(t1) sb.append(difficultyType.findOne(DifficultyType_.level,1));
        else if(t2) sb.append(difficultyType.findOne(DifficultyType_.level,2).getDifft_id());
        else if(t3) sb.append(difficultyType.findOne(DifficultyType_.level,3).getDifft_id());
        else if(t4) sb.append(difficultyType.findOne(DifficultyType_.level,4).getDifft_id());
        else if(t5) sb.append(difficultyType.findOne(DifficultyType_.level,5).getDifft_id());
        else if(t6) sb.append(difficultyType.findOne(DifficultyType_.level,6).getDifft_id());
        return sb.toString();
    }
}
