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

package org.ejbca.core.protocol.ssh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extensions;
import org.cesecore.certificates.certificate.request.RequestMessage;
import org.cesecore.certificates.certificate.ssh.SshEndEntityProfileFields;
import org.cesecore.certificates.certificate.ssh.SshKeyException;
import org.cesecore.certificates.certificate.ssh.SshKeyFactory;
import org.cesecore.certificates.certificate.ssh.SshPublicKey;
import org.cesecore.certificates.certificateprofile.CertificateProfile;
import org.cesecore.certificates.endentity.EndEntityInformation;
import org.cesecore.certificates.endentity.ExtendedInformation;
import org.cesecore.keys.util.KeyTools;

/**
 * Request message for SSH certificates
 */
public class SshRequestMessage implements RequestMessage {

    private static final long serialVersionUID = 1L;
    
    private final String keyId;
    private final String comment;
    private byte[] publicKey;
    private final List<String> principals;
    private final Map<String, String> criticalOptions;
    private final Map<String, byte[]> additionalExtensions;
    private String username;
    private transient String serialNumber;
    private String password;


    public SshRequestMessage(final PublicKey publicKey, final String keyId, List<String> principals, final Map<String, byte[]> additionalExtensions,
            final Map<String, String> criticalOptions, final String comment) {
        this.keyId = keyId;
        this.comment = comment;
        this.publicKey = publicKey.getEncoded();
        this.principals = principals;
        this.criticalOptions = criticalOptions;
        this.additionalExtensions = additionalExtensions;
    }
    
    public SshRequestMessage(final byte[] publicKey, final String keyId, List<String> principals, final Map<String, byte[]> additionalExtensions,
            final Map<String, String> criticalOptions, final String comment) {
        this.keyId = keyId;
        this.comment = comment;
        this.publicKey = publicKey;
        this.principals = (principals != null ? principals : new ArrayList<>());
        this.criticalOptions = (criticalOptions != null ? criticalOptions : new HashMap<>());
        this.additionalExtensions = (additionalExtensions != null ? additionalExtensions : new HashMap<>());
    }
    
    protected SshRequestMessage(String subjectDn, String subjectAlternateName, ExtendedInformation ei) {
        if(ei==null) {
            throw new IllegalStateException("SSH request message is absent as extended information is null.");
        }
        if(StringUtils.isNotBlank(subjectDn)) {
            this.keyId = subjectDn.substring("CN=".length());
        } else {
            this.keyId = "";
        }
        List<String> principals = new ArrayList<>();
        
        if(StringUtils.isNotBlank(subjectAlternateName)) {
            subjectAlternateName = subjectAlternateName.substring("dnsName=".length());
            int commentIndex = subjectAlternateName.indexOf(SshEndEntityProfileFields.SSH_CERTIFICATE_COMMENT);
            if(commentIndex!=0) { // no principal
                if(commentIndex==-1) {
                    commentIndex = subjectAlternateName.length(); // principal is whole content
                    this.comment = "";
                } else {
                    this.comment = subjectAlternateName.substring(commentIndex + 
                            SshEndEntityProfileFields.SSH_CERTIFICATE_COMMENT.length() + 1);
                    commentIndex--;
                }
                String allPrincipals = subjectAlternateName.substring(SshEndEntityProfileFields.SSH_PRINCIPAL.length()+1, commentIndex);
                principals = Arrays.asList(allPrincipals.split(":"));
            } else {
                this.comment = subjectAlternateName.substring(SshEndEntityProfileFields.SSH_CERTIFICATE_COMMENT.length() + 1);
            }
            
        } else {
            this.comment = "";
        }
        
        this.principals = principals;
        this.criticalOptions = ei.getSshCriticalOptions();
        this.additionalExtensions = ei.getSshExtensions();
    }
    
    public SshRequestMessage(byte[] sshPublicKey, String subjectDn, String subjectAlternateName, ExtendedInformation ei) {
        
        this(subjectDn, subjectAlternateName, ei);
        
        try {
            SshPublicKey pubKey = SshKeyFactory.INSTANCE.extractSshPublicKeyFromFile(sshPublicKey);
            sshPublicKey = pubKey.encode();
        } catch (InvalidKeySpecException | SshKeyException | IOException e) {
            throw new IllegalStateException("SSH public key parsing failed.", e);
        } 
        
        this.publicKey = sshPublicKey;
                
    }

    public byte[] getEncoded() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        byte[] encodedObject;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            encodedObject = bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // NOPMD: ignore close exception
            }
        }
        return encodedObject;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getIssuerDN() {
        return null;
    }

    @Override
    public BigInteger getSerialNo() {
        return null;
    }

    @Override
    public String getRequestDN() {
        return null;
    }

    @Override
    public X500Name getRequestX500Name() {
        return null;
    }

    @Override
    public String getRequestAltNames() {
        return null;
    }

    @Override
    public Date getRequestValidityNotBefore() {
        return null;
    }

    @Override
    public Date getRequestValidityNotAfter() {
        return null;
    }

    @Override
    public Extensions getRequestExtensions() {
        return null;
    }

    @Override
    public String getCRLIssuerDN() {
        return null;
    }

    @Override
    public BigInteger getCRLSerialNo() {
        return null;
    }

    @Override
    public PublicKey getRequestPublicKey() throws InvalidKeyException {
        //Key can either come in as a straight java public key or an SSH public key, we'll accept both. First try a standard public key.
        PublicKey result = KeyTools.getPublicKeyFromBytes(publicKey);
        if(result != null) {
            return result;
        } else {
            try {
                SshPublicKey sshPublicKey = SshKeyFactory.INSTANCE.extractSshPublicKeyFromFile(publicKey);
                return sshPublicKey.getPublicKey();
            } catch (Exception e) {
                try {
                    SshPublicKey sshPublicKey = SshKeyFactory.INSTANCE.getSshPublicKey(publicKey);
                    return sshPublicKey.getPublicKey();
                } catch (Exception e2) {
                   throw new InvalidKeyException(e2);
                }
            }
        }
    }

    @Override
    public boolean verify() {
        return true;
    }

    @Override
    public boolean requireKeyInfo() {
        return false;
    }

    @Override
    public void setKeyInfo(Certificate cert, PrivateKey key, String provider) {

    }

    @Override
    public int getErrorNo() {
        return 0;
    }

    @Override
    public String getErrorText() {
        return null;
    }

    @Override
    public String getSenderNonce() {
        return null;
    }

    @Override
    public String getTransactionId() {
        return null;
    }

    @Override
    public byte[] getRequestKeyInfo() {
        return null;
    }

    @Override
    public String getPreferredDigestAlg() {
        return null;
    }

    @Override
    public boolean includeCACert() {
        return false;
    }

    @Override
    public int getRequestType() {
        return 0;
    }

    @Override
    public int getRequestId() {
        return 0;
    }

    @Override
    public void setResponseKeyInfo(PrivateKey key, String provider) {

    }

    @Override
    public List<Certificate> getAdditionalCaCertificates() {
        return null;
    }

    @Override
    public void setAdditionalCaCertificates(List<Certificate> additionalCaCertificates) {

    }

    @Override
    public List<Certificate> getAdditionalExtraCertsCertificates() {
        return null;
    }

    @Override
    public void setAdditionalExtraCertsCertificates(List<Certificate> additionalExtraCertificates) {

    }
    
    
    public String getKeyId() {
        return keyId;
    }

    public String getComment() {
        return comment;
    }

    public Map<String, byte[]> getAdditionalExtensions() {
        return additionalExtensions;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public List<String> getPrincipals() {
        return principals;
    }

    public Map<String, String> getCriticalOptions() {
        return criticalOptions;
    }

    @Override
    public String getCASequence() {
        return null;
    }

    @Override
    public void setRequestValidityNotAfter(Date notAfter) {
    }
    
    public void populateEndEntityData(EndEntityInformation userdata, CertificateProfile cerificateProfile) {
                
        if(userdata.getExtendedInformation()==null) {
            userdata.setExtendedInformation(new ExtendedInformation());
        }
        // these serve as an indicator to SSH end entity
        userdata.setSshEndEntity(true);
        userdata.getExtendedInformation().setSshCustomData(
                SshEndEntityProfileFields.SSH_CERTIFICATE_TYPE, cerificateProfile.getSshCertificateType().getLabel());
        
        if(StringUtils.isNotBlank(this.keyId)) {
            userdata.setDN("CN=" + this.keyId);
        } else {
            userdata.setDN("CN="); // will set to blank DN
        }
        StringBuilder placeHolderSan = new StringBuilder();
        if(getPrincipals()!=null && !getPrincipals().isEmpty()) {
            placeHolderSan.append(SshEndEntityProfileFields.SSH_PRINCIPAL + ":");
            for(String principal: getPrincipals()) {
                if(StringUtils.isNotBlank(principal)) {
                    placeHolderSan.append(principal);
                    placeHolderSan.append(":");
                }
            }
        }
        
        if(StringUtils.isNotBlank(getComment())) {
            placeHolderSan.append(SshEndEntityProfileFields.SSH_CERTIFICATE_COMMENT + ":");
            placeHolderSan.append(getComment());
        }
        
        String placeHolderSanString = placeHolderSan.toString();
        if(StringUtils.isNotBlank(placeHolderSanString)) {
            userdata.setSubjectAltName("dnsName=" + placeHolderSanString);
        }
        
        if(getCriticalOptions()!=null) {
            userdata.getExtendedInformation().setSshCriticalOptions(getCriticalOptions());
        }
        
        if(getAdditionalExtensions()!=null) {
            userdata.getExtendedInformation().setSshExtensions(getAdditionalExtensions());
        }
    }
    
}
