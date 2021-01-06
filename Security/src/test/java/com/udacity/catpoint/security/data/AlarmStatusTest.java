package com.udacity.catpoint.security.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import com.udacity.catpoint.security.data.AlarmStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * @author shubham sharma
 *         <p>
 *         19/12/20
 */
public class AlarmStatusTest {
    
    // Any new alarm has to be added to the test case
    
    @ParameterizedTest
    @EnumSource(AlarmStatus.class)
    public void alarmStatusCheck(AlarmStatus status) {
        Set<AlarmStatus> alarmStatusSet = EnumSet.of(AlarmStatus.ALARM, AlarmStatus.NO_ALARM,
                                                     AlarmStatus.PENDING_ALARM);
        assertTrue(alarmStatusSet.contains(status));
    }
}
