package com.udacity.catpoint.security;

import static org.mockito.Mockito.when;

import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import com.udacity.catpoint.security.data.SecurityRepository;
import com.udacity.catpoint.security.data.Sensor;
import com.udacity.catpoint.security.data.SensorType;
import com.udacity.catpoint.security.service.SecurityService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author shubham sharma
 *         <p>
 *         19/12/20
 */
@ExtendWith(MockitoExtension.class)
public class SecurityServiceTests {
    
    @InjectMocks
    SecurityService securityService;
    
    @Mock
    SecurityRepository securityRepository;
    
    // All Tests should not throw error
    
    @ParameterizedTest
    @EnumSource(ArmingStatus.class)
    public void setArmingStatusTest(ArmingStatus status){
        securityService.setArmingStatus(status);
    }
    
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void catDetectedTest(Boolean cat){
        securityService.catDetected(cat);
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
         Sensor sensor = new Sensor("udacitySensor", sensorType);
         sensor.setActive(true);
         when(securityRepository.getAlarmStatus()).thenReturn(alarmStatus);
         securityService.changeSensorActivationStatus(sensor, active);
         sensor.setActive(false);
         securityService.changeSensorActivationStatus(sensor, active);
    }
}
