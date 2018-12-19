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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.cesecore.authorization.AuthorizationDeniedException;
import org.ejbca.core.ejb.ra.CouldNotRemoveEndEntityException;
import org.ejbca.core.ejb.ra.NoSuchEndEntityException;
import org.ejbca.webtest.WebTestBase;
import org.ejbca.webtest.helper.EndEntityProfileHelper;
import org.ejbca.webtest.helper.SystemConfigurationHelper;
import org.ejbca.webtest.helper.SystemConfigurationHelper.SysConfigTabs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * 
 * @version $Id$
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EcaQa59_EEPHidden extends WebTestBase {
    
    private static final String eepName = "Hidden";
    private static final String endEntityName = "TestEndEnityHidden";
    private static String currentDateString;
    private static String oneMonthsFromNowString;
    private static WebDriver webDriver;
    
    // Helpers
    private static SystemConfigurationHelper sysConfigHelper;
    private static EndEntityProfileHelper eeProfileHelper;
    
    @BeforeClass
    public static void init() {
        Date currentDate = new Date();
        Calendar oneMonthsFromNow = Calendar.getInstance();
        oneMonthsFromNow.setTime(currentDate);
        oneMonthsFromNow.add(Calendar.MONTH, 1);
        currentDateString = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
        oneMonthsFromNowString = new SimpleDateFormat("yyyy-MM-dd").format(oneMonthsFromNow.getTime());
        beforeClass(true, null);
        webDriver = getWebDriver();
        sysConfigHelper = new SystemConfigurationHelper(webDriver);
        eeProfileHelper = new EndEntityProfileHelper(webDriver);
    }

    @AfterClass
    public static void exit() throws NoSuchEndEntityException, AuthorizationDeniedException, CouldNotRemoveEndEntityException {
        removeEndEntityByUsername(endEntityName);
        removeEndEntityProfileByName(eepName);
        webDriver.quit();
    }

    /** Attempts to enable key recovery through system configuration in the Admin Web. */
    @Test
    public void testA_enableKeyRecovery() {
        sysConfigHelper.openPage(getAdminWebUrl());
        sysConfigHelper.openTab(SysConfigTabs.BASICCONFIG);
        sysConfigHelper.triggerEnableKeyRecovery(true);
        sysConfigHelper.saveBasicConfiguration();
        sysConfigHelper.assertEnableKeyRecoveryEnabled(true);
    }

    @Test
    public void testB_addEndEntityProfile() {
        eeProfileHelper.openPage(getAdminWebUrl());
        eeProfileHelper.addEndEntityProfile(eepName);
        eeProfileHelper.openEditEndEntityProfilePage(eepName);
        // Set all desired values in EEP. Set values will be validated in next step (add end entity)
        Select dropDownCa =  new Select(webDriver.findElement(By.xpath("//select[@name='selectdefaultca']")));
        dropDownCa.selectByVisibleText(getCaName());
        webDriver.findElement(By.id("checkboxusemaxfailedlogins")).click();
        webDriver.findElement(By.id("checkboxsusetarttime")).click();
        webDriver.findElement(By.id("checkboxuseendtime")).click();
        webDriver.findElement(By.xpath("//input[@name='textfieldstarttime']")).sendKeys(currentDateString);
        webDriver.findElement(By.xpath("//input[@name='textfieldendtime']")).sendKeys(oneMonthsFromNowString);
        webDriver.findElement(By.id("checkboxusencpermitted")).click();
        webDriver.findElement(By.id("checkboxuseextensiondata")).click();
        webDriver.findElement(By.id("checkboxuseallowedrequests")).click();
        webDriver.findElement(By.id("checkboxusekeyrecoverable")).click();
        webDriver.findElement(By.id("checkboxuseissuancerevocationreason")).click();
        webDriver.findElement(By.id("checkboxusesendnotification")).click();
        webDriver.findElement(By.xpath("//input[@name='buttonaddnotification']")).click();
        webDriver.findElement(By.xpath("//input[@name='textfieldnotificationsender']")).sendKeys("sender@example.com");
        webDriver.findElement(By.xpath("//input[@name='textfieldnotificationsubject']")).sendKeys("test subject");
        webDriver.findElement(By.xpath("//textarea[@name='textareanotificationmessage']")).sendKeys("test message");
        // Save configuration
        webDriver.findElement(By.xpath("//input[@name='buttonsave']")).click();
        WebElement tableResult = webDriver.findElement(By.xpath("//table[@class='list']/tbody/tr/td"));
        assertEquals("Status text 'End Entity Profile saved' could not be found after saving EEP", "End Entity Profile saved.", tableResult.getText());
    }

    @Test
    public void testC_checkEep() {
        webDriver.get(getAdminWebUrl());
        WebElement addEeLink = webDriver.findElement(By.id("raAddendentity"));
        addEeLink.click();

        // Check expected values preset by EEP
        Select dropDownEepPreSelect =  new Select(webDriver.findElement(By.xpath("//select[@name='selectendentityprofile']")));
        dropDownEepPreSelect.selectByVisibleText(eepName);
        Select dropDownEep =  new Select(webDriver.findElement(By.xpath("//select[@name='selectendentityprofile']")));
        Select dropDownCp =  new Select(webDriver.findElement(By.xpath("//select[@name='selectcertificateprofile']")));
        Select dropDownToken =  new Select(webDriver.findElement(By.xpath("//select[@name='selecttoken']")));
        Select dropDownNumOfAllowedRequests =  new Select(webDriver.findElement(By.xpath("//select[@name='selectallowedrequests']")));
        Select dropDownRevocationReason =  new Select(webDriver.findElement(By.xpath("//select[@name='selectissuancerevocationreason']")));
        assertTrue("End entity profile: " + eepName + " was not selected", dropDownEep.getAllSelectedOptions().get(0).getText().equals(eepName));
        assertTrue("Maximum number of failed login attempts not set to 'Unlimited'", webDriver.findElement(By.id("radiomaxfailedloginsunlimited")).isSelected());
        assertTrue("CP 'ENDUSER' was not selected by default", dropDownCp.getAllSelectedOptions().get(0).getText().equals("ENDUSER"));
        assertTrue("Token type 'User Generated' was not selected by default", dropDownToken.getAllSelectedOptions().get(0).getText().equals("User Generated"));
        assertTrue("Certificate Validity Start Time was not set to todays date", webDriver.findElement(By.xpath("//input[@name='textfieldstarttime']")).getAttribute("value").contains(currentDateString));
        assertTrue("Certificate Validity End Time was not set to 1 month from todays date", webDriver.findElement(By.xpath("//input[@name='textfieldendtime']")).getAttribute("value").contains(oneMonthsFromNowString));
        assertTrue("Number of allowed requests was not set to '1' by default", dropDownNumOfAllowedRequests.getAllSelectedOptions().get(0).getText().equals("1"));
        assertTrue("Revocation reason to set after certificate issuance was not set to'Active' by default", dropDownRevocationReason.getAllSelectedOptions().get(0).getText().equals("Active"));
        assertTrue("Key recovery was not activated", webDriver.findElement(By.id("checkboxkeyrecoverable")).getAttribute("value").equals("true"));

        // Check presence of expected but empty fields / attributes
        try {
            webDriver.findElement(By.xpath("//input[@name='textfieldusername']"));
            webDriver.findElement(By.xpath("//input[@name='textfieldpassword']"));
            webDriver.findElement(By.xpath("//input[@name='textfieldconfirmpassword']"));
            webDriver.findElement(By.xpath("//input[@name='textfieldemail']"));
            webDriver.findElement(By.xpath("//input[@name='textfieldemaildomain']"));
            webDriver.findElement(By.xpath("//input[@name='textfieldsubjectdn0']"));
            webDriver.findElement(By.xpath("//textarea[@name='textarencpermitted']"));
            webDriver.findElement(By.xpath("//textarea[@name='textareaextensiondata']"));
        } catch (NoSuchElementException e) {
            fail("Expected attribute not found: " + e.getMessage());
        }
    }

    // TODO If EjbcaMail isn't available in appserver this test errors. Should fail test immediately in that case
    @Test
    public void testD_addEndEntity() {

        webDriver.get(getAdminWebUrl());
        WebElement configLink = webDriver.findElement(By.id("raAddendentity"));
        configLink.click();

        Select dropDownEepPreSelect =  new Select(webDriver.findElement(By.xpath("//select[@name='selectendentityprofile']")));
        dropDownEepPreSelect.selectByVisibleText(eepName);
        Select dropDownCaPreSelect =  new Select(webDriver.findElement(By.xpath("//select[@name='selectca']")));
        dropDownCaPreSelect.selectByVisibleText(getCaName());
        webDriver.findElement(By.xpath("//input[@name='textfieldusername']")).sendKeys(endEntityName);
        webDriver.findElement(By.xpath("//input[@name='textfieldpassword']")).sendKeys("foo123");
        webDriver.findElement(By.xpath("//input[@name='textfieldconfirmpassword']")).sendKeys("foo123");
        webDriver.findElement(By.xpath("//input[@name='textfieldemail']")).sendKeys("sender");
        webDriver.findElement(By.xpath("//input[@name='textfieldemaildomain']")).sendKeys("example.com");
        webDriver.findElement(By.xpath("//input[@name='textfieldsubjectdn0']")).sendKeys(endEntityName);
        webDriver.findElement(By.xpath("//textarea[@name='textarencpermitted']")).sendKeys(
                "example.com\n" +
                "198.51.100.0/24\n" +
                "CN=Name,O=Company @example.com"
        );
        webDriver.findElement(By.xpath("//textarea[@name='textareaextensiondata']")).sendKeys("Other Data");
        webDriver.findElement(By.id("checkboxsendnotification")).click();
        webDriver.findElement(By.xpath("//input[@name='buttonadduser']")).click();
        WebElement messageInfo = webDriver.findElement(By.xpath("//div[@class='message info']"));
        assertEquals("Unexpected status text after adding end entity","End Entity " + endEntityName + " added successfully.", messageInfo.getText());
    }
}
