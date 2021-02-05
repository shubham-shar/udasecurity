package com.udacity.catpoint.security.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.awt.image.BufferedImage;
import java.util.Collections;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * @author shubham sharma
 *         <p>
 *         20/12/20
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SecurityServiceTest {
    
    public static final String TEST_SENSOR = "testSensor";
    
    @InjectMocks
    private SecurityService securityService;
    
    @Mock
    private PretendDatabaseSecurityRepositoryImpl securityRepository;
    
    @Mock
    private ImageService imageService;
    
    /*
    * 1. If alarm is armed and a sensor becomes activated, put the system into pending alarm status.
    */
    @Test
    public void armedAlarm_activatedSensor_pendingAlarmResult(){
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(false);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
        Mockito.verify(securityRepository, Mockito.times(1))
               .setAlarmStatus(any(AlarmStatus.class));
    }
    
    /*
    * 2. If alarm is armed and a sensor becomes activated and the system is already pending alarm, set on the alarm.
    */
    @Test
    public void armedAlarm_activatedSensor_pendingAlarm_AlarmResult(){
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
        Mockito.verify(securityRepository, Mockito.times(1))
             .setAlarmStatus(any(AlarmStatus.class));
    }
    
    /*
     * 3. If pending alarm and all sensors are inactive, return to no alarm state.
     */
    @Test
    public void pendingAlarmWithInactiveSensors_noAlarmResult() {
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(false);
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.setAlarmStatus(AlarmStatus.PENDING_ALARM);
        Mockito.verify(securityRepository, Mockito.times(1))
               .setAlarmStatus(any(AlarmStatus.class));
    }
    
    /*
     * 4. If alarm is active, change in sensor state should not affect the alarm state.
     */
    @Test
    public void activeAlarm_noAffectOnAlarmIfSensorStateChanged(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(true);
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.ALARM);
        securityService.changeSensorActivationStatus(sensor, false);
        Mockito.verify(securityRepository, Mockito.times(0))
               .setAlarmStatus(any(AlarmStatus.class));
    }
    
    /*
     * 5. If a sensor is activated while already active and the system is in pending state, change it to alarm state.
     */
    @Test
    public void activateActiveSensor_whileSystemInPendingState_alarmResult(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(true);
        Mockito.when(securityRepository.getAlarmStatus())
                                      .thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
        Mockito.verify(securityRepository, Mockito.times(1))
               .setAlarmStatus(any(AlarmStatus.class));
    }
    
    /*
     * 6. If a sensor is deactivated while already inactive, make no changes to the alarm state.
     */
    @Test
    public void deactivateInActiveSensor_noChangeInAlarm(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(false);
        Mockito.when(securityRepository.getAlarmStatus())
               .thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, false);
        Mockito.verify(securityRepository, Mockito.times(0))
               .setAlarmStatus(any(AlarmStatus.class));
    }
    
    /*
     * 7. If the image service identifies an image containing a cat while the system is armed-home,
     * put the system into alarm status.
     */
    @Test
    public void catDetected_systemArmed_resultInAlarm(){
        BufferedImage currentCameraImage = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Mockito.when(imageService.imageContainsCat(any(), ArgumentMatchers.anyFloat()))
               .thenReturn(Boolean.TRUE);
        Mockito.when(securityRepository.getArmingStatus())
                                                .thenReturn(ArmingStatus.ARMED_HOME);
        securityService.processImage(currentCameraImage);
        Mockito.verify(securityRepository, Mockito.times(1))
               .setAlarmStatus(any(AlarmStatus.class));
    }
    
    /*
     * 8. If the image service identifies an image that does not contain a cat,
     * change the status to no alarm as long as the sensors are not active.
     */
    @Test
    public void catNotDetected_sensorsNotActive_changeToAlarm() throws BackingStoreException {
        Mockito.when(imageService.imageContainsCat(any(), ArgumentMatchers.anyFloat()))
               .thenReturn(Boolean.FALSE);
        BufferedImage currentCameraImage = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(false);
        securityService.processImage(currentCameraImage);
        Mockito.verify(securityRepository, Mockito.times(1))
               .setAlarmStatus(any(AlarmStatus.class));
    }
    
    /*
     * 9. If the system is disarmed, set the status to no alarm.
     */
    @Test
    public void systemDisarmed_changeToNoAlarm(){
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        Mockito.verify(securityRepository, Mockito.times(1))
               .setAlarmStatus(any(AlarmStatus.class));
    }
    
    /*
     * 10. If the system is armed, reset all sensors to inactive.
     */
    @Test
    public void systemArmed_deactivateAllSensors(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        assertTrue(securityService.getSensors().stream().allMatch(sensor1 -> Boolean.FALSE.equals(sensor1.getActive())));
    }
    
    /*
     * 11. If the system is armed-home while the camera shows a cat, set the alarm status to alarm.
     */
    @Test
    public void armedHome_catDetected_changeToAlarm(){
        BufferedImage currentCameraImage = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Mockito.when(imageService.imageContainsCat(any(), ArgumentMatchers.anyFloat()))
               .thenReturn(Boolean.TRUE);
        securityService.processImage(currentCameraImage);
        Mockito.verify(securityRepository, Mockito.times(1))
               .setAlarmStatus(any(AlarmStatus.class));
    }
    
    
    //****************************** ADDTIONAL TESTS ******************************//
    
    @ParameterizedTest
    @EnumSource(ArmingStatus.class)
    public void setArmingStatusMethod(ArmingStatus status){
        securityService.setArmingStatus(status);
    }
    
    @ParameterizedTest
    @EnumSource(AlarmStatus.class)
    public void setAlarmStatusMethod(AlarmStatus alarmStatus){
        securityService.setAlarmStatus(alarmStatus);
    }
    
    @ParameterizedTest
    @CsvSource({ "NO_ALARM,DOOR,true", "NO_ALARM,DOOR,false", "NO_ALARM,WINDOW,true", "NO_ALARM,WINDOW,false",
            "NO_ALARM,MOTION,true", "NO_ALARM,MOTION,false","PENDING_ALARM,DOOR,true", "PENDING_ALARM,DOOR,false",
            "PENDING_ALARM,WINDOW,true", "PENDING_ALARM,WINDOW,false", "PENDING_ALARM,MOTION,true",
            "PENDING_ALARM,MOTION,false" })
    public void changeSensorActivationStatusWithAllAlarms(AlarmStatus alarmStatus, SensorType sensorType,
            Boolean active){
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
    
    @Test
    public void updateSensorWhenArmed() {
        ArmingStatus armingStatus = ArmingStatus.ARMED_HOME;
        Sensor sensor = new Sensor("udacitySensor", SensorType.DOOR);
        sensor.setActive(true);
        Mockito.when(securityRepository.getSensors())
               .thenReturn(Collections.singleton(sensor));
        securityService.setArmingStatus(armingStatus);
       Mockito.verify(securityRepository, Mockito.times(1)).updateSensor(any());
    }
}
