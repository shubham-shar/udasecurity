package com.udacity.catpoint.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.udacity.catpoint.imageService.service.FakeImageService;
import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import com.udacity.catpoint.security.data.PretendDatabaseSecurityRepositoryImpl;
import com.udacity.catpoint.security.data.SecurityRepository;
import com.udacity.catpoint.security.data.Sensor;
import com.udacity.catpoint.security.data.SensorType;
import com.udacity.catpoint.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author shubham sharma
 *         <p>
 *         20/12/20
 */
public class CatpointAppTests {
    
    public static final String TEST_SENSOR = "testSensor";
    
    @InjectMocks
    private SecurityService securityService;
    
    private SecurityRepository securityRepository;
    
    @Mock
    private FakeImageService imageService;
    
    @BeforeEach
    public void setup() throws BackingStoreException {
        Preferences prefs = Preferences.userNodeForPackage(PretendDatabaseSecurityRepositoryImpl.class);
        prefs.clear();
        MockitoAnnotations.initMocks(this);
        this.securityRepository = new PretendDatabaseSecurityRepositoryImpl();
        this.securityService = new SecurityService(securityRepository, imageService);
    }
    
    /*
    * If alarm is armed and a sensor becomes activated, put the system into pending alarm status.
    */
    @Test
    public void securityTestOne(){
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        securityService.setAlarmStatus(AlarmStatus.NO_ALARM);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        securityService.changeSensorActivationStatus(sensor, true);
        assertEquals(AlarmStatus.PENDING_ALARM, securityService.getAlarmStatus());
    }
    
    /*
    * If alarm is armed and a sensor becomes activated and the system is already pending alarm, set off the alarm.
    */
    @Test
    public void securityTestTwo(){
        securityService.setAlarmStatus(AlarmStatus.PENDING_ALARM);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        securityService.changeSensorActivationStatus(sensor, true);
        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }
    
    /*
     * If pending alarm and all sensors are inactive, return to no alarm state.
     */
    @Test
    public void securityTestThree(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        securityService.setAlarmStatus(AlarmStatus.PENDING_ALARM);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor, false);
        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }
    
    /*
     * If alarm is active, change in sensor state should not affect the alarm state.
     */
    @Test
    public void securityTestFour(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        securityService.setAlarmStatus(AlarmStatus.ALARM);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor, false);
        assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
        securityService.changeSensorActivationStatus(sensor, true);
        assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }
    
    /*
     * If a sensor is activated while already active and the system is in pending state, change it to alarm state.
     */
    @Test
    public void securityTestFive(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        securityService.setAlarmStatus(AlarmStatus.PENDING_ALARM);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(true);
        securityService.changeSensorActivationStatus(sensor, true);
        assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }
    
    /*
     * If a sensor is deactivated while already inactive, make no changes to the alarm state.
     */
    @Test
    public void securityTestSix(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        securityService.setAlarmStatus(AlarmStatus.NO_ALARM);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor, false);
        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }
    
    /*
     * If the image service identifies an image containing a cat while the system is armed-home,
     * put the system into alarm status.
     */
    @Test
    public void securityTestSeven(){
        BufferedImage currentCameraImage = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Mockito.when(imageService.imageContainsCat(ArgumentMatchers.any(), ArgumentMatchers.anyFloat()))
               .thenReturn(Boolean.TRUE);
        securityService.processImage(currentCameraImage);
        assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }
    
    /*
     * If the image service identifies an image that does not contain a cat,
     * change the status to no alarm as long as the sensors are not active.
     */
    @Test
    public void securityTestEight(){
        Mockito.when(imageService.imageContainsCat(ArgumentMatchers.any(), ArgumentMatchers.anyFloat()))
               .thenReturn(Boolean.FALSE);
        
        BufferedImage currentCameraImage = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(false);
        securityService.processImage(currentCameraImage);
        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }
    
    /*
     * If the system is disarmed, set the status to no alarm.
     */
    @Test
    public void securityTestNine(){
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }
    
    /*
     * If the system is armed, reset all sensors to inactive.
     */
    @Test
    public void securityTestTen(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        assertTrue(securityService.getSensors().stream().allMatch(sensor -> Boolean.TRUE.equals(sensor.getActive())));
    }
    
    /*
     * If the system is armed-home while the camera shows a cat, set the alarm status to alarm.
     */
    @Test
    public void securityTestEleven(){
        BufferedImage currentCameraImage = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Mockito.when(imageService.imageContainsCat(ArgumentMatchers.any(), ArgumentMatchers.anyFloat()))
               .thenReturn(Boolean.TRUE);
        securityService.processImage(currentCameraImage);
        assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }
}
