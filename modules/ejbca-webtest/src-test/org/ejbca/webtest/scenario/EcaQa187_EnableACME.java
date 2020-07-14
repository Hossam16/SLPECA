/*************************************************************************
 *                                                                       *
 *  EJBCA Community: The OpenSource Certificate Authority                *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.ejbca.webtest.scenario;

import java.util.Collections;

import org.ejbca.webtest.WebTestBase;
import org.ejbca.webtest.helper.AuditLogHelper;
import org.ejbca.webtest.helper.SystemConfigurationHelper;
import org.ejbca.webtest.helper.SystemConfigurationHelper.SysConfigProtokols;
import org.ejbca.webtest.helper.SystemConfigurationHelper.SysConfigTabs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * This test verifies that enabling ACME works.
 * <br/>
 * Reference: <a href="https://jira.primekey.se/browse/ECAQA-187">ECAQA-187</a>
 * 
 * @version $Id$
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EcaQa187_EnableACME extends WebTestBase {
    
    private static WebDriver webDriver;
    private static AuditLogHelper auditLogHelper;
    private static SystemConfigurationHelper systemConfigurationHelper;

    private static final By AUDITLOG_ACME_ENABLED_XPATH = By.xpath("//span[contains(text(),'changed:ACME=true')]");
    
    @BeforeClass
    public static void init() {
        beforeClass(true, null);
        webDriver = getWebDriver();
        systemConfigurationHelper = new SystemConfigurationHelper(webDriver);
        auditLogHelper = new AuditLogHelper(webDriver);
        auditLogHelper.initFilterTime();
    }

    @AfterClass
    public static void exit() {
        afterClass();
    }

    /**
     * Disables ACME so it can be enabled in the next step.
     */
    @Test
    public void stepA_DisableAcme() {
        systemConfigurationHelper.openPage(getAdminWebUrl());
        systemConfigurationHelper.openTab(SysConfigTabs.PROTOCOLCONFIG);
        systemConfigurationHelper.disableProtocol(SysConfigProtokols.ACME);
        systemConfigurationHelper.assertProtocolDisabled(SysConfigProtokols.ACME);
    }
    
    @Test
    public void stepB_EnableAcme() {
        systemConfigurationHelper.enableProtocol(SysConfigProtokols.ACME);
        systemConfigurationHelper.assertProtocolEnabled(SysConfigProtokols.ACME);
    }

    @Test
    public void stepC_CheckAuditLog() {
        auditLogHelper.openPage(getAdminWebUrl());
        auditLogHelper.reloadView();
        WebElement addedElement = webDriver.findElement(AUDITLOG_ACME_ENABLED_XPATH);
        auditLogHelper.assertLogEntryByEventText("System Configuration Edit", "Success", null, Collections.singletonList(addedElement.getText()));
    }
}