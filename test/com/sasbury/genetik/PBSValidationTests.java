package test.com.sasbury.genetik;

import java.util.*;

import com.sasbury.genetik.driver.cluster.pbs.*;

import junit.framework.*;

public class PBSValidationTests extends TestCase
{
    public void testEmptyProps()
    {
        Properties props = ValidationTests.genTestProps();
        PBSValidator validator = new PBSValidator();
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length,5);
    }
    
    public void testPartialProps()
    {
        Properties props = ValidationTests.genTestProps();
        PBSValidator validator = new PBSValidator();
        String[] issues = validator.validate(props);
        assertEquals(issues.length,5);
        
        props.setProperty(PBSJobDriver.EST_SCORE_TIME, "1:0:0");
        issues = validator.validate(props);
        assertEquals(issues.length,4);
        
        props.setProperty(PBSJobDriver.EST_JOB_TIME, "1:0:0");
        issues = validator.validate(props);
        assertEquals(issues.length,3);
        
        props.setProperty(PBSJobDriver.EMAIL, "test");
        issues = validator.validate(props);
        assertEquals(issues.length,2);
        
        props.setProperty(PBSJobDriver.CLUSTER_ID, "test");
        issues = validator.validate(props);
        assertEquals(issues.length,1);
        
        props.setProperty(PBSJobDriver.CLASS_PATH, "test");
        issues = validator.validate(props);
        assertEquals(issues.length,0);
    }

    public void testValidProps()
    {
        Properties props = ValidationTests.genTestProps();
        PBSValidator validator = new PBSValidator();
        props.setProperty(PBSJobDriver.EST_SCORE_TIME, "1:0:0");
        props.setProperty(PBSJobDriver.EST_JOB_TIME, "1:0:0");
        props.setProperty(PBSJobDriver.EMAIL, "test");
        props.setProperty(PBSJobDriver.CLUSTER_ID, "test");
        props.setProperty(PBSJobDriver.CLASS_PATH, "test");
        
        String[] issues = validator.validate(props);
        
        assertEquals(issues.length,0);
    }
}

