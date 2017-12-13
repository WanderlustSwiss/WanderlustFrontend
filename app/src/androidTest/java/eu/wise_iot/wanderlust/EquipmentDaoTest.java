//package eu.wise_iot.wanderlust;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.junit.Assert.*;
//
//import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
//import eu.wise_iot.wanderlust.models.DatabaseModel.Equipement_;
//import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
//import eu.wise_iot.wanderlust.models.DatabaseObject.EquipmentDao;
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
//@Ignore
//public class EquipmentDaoTest {
//
//    BoxStore boxStore;
//    Box<Equipment> equipementBox;
//    QueryBuilder<Equipment> equipementQueryBuilder;
//    Equipment testEquipment;
//    EquipmentDao equipmentDao;
//
//    @Before
//    public void setUpBefore(){
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        boxStore = MyObjectBox.builder().androidContext(appContext).build();
//        equipmentDao = new EquipmentDao(boxStore);
//        equipementBox = boxStore.boxFor(Equipment.class);
//        equipementQueryBuilder = equipementBox.query();
//        testEquipment = new Equipment(0, "Regenschuhe", "Wasserdichte Schuhe");
//    }
//
//    @Test
//    public void createTest(){
//        equipmentDao.create(testEquipment);
//        assertEquals("Regenschuhe", equipementQueryBuilder.equal(Equipement_.name, "Regenschuhe")
//                .build().findFirst().getName());
//    }
//
//    @Test
//    public void updateTest(){
//        equipementBox.put(testEquipment);
//        testEquipment.setName("tiefe Regenschuhe");
//        equipementBox.put(testEquipment);
//        assertEquals("tiefe Regenschuhe", equipementQueryBuilder.equal(Equipement_.equipementId,
//                testEquipment.getEquipementId()).build().findFirst().getName());
//    }
//
//    @Test
//    public void findOneTest(){
//        equipementBox.put(testEquipment);
//        try {
//            assertEquals("Regenschuhe", equipmentDao.findOne("name", "Regenschuhe").getName());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void findTest(){
//        Equipment equipmentOne = new Equipment(0, "Regenschuhe", "Wasserdichte Schuhe");
//        Equipment equipmentTwo = new Equipment(0, "Regenjacke", "Vor Wasser schuetzende Jacke");
//
//        equipementBox.put(equipmentOne);
//        equipementBox.put(equipmentTwo);
//
//        assertEquals(2, equipmentDao.find().size());
//    }
//
//    @Test
//    public void findDetailedTest(){
//        Equipment equipmentOne = new Equipment(0, "Regenschuhe", "Wasserdichte Schuhe");
//        Equipment equipmentTwo = new Equipment(0, "Regenjacke", "Vor Wasser schuetzende Jacke");
//        Equipment equipmentThree = new Equipment(0, "Rucksack", "dingsbumsda");
//        Equipment equipmentFour = new Equipment(0, "Rucksack", "Traghilfsmittel");
//
//        equipementBox.put(equipmentOne);
//        equipementBox.put(equipmentTwo);
//        equipementBox.put(equipmentThree);
//        equipementBox.put(equipmentFour);
//
//        try {
//            assertEquals(2, equipmentDao.find("name", "Rucksack").size());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void countTest(){
//        Equipment equipmentOne = new Equipment(0, "Regenschuhe", "Wasserdichte Schuhe");
//        Equipment equipmentTwo = new Equipment(0, "Regenjacke", "Vor Wasser schuetzende Jacke");
//        Equipment equipmentThree = new Equipment(0, "Rucksack", "dingsbumsda");
//        Equipment equipmentFour = new Equipment(0, "Rucksack", "Traghilfsmittel");
//
//        equipementBox.put(equipmentOne);
//        equipementBox.put(equipmentTwo);
//        equipementBox.put(equipmentThree);
//        equipementBox.put(equipmentFour);
//
//        assertEquals(4, equipmentDao.count());
//    }
//
//    @Test
//    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
//        Equipment equipmentOne = new Equipment(0, "Regenschuhe", "Wasserdichte Schuhe");
//        Equipment equipmentTwo = new Equipment(0, "Regenjacke", "Vor Wasser schuetzende Jacke");
//        Equipment equipmentThree = new Equipment(0, "Rucksack", "dingsbumsda");
//        Equipment equipmentFour = new Equipment(0, "Rucksack", "Traghilfsmittel");
//
//        equipementBox.put(equipmentOne);
//        equipementBox.put(equipmentTwo);
//        equipementBox.put(equipmentThree);
//        equipementBox.put(equipmentFour);
//
//        assertEquals(2, equipmentDao.count("name", "Rucksack"));
//    }
//
//    @Test
//    public void deleteTest(){
//        Equipment equipmentOne = new Equipment(0, "Regenschuhe", "Wasserdichte Schuhe");
//        Equipment equipmentTwo = new Equipment(0, "Regenjacke", "Vor Wasser schuetzende Jacke");
//
//        equipementBox.put(equipmentOne);
//        equipementBox.put(equipmentTwo);
//        try {
//            equipmentDao.delete("name", "Regenjacke");
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//
//        assertEquals(1, equipmentDao.find().size());
//    }
//
//    @Test
//    public void deleteAllTest(){
//        Equipment equipmentOne = new Equipment(0, "Regenschuhe", "Wasserdichte Schuhe");
//        Equipment equipmentTwo = new Equipment(0, "Regenjacke", "Vor Wasser schuetzende Jacke");
//        Equipment equipmentThree = new Equipment(0, "Rucksack", "dingsbumsda");
//        Equipment equipmentFour = new Equipment(0, "Rucksack", "Traghilfsmittel");
//
//        equipementBox.put(equipmentOne);
//        equipementBox.put(equipmentTwo);
//        equipementBox.put(equipmentThree);
//        equipementBox.put(equipmentFour);
//
//        equipmentDao.deleteAll();
//        assertEquals(0, equipmentDao.count());
//    }
//
//    @After
//    public void after() {
//        equipementBox.removeAll();
//        boxStore.close();
//    }
//}
