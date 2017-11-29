package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.models.DatabaseModel.TourKit;
import eu.wise_iot.wanderlust.models.DatabaseModel.RouteEquipement_;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseObject.TourKitDao;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


/**
 * @author Rilind Gashi
 */

@RunWith(AndroidJUnit4.class)
public class TourEquipmentDaoTest {

    BoxStore boxStore;
    Box<TourKit> routeEquipementBox;
    QueryBuilder<TourKit> routeEquipementQueryBuilder;
    TourKit testTourKit;
    TourKitDao tourKitDao;

    @Before
    public void setUpBefore(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        tourKitDao = new TourKitDao(boxStore);
        routeEquipementBox = boxStore.boxFor(TourKit.class);
        routeEquipementQueryBuilder = routeEquipementBox.query();
        testTourKit = new TourKit(0, 1, 1);
    }

    @Test
    public void createTest(){
        tourKitDao.create(testTourKit);
        assertEquals(1, routeEquipementQueryBuilder.equal(RouteEquipement_.routeEquipementId, 1)
                .build().findFirst().getRoute());
    }

    @Test
    public void updateTest(){
        routeEquipementBox.put(testTourKit);
        testTourKit.setRoute(2);
        routeEquipementBox.put(testTourKit);
        assertEquals(2, routeEquipementQueryBuilder.
                equal(RouteEquipement_.routeEquipementId, testTourKit.getrKit_id()).build().findFirst().getRoute());
    }

    @Test
    public void findOneTest(){
        routeEquipementBox.put(testTourKit);
        try {
            assertEquals(1, tourKitDao.findOne("routeId", "1").getRoute());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void findTest(){
        TourKit tourKitOne = new TourKit(0, 2, 2);
        TourKit tourKitTwo = new TourKit(0, 3, 3);

        routeEquipementBox.put(tourKitOne);
        routeEquipementBox.put(tourKitTwo);

        assertEquals(2, tourKitDao.find().size());
    }

    @Test
    public void findDetailedTest(){
        TourKit tourKitOne = new TourKit(0, 1, 1);
        TourKit tourKitTwo = new TourKit(0, 2, 2);
        TourKit tourKitThree = new TourKit(0, 3, 3);
        TourKit tourKitFour = new TourKit(0, 3, 3);

        routeEquipementBox.put(tourKitOne);
        routeEquipementBox.put(tourKitTwo);
        routeEquipementBox.put(tourKitThree);
        routeEquipementBox.put(tourKitFour);

        try {
            assertEquals(2, tourKitDao.find("routeId", "3").size());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void countTest(){
        TourKit tourKitOne = new TourKit(0, 1, 1);
        TourKit tourKitTwo = new TourKit(0, 2, 2);
        TourKit tourKitThree = new TourKit(0, 3, 3);
        TourKit tourKitFour = new TourKit(0, 3, 3);

        routeEquipementBox.put(tourKitOne);
        routeEquipementBox.put(tourKitTwo);
        routeEquipementBox.put(tourKitThree);
        routeEquipementBox.put(tourKitFour);

        assertEquals(4, tourKitDao.count());
    }

    @Test
    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
        TourKit tourKitOne = new TourKit(0, 1, 1);
        TourKit tourKitTwo = new TourKit(0, 2, 2);
        TourKit tourKitThree = new TourKit(0, 3, 3);
        TourKit tourKitFour = new TourKit(0, 3, 3);

        routeEquipementBox.put(tourKitOne);
        routeEquipementBox.put(tourKitTwo);
        routeEquipementBox.put(tourKitThree);
        routeEquipementBox.put(tourKitFour);

        assertEquals(2, tourKitDao.count("routeId", "3"));
    }

    @Test
    public void deleteTest(){
        TourKit tourKitOne = new TourKit(0, 1, 1);
        TourKit tourKitTwo = new TourKit(0, 2, 2);

        routeEquipementBox.put(tourKitOne);
        routeEquipementBox.put(tourKitTwo);

        try {
            tourKitDao.delete("routeId", "2");
        }catch (NoSuchFieldException | IllegalAccessException e){

        }

        assertEquals(1, tourKitDao.find().size());
    }

    @Test
    public void deleteAllTest(){
        TourKit tourKitOne = new TourKit(0, 1, 1);
        TourKit tourKitTwo = new TourKit(0, 2, 2);


        routeEquipementBox.put(tourKitOne);
        routeEquipementBox.put(tourKitTwo);

        tourKitDao.deleteAll();
        assertEquals(0, tourKitDao.count());
    }

    @After
    public void after() {
        routeEquipementBox.removeAll();
        boxStore.close();
    }
}
