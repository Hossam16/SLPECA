/*************************************************************************
 *                                                                       *
 *  EJBCA - Proprietary Modules: Enterprise Certificate Authority        *
 *                                                                       *
 *  Copyright (c), PrimeKey Solutions AB. All rights reserved.           *
 *  The use of the Proprietary Modules are subject to specific           * 
 *  commercial license terms.                                            *
 *                                                                       *
 *************************************************************************/

package org.ejbca.ui.web.rest.api.resource;

import static org.ejbca.ui.web.rest.api.Assert.EjbcaAssert.assertJsonContentType;
import static org.ejbca.ui.web.rest.api.Assert.EjbcaAssert.assertProperJsonStatusResponse;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;
import org.ejbca.config.GlobalConfiguration;
import org.ejbca.its.ca.model.enroll.EcaEnrollResponseCode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A set of system tests for CaRestResource ('').
 */
public class CaRestResourceSystemTest extends RestResourceSystemTestBase {
    
    private static final Logger log = Logger.getLogger(CaRestResourceSystemTest.class);

    @BeforeClass
    public static void beforeClass() throws Exception {
        RestResourceSystemTestBase.beforeClass();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        RestResourceSystemTestBase.afterClass();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    //@Test
    public void shouldReturnStatusInformation() throws Exception {
        // given
        final String expectedStatus = "OK";
        final String expectedVersion = "1.0";
        final String expectedRevision = GlobalConfiguration.EJBCA_VERSION;
        // when
        final Response actualResponse = newRequest("/v1/ca/status").request().get();
        final String actualJsonString = actualResponse.readEntity(String.class);
        // then
        assertEquals(Status.OK.getStatusCode(), actualResponse.getStatus());
        assertJsonContentType(actualResponse);
        assertProperJsonStatusResponse(expectedStatus, expectedVersion, expectedRevision, actualJsonString);
    }
    
    @Test
    public void shouldReturnStatusInformation2() throws Exception {
        // given
        final String expectedStatus = "OK";
        final String expectedVersion = "1.0";
        final String expectedRevision = GlobalConfiguration.EJBCA_VERSION;
        
        byte[] bytebody = Hex.decode("ABCDEF123456ABCD04");
        Entity<byte[]> requestEntity = Entity.entity(bytebody, "application/x-its-request");
        // when
        final Response actualResponse = newRequest("/its/etsi/enroll-certificate",
                getBaseUrl().replace("ejbca-rest-api", "")).request().post(requestEntity);
        InputStream is = actualResponse.readEntity(InputStream.class);
        log.info(Hex.toHexString(is.readAllBytes()));
        log.info(actualResponse.getHeaders());
        log.info(EcaEnrollResponseCode.DECRYPTION_FAILED.toString());
        
        // then
        assertEquals(Status.OK.getStatusCode(), actualResponse.getStatus());
        assertJsonContentType(actualResponse);
        //assertProperJsonStatusResponse(expectedStatus, expectedVersion, expectedRevision, actualJsonString);
    }

    /**
     * Disables REST and then runs a simple REST access test which will expect status 403 when
     * service is disabled by configuration.
     * @throws Exception 
     */
    //@Test
    public void shouldRestrictAccessToRestResourceIfProtocolDisabled() throws Exception {
        // given
        disableRestProtocolConfiguration();
        // when
        final Response actualResponse = newRequest("/v1/ca/status").request().get();
        final int status = actualResponse.getStatus();
        // then
        assertEquals("Unexpected response after disabling protocol", 403, status);
        // restore state
        enableRestProtocolConfiguration();
    }
}
