/*************************************************************************
 *                                                                       *
 *  EJBCA - Proprietary Modules: Enterprise Certificate Authority        *
 *                                                                       *
 *  Copyright (c), PrimeKey Solutions AB. All rights reserved.           *
 *  The use of the Proprietary Modules are subject to specific           * 
 *  commercial license terms.                                            *
 *                                                                       *
 *************************************************************************/

package se.primekey.ejbca.autoenroll;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.SignatureException;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSignerInfoVerifierBuilder;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class RequestDecoder {

    static final String CMCRequest = "1.3.6.1.5.5.7.12.2";
    private byte[] pkcs10Blob;

    public RequestDecoder(byte[] cmsData) throws SignatureException, CMSException, IOException, GeneralSecurityException {
        //		cmsData[cmsData.length - 50] += 1;  // Kills CMS signature
        //		cmsData[cmsData.length - 554] += 1; // Kills PKCS #10 signature
        CMSSignedData csd = new CMSSignedData(cmsData);
        if (!csd.getSignedContentTypeOID().equals(CMCRequest)) {
            throw new IOException("CMC request missing!");
        }
        SignerInformation signer = (SignerInformation) csd.getSignerInfos().getSigners().iterator().next();
        CMSProcessableByteArray cpb = (CMSProcessableByteArray) csd.getSignedContent();
        byte[] signedContent = (byte[]) cpb.getContent();
        ASN1Sequence inner = (ASN1Sequence) ASN1Sequence.getInstance(signedContent).getObjectAt(1);
        Object obj = inner.getObjectAt(0);
        if (obj instanceof ASN1TaggedObject) {
            inner = (ASN1Sequence) ASN1Sequence.getInstance((ASN1TaggedObject) obj, false).getObjectAt(1);
        } else {
            throw new IOException("Undecodable tagged object");
        }
        pkcs10Blob = inner.getEncoded();
        PKCS10CertificationRequest pkcs10 = new PKCS10CertificationRequest(inner);

        try {
            if (!pkcs10.verify()) {
                //            throw new IOException("Bad PoP signature");
                System.err.println("Bad Pop signature: PKCS10CertificationRequest verify failure.");
            }
        } catch (Exception ex) {
            //            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }

        try {
            JcaDigestCalculatorProviderBuilder calculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder()
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME);
            JcaSignerInfoVerifierBuilder signerInfoVerifierBuilder = new JcaSignerInfoVerifierBuilder(calculatorProviderBuilder.build())
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME);
            SignerInformationVerifier signerInformationVerifier = signerInfoVerifierBuilder.build(pkcs10.getPublicKey());
            if (!signer.verify(signerInformationVerifier)) {
                //            throw new IOException("CMS signature error");
                System.err.println("CMS signature error: SignerInformation verify failure.");
            }
        } catch (Exception ex) {
            //            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
        System.out.println("SIZE=" + pkcs10.getCertificationRequestInfo().getAttributes().size());
    }

    public byte[] getPKCS10Blob() {
        return pkcs10Blob;
    }

    private static byte[] getByteArrayFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
        byte[] buffer = new byte[10000];
        int bytes;
        while ((bytes = is.read(buffer)) != -1) {
            baos.write(buffer, 0, bytes);
        }
        is.close();
        return baos.toByteArray();
    }

    public static void main(String[] argc) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            RequestDecoder req = new RequestDecoder(getByteArrayFromInputStream(new FileInputStream(argc[0])));
            System.out.println("PKCS #10 blob is " + req.getPKCS10Blob().length + " bytes");
            System.out.println("PKCS #10 blob [" + req.getPKCS10Blob() + "]");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(-3);
        }

    }
}
