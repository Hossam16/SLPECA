package org.ejbca.ui.web.rest.api.io.request;

import io.swagger.annotations.ApiModelProperty;
import org.cesecore.certificates.certificate.ssh.SshKeyException;
import org.cesecore.certificates.certificate.ssh.SshKeyFactory;
import org.cesecore.certificates.certificate.ssh.SshPublicKey;
import org.ejbca.core.protocol.ssh.SshRequestMessage;
import org.ejbca.ui.web.rest.api.validator.ValidSshCertificateRestRequest;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
@ValidSshCertificateRestRequest
public class SshCertificateRequestRestRequest {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "End Entity profile name", example = "ExampleEEP")
    private String endEntityProfile;
    @ApiModelProperty(value = "Certificate profile name", example = "ENDUSER")
    private String certificateProfile;
    @ApiModelProperty(value = "Certificate Authority (CA) name", example = "CN=ExampleCA")
    private String certificateAuthority;
    @ApiModelProperty(value = "SSH Key Identifier", example = "ski-02")
    private String keyId;
    @ApiModelProperty(value = "Comment", example = "Yellow fish")
    private String comment;
    @ApiModelProperty(value = "Public Key", example = "ssh-rsa AAA...EWj")
    private String publicKey;
    @ApiModelProperty(value = "Valid principals", example = "Wishman Bradman")
    private List<String> principals;
    @ApiModelProperty(value = "Critical options", example = "\"force-command\": \".\\init.sh\", \"source-address\": \"1.2.3.0/24,1.10.10.1/32\"")
    private Map<String, String> criticalOptions;
    @ApiModelProperty(value = "Extensions", example = "\"permit-x11-forwarding\": \"\"")
    private Map<String, byte[]> additionalExtensions;
    @ApiModelProperty(value = "Username", example = "JohnDoe")
    private String username;
    @ApiModelProperty(value = "Password", example = "foo123")
    private String password;
    @ApiModelProperty(value = "Serial", example = "1")
    private String serialNumber;

    public SshCertificateRequestRestRequest() {
    }

    public SshCertificateRequestRestRequest(String endEntityProfile, String certificateProfile, String certificateAuthority, String keyId, String comment, String publicKey, List<String> principals, Map<String, String> criticalOptions, Map<String, byte[]> additionalExtensions, String username, String serialNumber, String password) {
        this.endEntityProfile = endEntityProfile;
        this.certificateProfile = certificateProfile;
        this.certificateAuthority = certificateAuthority;
        this.keyId = keyId;
        this.comment = comment;
        this.publicKey = publicKey;
        this.principals = principals;
        this.criticalOptions = criticalOptions;
        this.additionalExtensions = additionalExtensions;
        this.username = username;
        this.serialNumber = serialNumber;
        this.password = password;
    }

    public String getEndEntityProfile() {
        return endEntityProfile;
    }

    public void setEndEntityProfile(String endEntityProfile) {
        this.endEntityProfile = endEntityProfile;
    }

    public String getCertificateProfile() {
        return certificateProfile;
    }

    public void setCertificateProfile(String certificateProfile) {
        this.certificateProfile = certificateProfile;
    }

    public String getCertificateAuthority() {
        return certificateAuthority;
    }

    public void setCertificateAuthority(String certificateAuthority) {
        this.certificateAuthority = certificateAuthority;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public List<String> getPrincipals() {
        return principals;
    }

    public void setPrincipals(List<String> principals) {
        this.principals = principals;
    }

    public Map<String, String> getCriticalOptions() {
        return criticalOptions;
    }

    public void setCriticalOptions(Map<String, String> criticalOptions) {
        this.criticalOptions = criticalOptions;
    }

    public Map<String, byte[]> getAdditionalExtensions() {
        return additionalExtensions;
    }

    public void setAdditionalExtensions(Map<String, byte[]> additionalExtensions) {
        this.additionalExtensions = additionalExtensions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a converter instance for this class.
     *
     * @return instance of converter for this class.
     */
    public static SshCertificateRequestRestRequestConverter converter() {
        return new SshCertificateRequestRestRequestConverter();
    }

    /**
     * Converter instance for this class.
     */
    public static class SshCertificateRequestRestRequestConverter {

        /**
         * Converts a sshCertificateRequestRestRequest into SshRequestMessage.
         *
         * @param sshCertificateRequestRestRequest input.
         *
         * @return SshRequestMessage instance.
         */
        public SshRequestMessage toSshRequestMessage(final SshCertificateRequestRestRequest sshCertificateRequestRestRequest) throws IOException, SshKeyException, InvalidKeySpecException {
            SshPublicKey pubKey = SshKeyFactory.INSTANCE.extractSshPublicKeyFromFile(sshCertificateRequestRestRequest.getPublicKey().getBytes());
            final byte[] sshPublicKey = pubKey.encode();
            return new SshRequestMessage.Builder()
                .keyId(sshCertificateRequestRestRequest.getKeyId())
                .comment(sshCertificateRequestRestRequest.getComment())
                .publicKey(sshPublicKey)
                .principals(sshCertificateRequestRestRequest.getPrincipals())
                .criticalOptions(sshCertificateRequestRestRequest.getCriticalOptions())
                .additionalExtensions(sshCertificateRequestRestRequest.getAdditionalExtensions())
                .username(sshCertificateRequestRestRequest.getUsername())
                .serialNumber(sshCertificateRequestRestRequest.getSerialNumber())
                .password(sshCertificateRequestRestRequest.getPassword())
                .build();
        }
    }
}
