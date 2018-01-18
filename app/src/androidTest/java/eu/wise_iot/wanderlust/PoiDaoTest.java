//package eu.wise_iot.wanderlust;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//
//import org.junit.Before;
//import org.junit.runner.RunWith;
//
//import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
//import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
//import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
//import io.objectbox.Box;
//import io.objectbox.BoxStore;
//import io.objectbox.query.QueryBuilder;
//
//
package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Poi Dao testclass
 * @author Rilind Gashi, Alexander Weinbeck
 * @license MIT
 */
public class PoiDaoTest {

    BoxStore boxStore;
    Box<Poi> poiBox;
    QueryBuilder<Poi> poiQueryBuilder;
    Poi testPoi;

    private static final byte[] POI_TEST_BYTES_IMAGE = {9,9,9,9};;
    private static final int POI_TEST_ID = 999;

    @Before
    public void setUpBefore() {

        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        LoginUser testUser = new LoginUser("testuser", "Ha11loW4lt");

        Call<User> call = loginService.basicLogin(testUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Headers headerResponse = response.headers();
                    //convert header to Map
                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));

                } else fail();
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                fail();
            }
        });

        //poiDao = new PoiDao(boxStore, appContext);
        poiBox = boxStore.boxFor(Poi.class);
        poiQueryBuilder = poiBox.query();
        //testPoi
        testPoi = new Poi(99, "TestMatterhorn", "TestBerg", POI_TEST_BYTES_IMAGE, 53.53f, 53.53f, 1, 1, 9, false, 9);
    }

    @After
    public void setUpAfter() {
        //close boxstore
        if(boxStore != null) boxStore.close();
    }
    /**
     * test to create poi remote
     */
    @Test
    public void createTest(){
        try {
            CountDownLatch doneSignal = new CountDownLatch(1);
            PoiDao pd = new PoiDao();
            pd.create(testPoi, new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    switch (controllerEvent.getType()){
                        case OK:
                            doneSignal.countDown();
                            break;
                        default:
                            fail();
                    }
                }
            });
            //wait for async method
            doneSignal.await();
            //continue if arrived
            //given testPoi from Remote should be in Local database
            assertTrue(poiBox.getAll().contains(testPoi));

        } catch (Exception e){
            fail(e.getMessage());
        }
    }
    /**
     * test to update poi remote
     */
    @Test
    public void updateTest(){
        try {
            Poi testPoiUpdate = new Poi(POI_TEST_ID, "TestMatterhorn", "TestBerg", POI_TEST_BYTES_IMAGE, 53.53f, 53.53f, 1, 1, 9, false, 9);
            CountDownLatch doneSignal = new CountDownLatch(1);

            PoiDao poiDao = new PoiDao();
            assertTrue(false);
            poiDao.update(testPoiUpdate, new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    switch (controllerEvent.getType()){
                        case OK:
                            doneSignal.countDown();
                            break;
                        default:
                            fail();
                    }
                }
            });

            //wait for async method
            doneSignal.await();
            //continue if arrived
            //given testPoi from Remote should be in Local database
            assertTrue(poiBox.getAll().contains(testPoi));

        } catch (Exception e){
            fail(e.getMessage());
        }
    }
    /**
     * test to retrieve poi remote
     */
    @Test
    public void retrieveTest(){

        try {
            PoiDao poiDao = new PoiDao();
            Poi testPoiUpdate = new Poi(POI_TEST_ID, "TestMatterhorn", "TestBerg", POI_TEST_BYTES_IMAGE, 53.53f, 53.53f, 1, 1, 9, false, 9);

            CountDownLatch doneSignal = new CountDownLatch(1);

            poiDao.retrieve(POI_TEST_ID, new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    switch (controllerEvent.getType()){
                        case OK:
                            doneSignal.countDown();
                            break;
                        default:
                            fail();
                    }
                }
            });
            //wait for async method
            doneSignal.await();
            //continue if arrived
            //given testPoi from Remote should be in Local database
            assertTrue(poiBox.getAll().contains(testPoiUpdate));

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    /**
     * test to add image remote
     */
    @Test
    public void addImageTest(){
        try {
            PoiDao poiDao = new PoiDao();
            Poi testPoiAddImage = new Poi(POI_TEST_ID, "TestMatterhorn", "TestBerg", POI_TEST_BYTES_IMAGE, 53.53f, 53.53f, 1, 1, 9, false, 9);

            int beforeCount = poiBox.getAll().get(POI_TEST_ID).getImageCount();

            CountDownLatch doneSignal = new CountDownLatch(1);
            poiDao.addImage(new File("testString"), testPoiAddImage, new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    switch (controllerEvent.getType()){
                        case OK:
                            doneSignal.countDown();
                            break;
                        default:
                            fail();
                    }
                }
            });

            //wait for async method
            doneSignal.await();
            //continue if arrived
            //given changed poi should not be in Local database because of changes
            assertFalse(poiBox.getAll().contains(testPoiAddImage));
            //check if image really was added
            assertTrue(poiBox.getAll().get(POI_TEST_ID).getImageCount() > beforeCount);
        } catch (Exception e){
            fail(e.getMessage());
        }
    }
    /**
     * test to delete image remote
     */
    @Test
    public void deleteImageTest(){
        try {
            PoiDao poiDao = new PoiDao();
            Poi testPoiAddImage = new Poi(POI_TEST_ID, "TestMatterhorn", "TestBerg", POI_TEST_BYTES_IMAGE, 53.53f, 53.53f, 1, 1, 9, false, 9);
            int beforeCount = poiBox.getAll().get(POI_TEST_ID).getImageCount();

            CountDownLatch doneSignal = new CountDownLatch(1);
            poiDao.deleteImage(POI_TEST_ID,0, new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    switch (controllerEvent.getType()){
                        case OK:
                            doneSignal.countDown();
                            break;
                        default:
                            fail();
                    }
                }
            });

            //wait for async method
            doneSignal.await();
            //continue if arrived
            //given changed poi should not be in Local database because of changes
            assertFalse(poiBox.getAll().contains(testPoiAddImage));
            //check if image really was deleted
            assertTrue(poiBox.getAll().get(POI_TEST_ID).getImageCount() < beforeCount);
        } catch (Exception e){
            fail(e.getMessage());
        }
    }
    /**
     * test to delete poi remote
     */
    @Test
    public void deleteTest(){
        try {
            PoiDao poiDao = new PoiDao();
            Poi testPoiDelete = new Poi(POI_TEST_ID, "TestMatterhorn", "TestBerg", POI_TEST_BYTES_IMAGE, 53.53f, 53.53f, 1, 1, 9, false, 9);

            CountDownLatch doneSignal = new CountDownLatch(1);
            poiDao.delete(testPoiDelete, new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    switch (controllerEvent.getType()){
                        case OK:
                            doneSignal.countDown();
                            break;
                        default:
                            fail();
                    }
                }
            });
            //wait for async method
            doneSignal.await();
            //continue if arrived
            //given testPoi should not be in Local database
            assertFalse(poiBox.getAll().contains(testPoiDelete));
        } catch (Exception e){
            fail(e.getMessage());
        }
    }
}

//        poiDao = new PoiDao(boxStore);
//
//    @Test
//    public void createTest(){
//        testPoi = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        poiDao.create(testPoi);
//        assertEquals("Matterhorn", poiQueryBuilder.equal(Poi_.title, "Matterhorn")
//                .build().findFirst().getTitle());
//
//    }
//
//        assertEquals("Matterhorn", poiQueryBuilder.equal(Poi_.title, "Matterhorn")
//                .build().findFirst().getTitle());
//
//    @Test
//    public void updateTest(){
//        poiBox.put(testPoi);
//        testPoi.setTitle("Matterhorn (VS)");
//        poiBox.put(testPoi);
//        assertEquals("Matterhorn (VS)", poiQueryBuilder.
//                equal(Poi_.poi_id, testPoi.getPoi_id()).build().findFirst().getTitle());
//    }
//        testPoi.setTitle("Matterhorn (VS)");

//    @Test
//    public void findOneTest(){
//                equal(Poi_.poi_id, testPoi.getPoi_id()).build().findFirst().getTitle());
//        poiBox.put(testPoi);
//        try {
//            assertEquals("Matterhorn", poiDao.findOne("name", "Matterhorn").getTitle());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//            assertEquals("Matterhorn", poiDao.findOne("name", "Matterhorn").getTitle());
//
//    @Test
//    public void findTest(){
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//
//        poiBox.put(poiOne);
//        poiBox.put(poiTwo);
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//
//        assertEquals(2, poiDao.find().size());
//    }
//
//    @Test
//    public void findDetailedTest(){
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Zugspitze", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiThree = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiFour = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Zugspitze", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiThree = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiFour = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        poiBox.put(poiOne);
//        poiBox.put(poiTwo);
//        poiBox.put(poiThree);
//        poiBox.put(poiFour);
//
//        try {
//            assertEquals(2, poiDao.find("name", "Mount Everest").size());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void countTest(){
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Zugspitze", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiThree = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiFour = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Zugspitze", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiThree = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiFour = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        poiBox.put(poiOne);
//        poiBox.put(poiTwo);
//        poiBox.put(poiThree);
//        poiBox.put(poiFour);
//
//        assertEquals(4, poiDao.count());
//    }
//
//    @Test
//    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Zugspitze", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiThree = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiFour = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Zugspitze", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiThree = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiFour = new Poi(0, "Mount Everest", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);

//
//        poiBox.put(poiOne);
//        poiBox.put(poiTwo);
//        poiBox.put(poiThree);
//        poiBox.put(poiFour);
//
//        assertEquals(2, poiDao.count("name", "Mount everest"));
//    }
//
//    @Test
//    public void deleteTest(){
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Zugspitze", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//
//        poiBox.put(poiOne);
//        poiBox.put(poiTwo);
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Zugspitze", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//
//        try {
//            poiDao.delete("name", "Matterhorn");
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//
//        assertEquals(1, poiDao.find().size());
//    }
//
//    @Test
//    public void deleteAllTest(){
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Zugspitze", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//
//        poiBox.put(poiOne);
//        poiBox.put(poiTwo);
//        Poi poiOne = new Poi(0, "Matterhorn", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//        Poi poiTwo = new Poi(0, "Zugspitze", "Berg", "picturePath", 53.53f, 53.53f, 1, 1, false);
//
//        poiDao.deleteAll();
//        assertEquals(0, poiDao.count());
//    }
//
//    @After
//    public void after() {
//        poiBox.removeAll();
//        boxStore.close();
//    }
//}
