package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultType;
import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultType_;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultTypeDao;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


/**
 * @author Rilind Gashi
 */

@RunWith(AndroidJUnit4.class)
public class DifficultTypeDaoTest {

    BoxStore boxStore;
    Box<DifficultType> difficultyBox;
    QueryBuilder<DifficultType> difficultyQueryBuilder;
    DifficultType testDifficulty;
    DifficultTypeDao difficultTypeDao;

    @Before
    public void setUpBefore(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        difficultTypeDao = new DifficultTypeDao(boxStore);
        difficultyBox = boxStore.boxFor(DifficultType.class);
        difficultyQueryBuilder = difficultyBox.query();
        testDifficulty = new DifficultType(0, "T1");
    }

    @Test
    public void createTest(){
        difficultTypeDao.create(testDifficulty);
        assertEquals("T1", difficultyQueryBuilder.equal(DifficultType_.typename, "T1").build().findFirst().getName());
    }

    @Test
    public void updateTest(){
        difficultyBox.put(testDifficulty);
        testDifficulty.setName("T2");
        difficultyBox.put(testDifficulty);
        assertEquals("T2", difficultyQueryBuilder.equal(DifficultType_.id, testDifficulty.getDifft_id()).build().findFirst().getName());
    }

    @Test
    public void findOneTest(){
        difficultyBox.put(testDifficulty);
        try {
            assertEquals("T1", difficultTypeDao.findOne("identifier", "deviceIdentifier").getName());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void findTest(){
        DifficultType difficultyOne = new DifficultType(0, "T1");
        DifficultType difficultyTwo = new DifficultType(0, "T2");

        difficultyBox.put(difficultyOne);
        difficultyBox.put(difficultyTwo);

        assertEquals(2, difficultTypeDao.find().size());
    }

    @Test
    public void findDetailedTest(){
        DifficultType difficultyOne = new DifficultType(0, "T1");
        DifficultType difficultyTwo = new DifficultType(0, "T2");
        DifficultType difficultyThree = new DifficultType(0, "T3");
        DifficultType difficultyFour = new DifficultType(0, "T3");

        difficultyBox.put(difficultyOne);
        difficultyBox.put(difficultyTwo);
        difficultyBox.put(difficultyThree);
        difficultyBox.put(difficultyFour);

        try {
            assertEquals(2, difficultTypeDao.find("typename", "T3").size());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void countTest(){
        DifficultType difficultyOne = new DifficultType(0, "T1");
        DifficultType difficultyTwo = new DifficultType(0, "T2");
        DifficultType difficultyThree = new DifficultType(0, "T3");
        DifficultType difficultyFour = new DifficultType(0, "T3");

        difficultyBox.put(difficultyOne);
        difficultyBox.put(difficultyTwo);
        difficultyBox.put(difficultyThree);
        difficultyBox.put(difficultyFour);

        assertEquals(4, difficultTypeDao.count());
    }

    @Test
    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
        DifficultType difficultyOne = new DifficultType(0, "T1");
        DifficultType difficultyTwo = new DifficultType(0, "T2");
        DifficultType difficultyThree = new DifficultType(0, "T3");
        DifficultType difficultyFour = new DifficultType(0, "T3");

        difficultyBox.put(difficultyOne);
        difficultyBox.put(difficultyTwo);
        difficultyBox.put(difficultyThree);
        difficultyBox.put(difficultyFour);

        assertEquals(2, difficultTypeDao.count("typename", "T3"));
    }

    @Test
    public void deleteTest(){
        DifficultType difficultyOne = new DifficultType(0, "T1");
        DifficultType difficultyTwo = new DifficultType(0, "T2");

        difficultyBox.put(difficultyOne);
        difficultyBox.put(difficultyTwo);

        try {
            difficultTypeDao.delete("typename", "T1");
        }catch (NoSuchFieldException | IllegalAccessException e){

        }

        assertEquals(1, difficultTypeDao.find().size());
    }

    @Test
    public void deleteAllTest(){
        DifficultType difficultyOne = new DifficultType(0, "T1");
        DifficultType difficultyTwo = new DifficultType(0, "T2");

        difficultyBox.put(difficultyOne);
        difficultyBox.put(difficultyTwo);

        difficultTypeDao.deleteAll();
        assertEquals(0, difficultTypeDao.count());
    }

    @After
    public void after() {
        difficultyBox.removeAll();
        boxStore.close();
    }
}
