package eu.wise_iot.wanderlust.controllers;

import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;
import eu.wise_iot.wanderlust.models.Old.Tour;

/**
 * ToursController:
 * handles the toursfragment and its in and output
 * @author Alexander Weinbeck
 * @license MIT
 */
public class TourController {
    /**
     * get all tours out of db
     * @return List of tours
     */
    public static List<String> getDataView(int tourID, FragmentHandler handler){

        UserTourDao userTourDao = new UserTourDao();
        UserTour ut = userTourDao.find().get(tourID);

        List<String> dataModel = new ArrayList<>();


        dataModel.add(ut.getDescription());
        dataModel.add(ut.getImagePath());
        dataModel.add(ut.getPolyline());
        dataModel.add(ut.getTitle());
        dataModel.add(Long.toString(ut.getDifficulty()));



        return dataModel;
    }
    public static void getDataViewServer(int tourID, FragmentHandler handler) {
        UserTourDao userTourDao = new UserTourDao();
        userTourDao.retrieve(tourID, handler);
    }

}
