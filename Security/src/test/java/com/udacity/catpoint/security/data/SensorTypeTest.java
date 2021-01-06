package com.udacity.catpoint.security.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import com.udacity.catpoint.security.data.SensorType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * @author shubham sharma
 *         <p>
 *         19/12/20
 */
public class SensorTypeTest {
    @ParameterizedTest
    @EnumSource(SensorType.class)
    public void SensorTypeCheck(SensorType sensorType){
        Set<SensorType> sensorTypeSet = EnumSet.of(SensorType.DOOR, SensorType.MOTION, SensorType.WINDOW);
        assertTrue(sensorTypeSet.contains(sensorType));
    }
}
