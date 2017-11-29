package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.models.DatabaseModel.History;
import eu.wise_iot.wanderlust.models.DatabaseModel.History_;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseObject.HistoryDao;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


/**
 * @author Rilind Gashi
 */

@RunWith(AndroidJUnit4.class)
public class HistoryDaoTest {

    BoxStore boxStore;
    Box<History> historyBox;
    QueryBuilder<History> historyQueryBuilder;
    History testHistory;
    HistoryDao historyDao;

    @Before
    public void setUpBefore(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        historyDao = new HistoryDao(boxStore);
        historyBox = boxStore.boxFor(History.class);
        historyQueryBuilder = historyBox.query();
        testHistory = new History(0, 1);
    }

    @Test
    public void createTest(){
        historyDao.create(testHistory);
        assertEquals(1, historyQueryBuilder.equal(History_.absolvedRoute, 1)
                .build().findFirst().getAbsolvedRoute());
    }

    @Test
    public void updateTest(){
        historyBox.put(testHistory);
        testHistory.setAbsolvedRoute(2);
        historyBox.put(testHistory);
        assertEquals(2, historyQueryBuilder.
                equal(History_.historyId, testHistory.getHistory_id()).build().findFirst().getAbsolvedRoute());
    }

    @Test
    public void findOneTest(){
        historyBox.put(testHistory);
        try {
            assertEquals(1, historyDao.findOne("historyId", "1").getAbsolvedRoute());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void findTest(){
        History historyOne = new History(0, 2);
        History historyTwo = new History(0, 3);

        historyBox.put(historyOne);
        historyBox.put(historyTwo);

        assertEquals(2, historyDao.find().size());
    }

    @Test
    public void findDetailedTest(){
        History historyOne = new History(0, 1);
        History historyTwo = new History(0, 2);
        History historyThree = new History(0, 3);
        History historyFour = new History(0, 3);

        historyBox.put(historyOne);
        historyBox.put(historyTwo);
        historyBox.put(historyThree);
        historyBox.put(historyFour);

        try {
            assertEquals(2, historyDao.find("absolvedRoute", "3").size());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void countTest(){
        History historyOne = new History(0, 1);
        History historyTwo = new History(0, 2);
        History historyThree = new History(0, 3);
        History historyFour = new History(0, 3);

        historyBox.put(historyOne);
        historyBox.put(historyTwo);
        historyBox.put(historyThree);
        historyBox.put(historyFour);

        assertEquals(4, historyDao.count());
    }

    @Test
    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
        History historyOne = new History(0, 1);
        History historyTwo = new History(0, 2);
        History historyThree = new History(0, 3);
        History historyFour = new History(0, 3);

        historyBox.put(historyOne);
        historyBox.put(historyTwo);
        historyBox.put(historyThree);
        historyBox.put(historyFour);


        assertEquals(2, historyDao.count("absolvedRoute", "3"));
    }

    @Test
    public void deleteTest(){
        History historyOne = new History(0, 1);
        History historyTwo = new History(0, 2);

        historyBox.put(historyOne);
        historyBox.put(historyTwo);

        try {
            historyDao.delete("absolvedRoute", "2");
        }catch (NoSuchFieldException | IllegalAccessException e){

        }

        assertEquals(1, historyDao.find().size());
    }

    @Test
    public void deleteAllTest(){
        History historyOne = new History(0, 1);
        History historyTwo = new History(0, 2);

        historyBox.put(historyOne);
        historyBox.put(historyTwo);

        historyDao.deleteAll();
        assertEquals(0, historyDao.count());
    }

    @After
    public void after() {
        historyBox.removeAll();
        boxStore.close();
    }
}
