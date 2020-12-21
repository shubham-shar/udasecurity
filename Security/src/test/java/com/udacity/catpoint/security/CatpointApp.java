package com.udacity.catpoint.security;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import javax.naming.ldap.Control;
import javax.swing.*;

import com.udacity.catpoint.security.application.ControlPanel;
import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import com.udacity.catpoint.security.data.PretendDatabaseSecurityRepositoryImpl;
import com.udacity.catpoint.security.data.Sensor;
import com.udacity.catpoint.security.data.SensorType;
import com.udacity.catpoint.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * @author shubham sharma
 *         <p>
 *         20/12/20
 */
public class CatpointApp {
    public static final String TEST_SENSOR = "testSensor";
    private PretendDatabaseSecurityRepositoryImpl repository;
    private SecurityService securityService;
    private ControlPanel controlPanel;
    
    @BeforeEach
    public void init(){
        this.repository = new PretendDatabaseSecurityRepositoryImpl();
        this.securityService = new SecurityService(this.repository);
        this.controlPanel = new ControlPanel(this.securityService);
    }
    
    /*
    * If alarm is armed and a sensor becomes activated, put the system into pending alarm status.
    */
    @ParameterizedTest
    @CsvSource({"ARMED_HOME,true"})
    public void ControlPanelTest(ArmingStatus armingStatus, Boolean active) throws NoSuchFieldException,
                                                                           IllegalAccessException {
        Sensor sensor = new Sensor(TEST_SENSOR, SensorType.DOOR);
        sensor.setActive(active);
        Map<ArmingStatus, JButton> buttonMap = getButtonMap();
        
    }
    
    private Map<ArmingStatus, JButton> getButtonMap() throws NoSuchFieldException, IllegalAccessException {
        Field f = controlPanel.getClass().getDeclaredField("buttonMap"); //NoSuchFieldException
        f.setAccessible(true);
        return (Map<ArmingStatus, JButton>) f.get(controlPanel);
    }
}
