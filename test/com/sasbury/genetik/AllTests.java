package test.com.sasbury.genetik;

import junit.framework.*;

public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for test.com.sasbury.genetik");
        //$JUnit-BEGIN$
        suite.addTestSuite(RunPropertiesTests.class);
        suite.addTestSuite(IndividualTests.class);
        suite.addTestSuite(ComparatorTests.class);
        suite.addTestSuite(XMLPopulationTests.class);
        suite.addTestSuite(GeneratorTests.class);
        suite.addTestSuite(ScalingTests.class);
        suite.addTestSuite(InsertionTests.class);
        suite.addTestSuite(SelectionTests.class);
        suite.addTestSuite(TerminationTests.class);
        suite.addTestSuite(SkipTestTests.class);
        suite.addTestSuite(ValidationTests.class);
        suite.addTestSuite(PBSValidationTests.class);
        suite.addTestSuite(JSONDecoderTests.class);
        suite.addTestSuite(JSONEncoderTests.class);
        suite.addTestSuite(JSONPopulationTests.class);
        suite.addTestSuite(TemplateTests.class);
        //$JUnit-END$
        return suite;
    }

}
