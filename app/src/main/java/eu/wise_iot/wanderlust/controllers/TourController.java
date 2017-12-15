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
    public UserTour getDataView(int tourID){

        UserTourDao userTourDao = new UserTourDao();
        return userTourDao.find().get(tourID);
    }
    public static void getDataViewServer(int tourID, FragmentHandler handler) {
        UserTourDao userTourDao = new UserTourDao();
        userTourDao.retrieve(tourID, handler);
    }

}
