package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.models.DatabaseModel.RouteKit;
import eu.wise_iot.wanderlust.models.DatabaseModel.RouteEquipement_;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseObject.RouteKitDao;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


/**
 * @author Rilind Gashi
 */

@RunWith(AndroidJUnit4.class)
public class RouteEquipmentDaoTest {

    BoxStore boxStore;
    Box<RouteKit> routeEquipementBox;
    QueryBuilder<RouteKit> routeEquipementQueryBuilder;
    RouteKit testRouteKit;
    RouteKitDao routeKitDao;

    @Before
    public void setUpBefore(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        routeKitDao = new RouteKitDao(boxStore);
        routeEquipementBox = boxStore.boxFor(RouteKit.class);
        routeEquipementQueryBuilder = routeEquipementBox.query();
        testRouteKit = new RouteKit(0, 1, 1);
    }

    @Test
    public void createTest(){
        routeKitDao.create(testRouteKit);
        assertEquals(1, routeEquipementQueryBuilder.equal(RouteEquipement_.routeEquipementId, 1)
                .build().findFirst().getRoute());
    }

    @Test
    public void updateTest(){
        routeEquipementBox.put(testRouteKit);
        testRouteKit.setRoute(2);
        routeEquipementBox.put(testRouteKit);
        assertEquals(2, routeEquipementQueryBuilder.
                equal(RouteEquipement_.routeEquipementId, testRouteKit.getrKit_id()).build().findFirst().getRoute());
    }

    @Test
    public void findOneTest(){
        routeEquipementBox.put(testRouteKit);
        try {
            assertEquals(1, routeKitDao.findOne("routeId", "1").getRoute());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void findTest(){
        RouteKit routeKitOne = new RouteKit(0, 2, 2);
        RouteKit routeKitTwo = new RouteKit(0, 3, 3);

        routeEquipementBox.put(routeKitOne);
        routeEquipementBox.put(routeKitTwo);

        assertEquals(2, routeKitDao.find().size());
    }

    @Test
    public void findDetailedTest(){
        RouteKit routeKitOne = new RouteKit(0, 1, 1);
        RouteKit routeKitTwo = new RouteKit(0, 2, 2);
        RouteKit routeKitThree = new RouteKit(0, 3, 3);
        RouteKit routeKitFour = new RouteKit(0, 3, 3);

        routeEquipementBox.put(routeKitOne);
        routeEquipementBox.put(routeKitTwo);
        routeEquipementBox.put(routeKitThree);
        routeEquipementBox.put(routeKitFour);

        try {
            assertEquals(2, routeKitDao.find("routeId", "3").size());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void countTest(){
        RouteKit routeKitOne = new RouteKit(0, 1, 1);
        RouteKit routeKitTwo = new RouteKit(0, 2, 2);
        RouteKit routeKitThree = new RouteKit(0, 3, 3);
        RouteKit routeKitFour = new RouteKit(0, 3, 3);

        routeEquipementBox.put(routeKitOne);
        routeEquipementBox.put(routeKitTwo);
        routeEquipementBox.put(routeKitThree);
        routeEquipementBox.put(routeKitFour);

        assertEquals(4, routeKitDao.count());
    }

    @Test
    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
        RouteKit routeKitOne = new RouteKit(0, 1, 1);
        RouteKit routeKitTwo = new RouteKit(0, 2, 2);
        RouteKit routeKitThree = new RouteKit(0, 3, 3);
        RouteKit routeKitFour = new RouteKit(0, 3, 3);

        routeEquipementBox.put(routeKitOne);
        routeEquipementBox.put(routeKitTwo);
        routeEquipementBox.put(routeKitThree);
        routeEquipementBox.put(routeKitFour);

        assertEquals(2, routeKitDao.count("routeId", "3"));
    }

    @Test
    public void deleteTest(){
        RouteKit routeKitOne = new RouteKit(0, 1, 1);
        RouteKit routeKitTwo = new RouteKit(0, 2, 2);

        routeEquipementBox.put(routeKitOne);
        routeEquipementBox.put(routeKitTwo);

        try {
            routeKitDao.delete("routeId", "2");
        }catch (NoSuchFieldException | IllegalAccessException e){

        }

        assertEquals(1, routeKitDao.find().size());
    }

    @Test
    public void deleteAllTest(){
        RouteKit routeKitOne = new RouteKit(0, 1, 1);
        RouteKit routeKitTwo = new RouteKit(0, 2, 2);


        routeEquipementBox.put(routeKitOne);
        routeEquipementBox.put(routeKitTwo);

        routeKitDao.deleteAll();
        assertEquals(0, routeKitDao.count());
    }

    @After
    public void after() {
        routeEquipementBox.removeAll();
        boxStore.close();
    }
}
