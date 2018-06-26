package eu.wise_iot.wanderlust.controllers;

import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Region;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.RegionDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;

/**
 * Handles the logic fpr tour fragment and its in and output
 *
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */
public class FilterController {

    private static final String TAG = "TourOverviewController";
    private final UserTourDao userTourDao;
    private FavoriteDao favoriteDao;
    private final RegionDao regionDao;
    private final DifficultyTypeDao difficultyType;
    private final ImageController imageController;

    public FilterController(){
        userTourDao = UserTourDao.getInstance();
        regionDao = RegionDao.getInstance();
        difficultyType = DifficultyTypeDao.getInstance();
        imageController = ImageController.getInstance();
    }

    public String[] getRegionsList() {
        List<String> list = new ArrayList<>();
        for(Region r : regionDao.find()) list.add(r.toString());
        String[] stockArr = new String[list.size()];
        stockArr = list.toArray(stockArr);
        return stockArr;
    }
    public Region[] getRegions(){
        List<Region> regions = regionDao.find();
        Region[] regionsArr = new Region[regions.size()];
        regionsArr = regions.toArray(regionsArr);
        return regionsArr;
    }
}
