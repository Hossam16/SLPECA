package se.anatom.ejbca.ca.store.junit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import junit.framework.*;


/**
 * main test class
 *
 * @version $Id: TestRunner.java,v 1.7 2004-03-14 13:50:07 herrvendil Exp $
 */
public class TestRunner extends Object {
    private static Logger log = Logger.getLogger(TestRunner.class);

    /**
     * main
     *
     * @param args cmd line args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
    }

    private void cleanUp() {
        log.debug(">cleanUp()");
        log.debug("<cleanUp()");
    }

    /**
     * sets up test suite
     *
     * @return none
     */
    public static Test suite() {
        log.debug(">suite()");

        TestSuite suite = new TestSuite();
        suite.addTest(new TestSuite(TestCertificateData.class));
		suite.addTest( new TestSuite( TestCertificateRetrival.class ));
		suite.addTest( new TestSuite( TestCertificateProfile.class ));
        log.debug("<suite()");

        return suite;
    }
}
