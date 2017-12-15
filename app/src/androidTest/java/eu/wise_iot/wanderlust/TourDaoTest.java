//package eu.wise_iot.wanderlust;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.junit.Assert.*;
//
//import eu.wise_iot.wanderlust.models.DatabaseModel.CommunityTours;
//import eu.wise_iot.wanderlust.models.DatabaseModel.Route_;
//import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
//import eu.wise_iot.wanderlust.models.DatabaseObject.TripDao;
//import io.objectbox.Box;
//import io.objectbox.BoxStore;
//import io.objectbox.query.QueryBuilder;
//
//
///**
// * @author Rilind Gashi
// */
//
//@RunWith(AndroidJUnit4.class)
//public class TourDaoTest {
//
//    BoxStore boxStore;
//    Box<CommunityTours> routeBox;
//    QueryBuilder<CommunityTours> routeQueryBuilder;
//    CommunityTours testTour;
//    TripDao tourDao;
//
//    @Before
//    public void setUpBefore(){
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        boxStore = MyObjectBox.builder().androidContext(appContext).build();
//        tourDao = new TripDao(boxStore);
//        routeBox = boxStore.boxFor(CommunityTours.class);
//        routeQueryBuilder = routeBox.query();
//        testTour = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
//    }
//
//    @Test
//    public void createTest(){
//        tourDao.create(testTour);
//        assertEquals("Tour1", routeQueryBuilder.equal(Route_.title, "Tour1")
//                .build().findFirst().getTitle());
//    }
//
//    @Test
//    public void updateTest(){
//        routeBox.put(testTour);
//        testTour.setTitle("UpdatedRouteTitle");
//        routeBox.put(testTour);
//        assertEquals("UpdatedRouteTitle", routeQueryBuilder.
//                equal(Route_.routeId, testTour.getRoute_id()).build().findFirst().getTitle());
//    }
//
//    @Test
//    public void findOneTest(){
//        routeBox.put(testTour);
//        try {
//            assertEquals("Tour1", tourDao.findOne("title", "Tour1").getTitle());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void findTest(){
//        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
//
//        routeBox.put(tourOne);
//        routeBox.put(tourTwo);
//
//        assertEquals(2, tourDao.find().size());
//    }
//
//    @Test
//    public void findDetailedTest(){
//        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourThree = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourFour = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
//
//        routeBox.put(tourOne);
//        routeBox.put(tourTwo);
//        routeBox.put(tourThree);
//        routeBox.put(tourFour);
//
//        try {
//            assertEquals(2, tourDao.find("title", "Tour3").size());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void countTest(){
//        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourThree = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourFour = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
//
//        routeBox.put(tourOne);
//        routeBox.put(tourTwo);
//        routeBox.put(tourThree);
//        routeBox.put(tourFour);
//
//        assertEquals(4, tourDao.count());
//    }
//
//    @Test
//    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
//        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourThree = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourFour = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
//
//        routeBox.put(tourOne);
//        routeBox.put(tourTwo);
//        routeBox.put(tourThree);
//        routeBox.put(tourFour);
//
//        assertEquals(2, tourDao.count("title", "Tour3"));
//    }
//
//    @Test
//    public void deleteTest(){
//        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
//
//        routeBox.put(tourOne);
//        routeBox.put(tourTwo);
//
//        try {
//            tourDao.delete("title", "Tour1");
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//
//        assertEquals(1, tourDao.find().size());
//    }
//
//    @Test
//    public void deleteAllTest(){
//        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
//        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
//
//        routeBox.put(tourOne);
//        routeBox.put(tourTwo);
//
//        tourDao.deleteAll();
//        assertEquals(0, tourDao.count());
//    }
//
//    @After
//    public void after() {
//        routeBox.removeAll();
//        boxStore.close();
//    }
//}
