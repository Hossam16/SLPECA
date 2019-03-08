package org.ejbca.webtest.scenario;

import org.apache.commons.lang.StringUtils;
import org.cesecore.authorization.AuthorizationDeniedException;
import org.ejbca.webtest.WebTestBase;
import org.ejbca.webtest.helper.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Asserts whether the blacklist validator permits a site based on the
 * blacklist.txt file.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EcaQa201_PositiveBlacklistValidator extends WebTestBase {

    private static WebDriver webDriver;
    // Helpers
    private static ValidatorsHelper validatorsHelper;
    private static CaHelper caHelper;
    private static ApprovalProfilesHelper approvalProfilesHelperDefault;
    private static CertificateProfileHelper certificateProfileHelper;
    private static AuditLogHelper auditLogHelper;
    private static EndEntityProfileHelper eeProfileHelper;
    private static RaWebHelper raWebHelper;

    // Test Data
    private static class TestData {
        private static final Date currentDate = new Date();
        private static final Calendar oneMonthsFromNow = Calendar.getInstance();

        private static final String EJBCA_HOME = System.getenv("EJBCA_HOME");
        private static final String VALIDATOR_NAME = "EcaQa201_Blacklist";
        private static final String VALIDATOR_BLACKLIST_FILENAME = EJBCA_HOME + "/modules/ejbca-webtest/resources/Blacklist.txt";

        private static final String CA_NAME = "EcaQa201_P_CA";
        private static final String CA_VALIDITY = "1y";
        private static final String APPROVAL_PROFILE_NAME = "EcaQa201_P_ApprovalProfile";
        private static final String APPROVAL_PROFILE_TYPE_PARTITIONED_APPROVAL = "Partitioned Approval";
        private static final String CERTIFICATE_PROFILE_NAME = "ECAQA-201-CertificateProfile";
        private static final String ROLE_NAME = "ECAQA201_AdminRole1";
        private static final String currentDateString = (new SimpleDateFormat("yyyy-MM-dd")).format(currentDate);
        private static final String oneMonthsFromNowString = (new SimpleDateFormat("yyyy-MM-dd")).format(oneMonthsFromNow.getTime());
        static final String[] CERTIFICATE_REQUEST_PEM = new String[]{"-----BEGIN CERTIFICATE REQUEST-----", "MIICZzCCAU8CAQAwIjELMAkGA1UEBhMCVVMxEzARBgNVBAMMClJlc3RyaWN0Q04w", "ggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDwyIsyw3HB+8yxOF9BOfjG", "zLoQIX7sLg1lXk1miLyU6wYmuLnZfZrr4pjZLyEr2iP92IE97DeK/8y2827qctPM", "y4axmczlRTrEZKI/bVXnLOrQNw1dE+OVHiVoRFa5i4TS/qfhNA/Gy/eKpzxm8LT7", "+folAu92HwbQ5H8fWQ/l+ysjTheLMyUDaK83+NvYAL9Gfl29EN/TTrRzLKWoXrlB", "Ed7PT2oCBgrvF7pHsrry2O3yuuO2hoF5RQTo9BdBaGvzxGdweYTvdoLWfZm1zGI+", "CW0lprBdjagCC4XAcWi5OFcxjrRA9WA6Cu1q4Hn+eJEdCNHVvqss2rz6LOWjAQAr", "AgMBAAGgADANBgkqhkiG9w0BAQsFAAOCAQEA1JlwrFN4ihTZWICnWFb/kzcmvjcs", "0xeerNZQAEk2FJgj+mKVNrqCRWr2iaPpAeggH8wFoZIh7OvhmIZNmxScw4K5HhI9", "SZD+Z1Dgkj8+bLAQaxvw8sxXLdizcMNvbaXbzwbAN9OUkXPavBlik/b2JLafcEMM", "8IywJOtJMWemfmLgR7KAqDj5520wmXgAK6oAbbMqWUip1vz9oIisv53n2HFq2jzq", "a5d2WKBq5pJY19ztQ17HwlGTI8it4rlKYn8p2fDuqxLXiBsX8906E/cFRN5evhWt", "zdJ6yvdw3HQsoVAVi0GDHTs2E8zWFoYyP0byzKSSvkvQR363LQ0bik4cuQ==", "-----END CERTIFICATE REQUEST-----"};


    }

    @BeforeClass
    public static void init() {
        // super
        beforeClass(true, null);
        webDriver = getWebDriver();
        // Init helpers
        validatorsHelper = new ValidatorsHelper(webDriver);
        caHelper = new CaHelper(webDriver);
        approvalProfilesHelperDefault = new ApprovalProfilesHelper(webDriver);
    }

    @AfterClass
    public static void exit() throws AuthorizationDeniedException {
        // Remove generated artifacts
        removeCaAndCryptoToken(EcaQa201_PositiveBlacklistValidator.TestData.CA_NAME);
        removeValidatorByName(TestData.VALIDATOR_NAME);
        // super
        afterClass();
    }


    @Test
    public void stepA_AddAValidator() {
        validatorsHelper.openPage(getAdminWebUrl());
        validatorsHelper.addValidator(TestData.VALIDATOR_NAME);
        validatorsHelper.assertValidatorNameExists(TestData.VALIDATOR_NAME);
    }

    @Test
    public void stepB_EditAValidator() {
        validatorsHelper.openPage(getAdminWebUrl());
        validatorsHelper.openEditValidatorPage(TestData.VALIDATOR_NAME);
        validatorsHelper.setValidatorType("Domain Blacklist Validator");
        validatorsHelper.setBlacklistFile(TestData.VALIDATOR_BLACKLIST_FILENAME);
        validatorsHelper.saveValidator();
        validatorsHelper.assertValidatorNameExists(TestData.VALIDATOR_NAME);
    }


    @Test
    public void stepC_CreateCA() {
        caHelper.openPage(getAdminWebUrl());
        caHelper.addCa(EcaQa201_PositiveBlacklistValidator.TestData.CA_NAME);
        caHelper.setValidity(EcaQa201_PositiveBlacklistValidator.TestData.CA_VALIDITY);
        caHelper.setOtherData(TestData.VALIDATOR_NAME);
        caHelper.createCa();
        caHelper.assertExists(EcaQa201_PositiveBlacklistValidator.TestData.CA_NAME);
    }

    @Test
    public void stepD_AddApprovalProfile() throws InterruptedException {
        approvalProfilesHelperDefault.openPage(getAdminWebUrl());
        approvalProfilesHelperDefault.addApprovalProfile(TestData.APPROVAL_PROFILE_NAME);
        approvalProfilesHelperDefault.openEditApprovalProfilePage(TestData.APPROVAL_PROFILE_NAME);
        approvalProfilesHelperDefault.setApprovalProfileType(TestData.APPROVAL_PROFILE_TYPE_PARTITIONED_APPROVAL);
        approvalProfilesHelperDefault.setApprovalStepPartitionApprovePartitionRole(0, 0,
                TestData.ROLE_NAME);
        approvalProfilesHelperDefault.saveApprovalProfile();

    }
    /*
    @Test
    public void stepD_AddCertificateProfile() {
        // Update default timestamp
        auditLogHelper.initFilterTime();
        // Add Certificate Profile
        certificateProfileHelper.openPage(getAdminWebUrl());
        certificateProfileHelper.addCertificateProfile(EcaQa201_PositiveBlacklistValidator.TestData.CERTIFICATE_PROFILE_NAME);
        // Verify Audit Log
        auditLogHelper.openPage(getAdminWebUrl());
        auditLogHelper.assertLogEntryByEventText(
                "Certificate Profile Create",
                "Success",
                null,
                Collections.singletonList("New certificate profile " + EcaQa201_PositiveBlacklistValidator.TestData.CERTIFICATE_PROFILE_NAME + " added successfully.")
        );
    }

    @Test
    public void stepB_edit_CertificateProfile() {
        // Update default timestamp
        auditLogHelper.initFilterTime();
        // Edit certificate Profile
        certificateProfileHelper.openPage(getAdminWebUrl());
        certificateProfileHelper.openEditCertificateProfilePage(EcaQa201_PositiveBlacklistValidator.TestData.CERTIFICATE_PROFILE_NAME);

        // Set Approval Settings
        certificateProfileHelper.selectApprovalSetting(CertificateProfileHelper.ApprovalSetting.ADD_OR_EDIT_END_ENTITY, EcaQa201_PositiveBlacklistValidator.TestData.APPROVAL_PROFILE_NAME);
        certificateProfileHelper.selectApprovalSetting(CertificateProfileHelper.ApprovalSetting.KEY_RECOVERY, EcaQa201_PositiveBlacklistValidator.TestData.APPROVAL_PROFILE_NAME);
        certificateProfileHelper.selectApprovalSetting(CertificateProfileHelper.ApprovalSetting.REVOCATION, EcaQa201_PositiveBlacklistValidator.TestData.APPROVAL_PROFILE_NAME);

        // Set validity
        certificateProfileHelper.editCertificateProfile("720d");
        // Save
        certificateProfileHelper.saveCertificateProfile();
        // Verify Audit Log
        auditLogHelper.openPage(getAdminWebUrl());
        auditLogHelper.assertLogEntryByEventText(
                "Certificate Profile Edit",
                "Success",
                null,
                Arrays.asList(
                        "msg=Edited certificateprofile " + EcaQa201_PositiveBlacklistValidator.TestData.CERTIFICATE_PROFILE_NAME + ".",
                        "changed:encodedvalidity=1y 11mo 25d"
                )
        );
    }

        @Test
        public void testB_addEndEntityProfile() {
            eeProfileHelper.openPage(this.getAdminWebUrl());
            eeProfileHelper.addEndEntityProfile("EcaQa201_P_EntityProfile");
            eeProfileHelper.openEditEndEntityProfilePage("EcaQa201_P_EntityProfile");
            eeProfileHelper.selectDefaultCa(this.getCaName());
            eeProfileHelper.triggerMaximumNumberOfFailedLoginAttempts();
            eeProfileHelper.triggerCertificateValidityStartTime();
            eeProfileHelper.triggerCertificateValidityEndTime();
            eeProfileHelper.setCertificateValidityStartTime(EcaQa201_PositiveBlacklistValidator.TestData.currentDateString);
            eeProfileHelper.setCertificateValidityEndTime(EcaQa201_PositiveBlacklistValidator.TestData.oneMonthsFromNowString);
            eeProfileHelper.triggerNameConstraints();
            eeProfileHelper.triggerExtensionData();
            eeProfileHelper.triggerNumberOfAllowedRequests();
            eeProfileHelper.triggerKeyRecoverable();
            eeProfileHelper.triggerIssuanceRevocationReason();
            eeProfileHelper.triggerSendNotification();
            eeProfileHelper.addNotification();
            eeProfileHelper.setNotificationSender(0, "sender@example.com");
            eeProfileHelper.setNotificationSubject(0, "Web Tester");
            eeProfileHelper.setNotificationMessage(0, "test message");
            eeProfileHelper.saveEndEntityProfile(true);
            eeProfileHelper.assertEndEntityProfileNameExists("EcaQa201_P_EntityProfile");
        }

    @Test
    public void stepD_MakeNewCertificate() {
        raWebHelper.openPage(this.getRaWebUrl());
        raWebHelper.makeNewCertificateRequest();
        raWebHelper.selectCertificateTypeByEndEntityName("EcaQa201_P_EntityProfile");
        raWebHelper.selectKeyPairGenerationProvided();
        raWebHelper.fillClearCsrText(StringUtils.join(EcaQa201_PositiveBlacklistValidator.TestData.CERTIFICATE_REQUEST_PEM, "\n"));
        raWebHelper.clickUploadCsrButton();
        raWebHelper.assertCsrUploadError();
    }
    */




}
