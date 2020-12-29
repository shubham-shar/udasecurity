package com.udacity.catpoint.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * @author shubham sharma
 *         <p>
 *         19/12/20
 */
public class ArmingStatusTest {
    
    // Any new alarm has to be added to the test case
    
    @ParameterizedTest
    @EnumSource(ArmingStatus.class)
    public void armingStatusCheck(ArmingStatus status) {
        Set<ArmingStatus> armingStatusSet = EnumSet.of(ArmingStatus.ARMED_HOME, ArmingStatus.ARMED_AWAY,
                                                     ArmingStatus.DISARMED);
        assertTrue(armingStatusSet.contains(status));
    }
}
