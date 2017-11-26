package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.model.DatabaseModel.Equipement;
import eu.wise_iot.wanderlust.model.DatabaseModel.Equipement_;
import eu.wise_iot.wanderlust.model.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.model.DatabaseObject.DifficultTypeDao;
import eu.wise_iot.wanderlust.model.DatabaseObject.EquipementDao;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


/**
 * @author Rilind Gashi
 */

@RunWith(AndroidJUnit4.class)
public class EquipementDaoTest {

    BoxStore boxStore;
    Box<Equipement> equipementBox;
    QueryBuilder<Equipement> equipementQueryBuilder;
    Equipement testEquipement;
    EquipementDao equipementDao;

    @Before
    public void setUpBefore(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        equipementDao = new EquipementDao(boxStore);
        equipementBox = boxStore.boxFor(Equipement.class);
        equipementQueryBuilder = equipementBox.query();
        testEquipement = new Equipement(0, "Regenschuhe", "Wasserdichte Schuhe");
    }

    @Test
    public void createTest(){
        equipementDao.create(testEquipement);
        assertEquals("Regenschuhe", equipementQueryBuilder.equal(Equipement_.name, "Regenschuhe")
                .build().findFirst().getName());
    }

    @Test
    public void updateTest(){
        equipementBox.put(testEquipement);
        testEquipement.setName("tiefe Regenschuhe");
        equipementBox.put(testEquipement);
        assertEquals("tiefe Regenschuhe", equipementQueryBuilder.equal(Equipement_.equipementId,
                testEquipement.getEquipementId()).build().findFirst().getName());
    }

    @Test
    public void findOneTest(){
        equipementBox.put(testEquipement);
        try {
            assertEquals("Regenschuhe", equipementDao.findOne("name", "Regenschuhe").getName());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void findTest(){
        Equipement equipementOne = new Equipement(0, "Regenschuhe", "Wasserdichte Schuhe");
        Equipement equipementTwo = new Equipement(0, "Regenjacke", "Vor Wasser schuetzende Jacke");

        equipementBox.put(equipementOne);
        equipementBox.put(equipementTwo);

        assertEquals(2, equipementDao.find().size());
    }

    @Test
    public void findDetailedTest(){
        Equipement equipementOne = new Equipement(0, "Regenschuhe", "Wasserdichte Schuhe");
        Equipement equipementTwo = new Equipement(0, "Regenjacke", "Vor Wasser schuetzende Jacke");
        Equipement equipementThree = new Equipement(0, "Rucksack", "dingsbumsda");
        Equipement equipementFour = new Equipement(0, "Rucksack", "Traghilfsmittel");

        equipementBox.put(equipementOne);
        equipementBox.put(equipementTwo);
        equipementBox.put(equipementThree);
        equipementBox.put(equipementFour);

        try {
            assertEquals(2, equipementDao.find("name", "Rucksack").size());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void countTest(){
        Equipement equipementOne = new Equipement(0, "Regenschuhe", "Wasserdichte Schuhe");
        Equipement equipementTwo = new Equipement(0, "Regenjacke", "Vor Wasser schuetzende Jacke");
        Equipement equipementThree = new Equipement(0, "Rucksack", "dingsbumsda");
        Equipement equipementFour = new Equipement(0, "Rucksack", "Traghilfsmittel");

        equipementBox.put(equipementOne);
        equipementBox.put(equipementTwo);
        equipementBox.put(equipementThree);
        equipementBox.put(equipementFour);

        assertEquals(4, equipementDao.count());
    }

    @Test
    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
        Equipement equipementOne = new Equipement(0, "Regenschuhe", "Wasserdichte Schuhe");
        Equipement equipementTwo = new Equipement(0, "Regenjacke", "Vor Wasser schuetzende Jacke");
        Equipement equipementThree = new Equipement(0, "Rucksack", "dingsbumsda");
        Equipement equipementFour = new Equipement(0, "Rucksack", "Traghilfsmittel");

        equipementBox.put(equipementOne);
        equipementBox.put(equipementTwo);
        equipementBox.put(equipementThree);
        equipementBox.put(equipementFour);

        assertEquals(2, equipementDao.count("name", "Rucksack"));
    }

    @Test
    public void deleteTest(){
        Equipement equipementOne = new Equipement(0, "Regenschuhe", "Wasserdichte Schuhe");
        Equipement equipementTwo = new Equipement(0, "Regenjacke", "Vor Wasser schuetzende Jacke");

        equipementBox.put(equipementOne);
        equipementBox.put(equipementTwo);
        try {
            equipementDao.delete("name", "Regenjacke");
        }catch (NoSuchFieldException | IllegalAccessException e){

        }

        assertEquals(1, equipementDao.find().size());
    }

    @Test
    public void deleteAllTest(){
        Equipement equipementOne = new Equipement(0, "Regenschuhe", "Wasserdichte Schuhe");
        Equipement equipementTwo = new Equipement(0, "Regenjacke", "Vor Wasser schuetzende Jacke");
        Equipement equipementThree = new Equipement(0, "Rucksack", "dingsbumsda");
        Equipement equipementFour = new Equipement(0, "Rucksack", "Traghilfsmittel");

        equipementBox.put(equipementOne);
        equipementBox.put(equipementTwo);
        equipementBox.put(equipementThree);
        equipementBox.put(equipementFour);

        equipementDao.deleteAll();
        assertEquals(0, equipementDao.count());
    }

    @After
    public void after() {
        equipementBox.removeAll();
        boxStore.close();
    }
}
