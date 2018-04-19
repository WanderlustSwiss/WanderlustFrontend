//package eu.wise_iot.wanderlust;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//
//import eu.wise_iot.wanderlust.controllers.ControllerEvent;
//import eu.wise_iot.wanderlust.controllers.FragmentHandler;
//import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
//import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
//import eu.wise_iot.wanderlust.models.DatabaseModel.User;
//import eu.wise_iot.wanderlust.models.DatabaseModel.Trip;
//import eu.wise_iot.wanderlust.models.DatabaseObject.TripDao;
//import eu.wise_iot.wanderlust.services.LoginService;
//import eu.wise_iot.wanderlust.services.ServiceGenerator;
//import io.objectbox.Box;
//import io.objectbox.BoxStore;
//import io.objectbox.query.QueryBuilder;
//import okhttp3.Headers;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//
///**
// * Test class for Trip dao
// * @author Rilind Gashi, Alexander Weinbeck
// */
//public class TripDaoTest {
//
//    private BoxStore boxStore;
//    private Box<Trip> tripBox;
//    private QueryBuilder<Trip> tripQueryBuilder;
//    private Trip testTrip;
//    private TripDao tripDao;
//
//    private final static int TRIP_TEST_ID = 999;
//
//
//    @Before
//    public void setUpBefore() {
//
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        boxStore = MyObjectBox.builder().androidContext(appContext).build();
//        LoginService loginService = ServiceGenerator.createService(LoginService.class);
//        LoginUser testUser = new LoginUser("testuser", "Ha11loW4lt");
//
//        Call<User> call = loginService.basicLogin(testUser);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if (response.isSuccessful()) {
//                    Headers headerResponse = response.headers();
//                    //convert header to Map
//                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
//                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));
//
//                } else fail();
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                fail();
//            }
//        });
//
//        //tripDao = new TripDao(boxStore, appContext);
//        tripBox = boxStore.boxFor(Trip.class);
//        tripQueryBuilder = tripBox.query();
//        //testTrip
//
//        //Trip(long internal_id, long trip_id, byte imageId, int score, String birthday, String language, long user, long difficulty)
//        // 999 as test value
//        testTrip = new Trip(TRIP_TEST_ID,TRIP_TEST_ID, TRIP_TEST_ID);
//    }
//
//    @After
//    public void setUpAfter() {
//        //close boxstore
//        if (boxStore != null) boxStore.close();
//    }
//
//    /**
//     * test to update trip remote
//     */
//    @Test
//    public void updateTest() {
//        try {
//            CountDownLatch doneSignal = new CountDownLatch(1);
//
//            TripDao tripDao = new TripDao();
//            assertTrue(false);
//            tripDao.update(testTrip, new FragmentHandler() {
//                @Override
//                public void onResponse(ControllerEvent controllerEvent) {
//                    switch (controllerEvent.getType()) {
//                        case OK:
//                            doneSignal.countDown();
//                            break;
//                        default:
//                            fail();
//                    }
//                }
//            });
//
//            //wait for async method
//            doneSignal.await();
//            //continue if arrived
//            //given testTrip from Remote should be in Local database
//            assertTrue(tripBox.getAll().contains(testTrip));
//
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//    /**
//     * test to create trip remote
//     */
//    @Test
//    public void createTest(){
//        try {
//            CountDownLatch doneSignal = new CountDownLatch(1);
//            TripDao tripDao = new TripDao();
//            tripDao.create(testTrip, new FragmentHandler() {
//                @Override
//                public void onResponse(ControllerEvent controllerEvent) {
//                    switch (controllerEvent.getType()){
//                        case OK:
//                            doneSignal.countDown();
//                            break;
//                        default:
//                            fail();
//                    }
//                }
//            });
//            //wait for async method
//            doneSignal.await();
//            //continue if arrived
//            //given testPoi from Remote should be in Local database
//            assertTrue(tripBox.getAll().contains(testTrip));
//
//        } catch (Exception e){
//            fail(e.getMessage());
//        }
//    }
//    /**
//     * test to retrieve trip remote
//     */
//    @Test
//    public void retrieveTest() {
//
//        try {
//            TripDao tripDao = new TripDao();
//
//            CountDownLatch doneSignal = new CountDownLatch(1);
//
//            tripDao.retrieve(testTrip, new FragmentHandler() {
//                @Override
//                public void onResponse(ControllerEvent controllerEvent) {
//                    switch (controllerEvent.getType()) {
//                        case OK:
//                            doneSignal.countDown();
//                            break;
//                        default:
//                            fail();
//                    }
//                }
//            });
//            //wait for async method
//            doneSignal.await();
//            //continue if arrived
//            //given testTrip from Remote should be in Local database
//            assertTrue(tripBox.getAll().contains(testTrip));
//
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
//}
//
////
////import org.junit.After;
////import org.junit.Before;
////import org.junit.Test;
////import org.junit.runner.RunWith;
////
////import static org.junit.Assert.*;
////
////import eu.wise_iot.wanderlust.models.DatabaseModel.CommunityTours;
////import eu.wise_iot.wanderlust.models.DatabaseModel.Route_;
////import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
////import eu.wise_iot.wanderlust.models.DatabaseObject.TripDao;
////import io.objectbox.Box;
////import io.objectbox.BoxStore;
////import io.objectbox.query.QueryBuilder;
////
////
/////**
//// * @author Rilind Gashi
//// */
////
////@RunWith(AndroidJUnit4.class)
////public class TourDaoTest {
////
////    BoxStore boxStore;
////    Box<CommunityTours> routeBox;
////    QueryBuilder<CommunityTours> routeQueryBuilder;
////    CommunityTours testTour;
////    TripDao tourDao;
////
////    @Before
////    public void setUpBefore(){
////        Context appContext = InstrumentationRegistry.getTargetContext();
////        boxStore = MyObjectBox.builder().androidContext(appContext).build();
////        tourDao = new TripDao(boxStore);
////        routeBox = boxStore.boxFor(CommunityTours.class);
////        routeQueryBuilder = routeBox.query();
////        testTour = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
////    }
////
////    @Test
////    public void createTest(){
////        tourDao.create(testTour);
////        assertEquals("Tour1", routeQueryBuilder.equal(Route_.title, "Tour1")
////                .build().findFirst().getTitle());
////    }
////
////    @Test
////    public void updateTest(){
////        routeBox.put(testTour);
////        testTour.setTitle("UpdatedRouteTitle");
////        routeBox.put(testTour);
////        assertEquals("UpdatedRouteTitle", routeQueryBuilder.
////                equal(Route_.routeId, testTour.getRoute_id()).build().findFirst().getTitle());
////    }
////
////    @Test
////    public void findOneTest(){
////        routeBox.put(testTour);
////        try {
////            assertEquals("Tour1", tourDao.findOne("title", "Tour1").getTitle());
////        }catch (NoSuchFieldException | IllegalAccessException e){
////
////        }
////    }
////
////    @Test
////    public void findTest(){
////        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
////
////        routeBox.put(tourOne);
////        routeBox.put(tourTwo);
////
////        assertEquals(2, tourDao.find().size());
////    }
////
////    @Test
////    public void findDetailedTest(){
////        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourThree = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourFour = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
////
////        routeBox.put(tourOne);
////        routeBox.put(tourTwo);
////        routeBox.put(tourThree);
////        routeBox.put(tourFour);
////
////        try {
////            assertEquals(2, tourDao.find("title", "Tour3").size());
////        }catch (NoSuchFieldException | IllegalAccessException e){
////
////        }
////    }
////
////    @Test
////    public void countTest(){
////        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourThree = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourFour = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
////
////        routeBox.put(tourOne);
////        routeBox.put(tourTwo);
////        routeBox.put(tourThree);
////        routeBox.put(tourFour);
////
////        assertEquals(4, tourDao.count());
////    }
////
////    @Test
////    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
////        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourThree = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourFour = new CommunityTours(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
////
////        routeBox.put(tourOne);
////        routeBox.put(tourTwo);
////        routeBox.put(tourThree);
////        routeBox.put(tourFour);
////
////        assertEquals(2, tourDao.count("title", "Tour3"));
////    }
////
////    @Test
////    public void deleteTest(){
////        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
////
////        routeBox.put(tourOne);
////        routeBox.put(tourTwo);
////
////        try {
////            tourDao.delete("title", "Tour1");
////        }catch (NoSuchFieldException | IllegalAccessException e){
////
////        }
////
////        assertEquals(1, tourDao.find().size());
////    }
////
////    @Test
////    public void deleteAllTest(){
////        CommunityTours tourOne = new CommunityTours(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
////        CommunityTours tourTwo = new CommunityTours(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
////
////        routeBox.put(tourOne);
////        routeBox.put(tourTwo);
////
////        tourDao.deleteAll();
////        assertEquals(0, tourDao.count());
////    }
////
////    @After
////    public void after() {
////        routeBox.removeAll();
////        boxStore.close();
////    }
////}
