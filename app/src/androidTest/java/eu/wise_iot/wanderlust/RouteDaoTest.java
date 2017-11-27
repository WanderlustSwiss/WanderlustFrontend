package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.models.DatabaseModel.Route;
import eu.wise_iot.wanderlust.models.DatabaseModel.Route_;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseObject.RouteDao;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


/**
 * @author Rilind Gashi
 */

@RunWith(AndroidJUnit4.class)
public class RouteDaoTest {

    BoxStore boxStore;
    Box<Route> routeBox;
    QueryBuilder<Route> routeQueryBuilder;
    Route testRoute;
    RouteDao routeDao;

    @Before
    public void setUpBefore(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        routeDao = new RouteDao(boxStore);
        routeBox = boxStore.boxFor(Route.class);
        routeQueryBuilder = routeBox.query();
        testRoute = new Route(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
    }

    @Test
    public void createTest(){
        routeDao.create(testRoute);
        assertEquals("Tour1", routeQueryBuilder.equal(Route_.title, "Tour1")
                .build().findFirst().getTitle());
    }

    @Test
    public void updateTest(){
        routeBox.put(testRoute);
        testRoute.setTitle("UpdatedRouteTitle");
        routeBox.put(testRoute);
        assertEquals("UpdatedRouteTitle", routeQueryBuilder.
                equal(Route_.routeId, testRoute.getRouteId()).build().findFirst().getTitle());
    }

    @Test
    public void findOneTest(){
        routeBox.put(testRoute);
        try {
            assertEquals("Tour1", routeDao.findOne("title", "Tour1").getTitle());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void findTest(){
        Route routeOne = new Route(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeTwo = new Route(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);

        routeBox.put(routeOne);
        routeBox.put(routeTwo);

        assertEquals(2, routeDao.find().size());
    }

    @Test
    public void findDetailedTest(){
        Route routeOne = new Route(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeTwo = new Route(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeThree = new Route(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeFour = new Route(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);

        routeBox.put(routeOne);
        routeBox.put(routeTwo);
        routeBox.put(routeThree);
        routeBox.put(routeFour);

        try {
            assertEquals(2, routeDao.find("title", "Tour3").size());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void countTest(){
        Route routeOne = new Route(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeTwo = new Route(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeThree = new Route(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeFour = new Route(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);

        routeBox.put(routeOne);
        routeBox.put(routeTwo);
        routeBox.put(routeThree);
        routeBox.put(routeFour);

        assertEquals(4, routeDao.count());
    }

    @Test
    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
        Route routeOne = new Route(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeTwo = new Route(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeThree = new Route(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeFour = new Route(0, "Tour3", "TourDescription", "picturePath", "polyline", 1,1, true);

        routeBox.put(routeOne);
        routeBox.put(routeTwo);
        routeBox.put(routeThree);
        routeBox.put(routeFour);

        assertEquals(2, routeDao.count("title", "Tour3"));
    }

    @Test
    public void deleteTest(){
        Route routeOne = new Route(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeTwo = new Route(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);

        routeBox.put(routeOne);
        routeBox.put(routeTwo);

        try {
            routeDao.delete("title", "Tour1");
        }catch (NoSuchFieldException | IllegalAccessException e){

        }

        assertEquals(1, routeDao.find().size());
    }

    @Test
    public void deleteAllTest(){
        Route routeOne = new Route(0, "Tour1", "TourDescription", "picturePath", "polyline", 1,1, true);
        Route routeTwo = new Route(0, "Tour2", "TourDescription", "picturePath", "polyline", 1,1, true);

        routeBox.put(routeOne);
        routeBox.put(routeTwo);

        routeDao.deleteAll();
        assertEquals(0, routeDao.count());
    }

    @After
    public void after() {
        routeBox.removeAll();
        boxStore.close();
    }
}
