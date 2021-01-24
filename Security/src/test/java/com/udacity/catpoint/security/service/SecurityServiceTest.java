package com.udacity.catpoint.security.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.prefs.BackingStoreException;

import com.udacity.catpoint.imageService.service.ImageService;
import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import com.udacity.catpoint.security.data.PretendDatabaseSecurityRepositoryImpl;
import com.udacity.catpoint.security.data.Sensor;
import com.udacity.catpoint.security.data.SensorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author shubham sharma
 *         <p>
 *         20/12/20
 */
@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {
    
    public static final String TEST_SENSOR = "testSensor";
    
    @InjectMocks
    private SecurityService securityService;
    
    @Mock
    private PretendDatabaseSecurityRepositoryImpl securityRepository;
    
    @Mock
    private ImageService imageService;
    
    /*
    * If alarm is armed and a sensor becomes activated, put the system into pending alarm status.
    */
    @Test
    public void securityTestOne(){
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        securityService.setAlarmStatus(AlarmStatus.NO_ALARM);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
    }
    
    /*
    * If alarm is armed and a sensor becomes activated and the system is already pending alarm, set on the alarm.
    */
    @Test
    public void securityTestTwo(){
        securityService.setAlarmStatus(AlarmStatus.PENDING_ALARM);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
    }
    
    /*
     * If pending alarm and all sensors are inactive, return to no alarm state.
     */
    @Test
    public void securityTestThree() {
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        securityService.setAlarmStatus(AlarmStatus.PENDING_ALARM);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(false);
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, false);
    }
    
    /*
     * If alarm is active, change in sensor state should not affect the alarm state.
     */
    @Test
    public void securityTestFour(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        securityService.setAlarmStatus(AlarmStatus.ALARM);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(true);
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.ALARM);
        securityService.changeSensorActivationStatus(sensor, false);
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
        Mockito.when(securityRepository.getAlarmStatus())
                                      .thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
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
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, false);
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
    }
    
    /*
     * If the image service identifies an image that does not contain a cat,
     * change the status to no alarm as long as the sensors are not active.
     */
    @Test
    public void securityTestEight() throws BackingStoreException {
        Mockito.when(imageService.imageContainsCat(ArgumentMatchers.any(), ArgumentMatchers.anyFloat()))
               .thenReturn(Boolean.FALSE);
        BufferedImage currentCameraImage = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(false);
        securityService.processImage(currentCameraImage);
    }
    
    /*
     * If the system is disarmed, set the status to no alarm.
     */
    @Test
    public void securityTestNine(){
        securityService.setArmingStatus(ArmingStatus.DISARMED);
    }
    
    /*
     * If the system is armed, reset all sensors to inactive.
     */
    @Test
    public void securityTestTen(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        assertTrue(securityService.getSensors().stream().allMatch(sensor1 -> Boolean.FALSE.equals(sensor1.getActive())));
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
    }
    
    
    //****************************** ADDTIONAL TESTS ******************************//
    
    @ParameterizedTest
    @EnumSource(ArmingStatus.class)
    public void setArmingStatusTest(ArmingStatus status){
        securityService.setArmingStatus(status);
    }
    
    @ParameterizedTest
    @EnumSource(AlarmStatus.class)
    public void setAlarmStatusTest(AlarmStatus alarmStatus){
        securityService.setAlarmStatus(alarmStatus);
    }
    
    @ParameterizedTest
    @CsvSource({ "NO_ALARM,DOOR,true", "NO_ALARM,DOOR,false", "NO_ALARM,WINDOW,true", "NO_ALARM,WINDOW,false",
            "NO_ALARM,MOTION,true", "NO_ALARM,MOTION,false","PENDING_ALARM,DOOR,true", "PENDING_ALARM,DOOR,false",
            "PENDING_ALARM,WINDOW,true", "PENDING_ALARM,WINDOW,false", "PENDING_ALARM,MOTION,true",
            "PENDING_ALARM,MOTION,false" })
    public void changeSensorActivationStatusTest(AlarmStatus alarmStatus, SensorType sensorType, Boolean active){
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.PENDING_ALARM);
        Sensor sensor = new Sensor("udacitySensor", sensorType);
        sensor.setActive(true);
        securityService.changeSensorActivationStatus(sensor, active);
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor, active);
    }
    
    @Test
    public void testHandleSensorActivatedWithSensorActive() {
        Sensor sensor = new Sensor("udacitySensor", SensorType.DOOR);
        sensor.setActive(false);
        Mockito.when(securityRepository.getArmingStatus())
               .thenReturn(ArmingStatus.DISARMED);
        securityService.changeSensorActivationStatus(sensor, true);
        assertTrue(sensor.getActive());
    }
    
    @Test
    public void testDandleSensorDeactivated() {
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.PENDING_ALARM);
        Sensor sensor = new Sensor("udacitySensor", SensorType.DOOR);
        sensor.setActive(true);
        securityService.changeSensorActivationStatus(sensor, false);
        assertFalse(sensor.getActive());
    }
}
