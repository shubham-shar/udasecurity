package com.udacity.catpoint.security.service;

import com.udacity.catpoint.security.CatpointApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author shubham sharma
 *         <p>
 *         19/12/20
 */
@ExtendWith(MockitoExtension.class)
public class CatPointAppTest {

    @Test
    public void testCatPointApp(){
        CatpointApp.main(null);
    }
    
}
