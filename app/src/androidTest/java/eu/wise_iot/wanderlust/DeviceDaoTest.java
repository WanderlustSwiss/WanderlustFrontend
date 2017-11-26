package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.models.DatabaseModel.Device;
import eu.wise_iot.wanderlust.models.DatabaseModel.Device_;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseObject.DeviceDao;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


/**
 * @author Rilind Gashi
 */

@RunWith(AndroidJUnit4.class)
public class DeviceDaoTest {

    BoxStore boxStore;
    Box<Device> deviceBox;
    QueryBuilder<Device> deviceQueryBuilder;
    Device testDevice;
    DeviceDao deviceDao;

    @Before
    public void setUpBefore(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        deviceDao = new DeviceDao(boxStore);
        deviceBox = boxStore.boxFor(Device.class);
        deviceQueryBuilder = deviceBox.query();
        testDevice = new Device(0, "deviceIdentifier", 1, 1);
    }

    @Test
    public void createTest(){
        deviceDao.create(testDevice);
        assertEquals("deviceIdentifier", deviceQueryBuilder.equal(Device_.identifier, "deviceIdentifier")
                .build().findFirst().getIdentifier());
    }

    @Test
    public void updateTest(){
        deviceBox.put(testDevice);
        testDevice.setIdentifier("UpdatedDeviceIdentifier");
        deviceBox.put(testDevice);
        assertEquals("UpdatedDeviceIdentifier", deviceQueryBuilder.
                equal(Device_.deviceId, testDevice.getDeviceId()).build().findFirst().getIdentifier());
    }

    @Test
    public void findOneTest(){
        deviceBox.put(testDevice);
        try {
            assertEquals("deviceIdentifier", deviceDao.findOne("identifier", "deviceIdentifier").getIdentifier());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void findTest(){
        Device deviceOne = new Device(0, "deviceIdentifierTwo", 2, 2);
        Device deviceTwo = new Device(0, "deviceIdentifierThree", 3, 3);

        deviceBox.put(deviceOne);
        deviceBox.put(deviceTwo);

        assertEquals(2, deviceDao.find().size());
    }

    @Test
    public void findDetailedTest(){
        Device deviceOne = new Device(0, "deviceIdentifierOne", 1, 1);
        Device deviceTwo = new Device(0, "deviceIdentifierTwo", 2, 2);
        Device deviceThree = new Device(0, "deviceIdentifierThree", 3, 3);
        Device deviceFour = new Device(0, "deviceIdentifierThree", 3, 3);

        deviceBox.put(deviceOne);
        deviceBox.put(deviceTwo);
        deviceBox.put(deviceThree);
        deviceBox.put(deviceFour);

        try {
            assertEquals(2, deviceDao.find("identifier", "deviceIdentifierThree").size());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void countTest(){
        Device deviceOne = new Device(0, "deviceIdentifierOne", 1, 1);
        Device deviceTwo = new Device(0, "deviceIdentifierTwo", 2, 2);
        Device deviceThree = new Device(0, "deviceIdentifierThree", 3, 3);
        Device deviceFour = new Device(0, "deviceIdentifierThree", 3, 3);

        deviceBox.put(deviceOne);
        deviceBox.put(deviceTwo);
        deviceBox.put(deviceThree);
        deviceBox.put(deviceFour);

        assertEquals(4, deviceDao.count());
    }

    @Test
    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
        Device deviceOne = new Device(0, "deviceIdentifierOne", 1, 1);
        Device deviceTwo = new Device(0, "deviceIdentifierTwo", 2, 2);
        Device deviceThree = new Device(0, "deviceIdentifierThree", 3, 3);
        Device deviceFour = new Device(0, "deviceIdentifierThree", 3, 3);

        deviceBox.put(deviceOne);
        deviceBox.put(deviceTwo);
        deviceBox.put(deviceThree);
        deviceBox.put(deviceFour);

        assertEquals(2, deviceDao.count("identifier", "deviceIdentifierThree"));
    }

    @Test
    public void deleteTest(){
        Device deviceOne = new Device(0, "deviceIdentifierThree", 3, 3);
        Device deviceTwo = new Device(0, "deviceIdentifierFour", 3, 3);

        deviceBox.put(deviceOne);
        deviceBox.put(deviceTwo);

        try {
            deviceDao.delete("identifier", "deviceIdentifierFour");
        }catch (NoSuchFieldException | IllegalAccessException e){

        }

        assertEquals(1, deviceDao.find().size());
    }

    @Test
    public void deleteAllTest(){
        Device deviceOne = new Device(0, "deviceIdentifierThree", 3, 3);
        Device deviceTwo = new Device(0, "deviceIdentifierFour", 3, 3);

        deviceBox.put(deviceOne);
        deviceBox.put(deviceTwo);

        deviceDao.deleteAll();
        assertEquals(0, deviceDao.count());
    }

    @After
    public void after() {
        deviceBox.removeAll();
        boxStore.close();
    }
}
