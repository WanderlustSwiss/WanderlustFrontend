package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.models.DatabaseModel.RouteEquipement;
import eu.wise_iot.wanderlust.models.DatabaseModel.RouteEquipement_;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseObject.RouteEquipementDao;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


/**
 * @author Rilind Gashi
 */

@RunWith(AndroidJUnit4.class)
public class RouteEquipementDaoTest {

    BoxStore boxStore;
    Box<RouteEquipement> routeEquipementBox;
    QueryBuilder<RouteEquipement> routeEquipementQueryBuilder;
    RouteEquipement testRouteEquipement;
    RouteEquipementDao routeEquipementDao;

    @Before
    public void setUpBefore(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        routeEquipementDao = new RouteEquipementDao(boxStore);
        routeEquipementBox = boxStore.boxFor(RouteEquipement.class);
        routeEquipementQueryBuilder = routeEquipementBox.query();
        testRouteEquipement = new RouteEquipement(0, 1, 1);
    }

    @Test
    public void createTest(){
        routeEquipementDao.create(testRouteEquipement);
        assertEquals(1, routeEquipementQueryBuilder.equal(RouteEquipement_.routeEquipementId, 1)
                .build().findFirst().getRouteId());
    }

    @Test
    public void updateTest(){
        routeEquipementBox.put(testRouteEquipement);
        testRouteEquipement.setRouteId(2);
        routeEquipementBox.put(testRouteEquipement);
        assertEquals(2, routeEquipementQueryBuilder.
                equal(RouteEquipement_.routeEquipementId, testRouteEquipement.getRouteEquipementId()).build().findFirst().getRouteId());
    }

    @Test
    public void findOneTest(){
        routeEquipementBox.put(testRouteEquipement);
        try {
            assertEquals(1, routeEquipementDao.findOne("routeId", "1").getRouteId());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void findTest(){
        RouteEquipement routeEquipementOne = new RouteEquipement(0, 2, 2);
        RouteEquipement routeEquipementTwo = new RouteEquipement(0, 3, 3);

        routeEquipementBox.put(routeEquipementOne);
        routeEquipementBox.put(routeEquipementTwo);

        assertEquals(2, routeEquipementDao.find().size());
    }

    @Test
    public void findDetailedTest(){
        RouteEquipement routeEquipementOne = new RouteEquipement(0, 1, 1);
        RouteEquipement routeEquipementTwo = new RouteEquipement(0, 2, 2);
        RouteEquipement routeEquipementThree = new RouteEquipement(0, 3, 3);
        RouteEquipement routeEquipementFour = new RouteEquipement(0, 3, 3);

        routeEquipementBox.put(routeEquipementOne);
        routeEquipementBox.put(routeEquipementTwo);
        routeEquipementBox.put(routeEquipementThree);
        routeEquipementBox.put(routeEquipementFour);

        try {
            assertEquals(2, routeEquipementDao.find("routeId", "3").size());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void countTest(){
        RouteEquipement routeEquipementOne = new RouteEquipement(0, 1, 1);
        RouteEquipement routeEquipementTwo = new RouteEquipement(0, 2, 2);
        RouteEquipement routeEquipementThree = new RouteEquipement(0, 3, 3);
        RouteEquipement routeEquipementFour = new RouteEquipement(0, 3, 3);

        routeEquipementBox.put(routeEquipementOne);
        routeEquipementBox.put(routeEquipementTwo);
        routeEquipementBox.put(routeEquipementThree);
        routeEquipementBox.put(routeEquipementFour);

        assertEquals(4, routeEquipementDao.count());
    }

    @Test
    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
        RouteEquipement routeEquipementOne = new RouteEquipement(0, 1, 1);
        RouteEquipement routeEquipementTwo = new RouteEquipement(0, 2, 2);
        RouteEquipement routeEquipementThree = new RouteEquipement(0, 3, 3);
        RouteEquipement routeEquipementFour = new RouteEquipement(0, 3, 3);

        routeEquipementBox.put(routeEquipementOne);
        routeEquipementBox.put(routeEquipementTwo);
        routeEquipementBox.put(routeEquipementThree);
        routeEquipementBox.put(routeEquipementFour);

        assertEquals(2, routeEquipementDao.count("routeId", "3"));
    }

    @Test
    public void deleteTest(){
        RouteEquipement routeEquipementOne = new RouteEquipement(0, 1, 1);
        RouteEquipement routeEquipementTwo = new RouteEquipement(0, 2, 2);

        routeEquipementBox.put(routeEquipementOne);
        routeEquipementBox.put(routeEquipementTwo);

        try {
            routeEquipementDao.delete("routeId", "2");
        }catch (NoSuchFieldException | IllegalAccessException e){

        }

        assertEquals(1, routeEquipementDao.find().size());
    }

    @Test
    public void deleteAllTest(){
        RouteEquipement routeEquipementOne = new RouteEquipement(0, 1, 1);
        RouteEquipement routeEquipementTwo = new RouteEquipement(0, 2, 2);


        routeEquipementBox.put(routeEquipementOne);
        routeEquipementBox.put(routeEquipementTwo);

        routeEquipementDao.deleteAll();
        assertEquals(0, routeEquipementDao.count());
    }

    @After
    public void after() {
        routeEquipementBox.removeAll();
        boxStore.close();
    }
}
