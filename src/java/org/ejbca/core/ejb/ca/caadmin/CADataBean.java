/*************************************************************************
 *                                                                       *
 *  EJBCA: The OpenSource Certificate Authority                          *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
 
package org.ejbca.core.ejb.ca.caadmin;

import java.io.UnsupportedEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.ejb.CreateException;

import org.apache.log4j.Logger;
import org.ejbca.core.ejb.BaseEntityBean;
import org.ejbca.core.model.UpgradeableDataHashMap;
import org.ejbca.core.model.ca.caadmin.CA;
import org.ejbca.core.model.ca.caadmin.CACacheManager;
import org.ejbca.core.model.ca.caadmin.CAInfo;
import org.ejbca.core.model.ca.caadmin.IllegalKeyStoreException;
import org.ejbca.core.model.ca.caadmin.X509CA;
import org.ejbca.util.Base64GetHashMap;
import org.ejbca.util.Base64PutHashMap;



/** Entity bean should not be used directly, use though Session beans.
 *
 * Entity Bean representing a ca instance.
 * Information stored:
 * <pre>
 *  caid (Primary key)
 *  name
 *  SubjectDN
 *  type
 *  status
 *  expiretime
 *  data (non searchable data, HashMap stored as XML-String)
 * </pre>
 *
 * @version $Id: CADataBean.java,v 1.13 2006-12-27 11:13:53 anatom Exp $
 *
 * @ejb.bean
 *   description="This enterprise bean entity represents a publisher"
 *   display-name="CADataEB"
 *   name="CAData"
 *   jndi-name="CAData"
 *   local-jndi-name="CADataLocal"
 *   view-type="local"
 *   type="CMP"
 *   reentrant="True"
 *   cmp-version="2.x"
 *   transaction-type="Container"
 *   schema="CADataBean"
 *   primkey-field="caId"
 *
 * @ejb.pk generate="false"
 *   class="java.lang.Integer"
 *
 * @ejb.persistence table-name = "CAData"
 * 
 * @ejb.env-entry description="Used internally to keystores in database"
 *   name="keyStorePass"
 *   type="java.lang.String"
 *   value="${ca.keystorepass}"
 *   
 * @ejb.env-entry description="Password for OCSP keystores"
 *   name="OCSPKeyStorePass"
 *   type="java.lang.String"
 *   value="${ca.ocspkeystorepass}"
 *
 * @ejb.env-entry description="Password for XKMS keystores"
 *   name="XKMSKeyStorePass"
 *   type="java.lang.String"
 *   value="${ca.xkmskeystorepass}"
 *   
 * @ejb.env-entry description="Password for CMS keystores"
 *   name="CMSKeyStorePass"
 *   type="java.lang.String"
 *   value="${ca.cmskeystorepass}"
 *   
 * @ejb.home
 *   generate="local"
 *   local-extends="javax.ejb.EJBLocalHome"
 *   local-class="org.ejbca.core.ejb.ca.caadmin.CADataLocalHome"
 *
 * @ejb.interface
 *   generate="local"
 *   local-extends="javax.ejb.EJBLocalObject"
 *   local-class="org.ejbca.core.ejb.ca.caadmin.CADataLocal"
 *
 * @ejb.finder
 *   description="findByName"
 *   signature="org.ejbca.core.ejb.ca.caadmin.CADataLocal findByName(java.lang.String name)"
 *   query="SELECT OBJECT(a) from CADataBean a WHERE a.name=?1"
 *
 * @ejb.finder
 *   description="findAll"
 *   signature="Collection findAll()"
 *   query="SELECT OBJECT(a) from CADataBean a"
 *
 * @ejb.transaction type="Required"
 *
 * @jonas.jdbc-mapping
 *   jndi-name="${datasource.jndi-name}"
 */
public abstract class CADataBean extends BaseEntityBean {

    private static final Logger log = Logger.getLogger(CADataBean.class);

    /**
     * @ejb.pk-field
     * @ejb.persistence column-name="cAId"
     * @ejb.interface-method
    */
    public abstract Integer getCaId();

    /**
    */
    public abstract void setCaId(Integer caid);

    /**
     * @ejb.persistence column-name="name"
     * @ejb.interface-method
     */
    public abstract String getName();

    /**
     * @ejb.interface-method
     */
    public abstract void setName(String name);

    /**
     * @ejb.persistence column-name="subjectDN"
     * @ejb.interface-method
     */
    public abstract String getSubjectDN();

    /**
     */
    public abstract void setSubjectDN(String subjectdn);
    
    /**
     * @ejb.persistence column-name="status"
     * @ejb.interface-method
     */
    public abstract int getStatus();

    /**
     * @ejb.interface-method
     */
    public abstract void setStatus(int status);
    
    /**
     * @ejb.persistence column-name="expireTime"
     * @ejb.interface-method
     */
    public abstract long getExpireTime();

    /**
     * @ejb.interface-method
     */
    public abstract void setExpireTime(long expiretime);
    
    /**
     * @ejb.persistence jdbc-type="LONGVARCHAR" column-name="data"
     */
    public abstract String getData();

    /**
     */
    public abstract void setData(String data);
    
    
    /** 
     * Method that retrieves the CA from the database.
     * @throws IllegalKeyStoreException 
     * @ejb.interface-method
     */
    public CA getCA() throws java.io.UnsupportedEncodingException, IllegalKeyStoreException {
        CA ca = null;
        // First check if we already have a cached instance of the CA
        ca = CACacheManager.instance().getCA(getCaId().intValue(), this);
        if (ca == null) {
            java.beans.XMLDecoder decoder = new  java.beans.XMLDecoder(new java.io.ByteArrayInputStream(getData().getBytes("UTF8")));
            HashMap h = (HashMap) decoder.readObject();
            decoder.close();
            // Handle Base64 encoded string values
            HashMap data = new Base64GetHashMap(h);
            
            // If CA-data is upgraded we want to save the new data, so we must get the old version before loading the data 
            // and perhaps upgrading
            float oldversion = ((Float) data.get(UpgradeableDataHashMap.VERSION)).floatValue();
            switch(((Integer)(data.get(CA.CATYPE))).intValue()){
                case CAInfo.CATYPE_X509:
                    ca = new X509CA(data, getCaId().intValue(), getSubjectDN(), getName(), getStatus());                    
                    break;
            }
            boolean upgradedExtendedService = ca.upgradeExtendedCAServices();
            // Compare old version with current version and save the data if there has been a change
            if ( ((ca != null) && (Float.compare(oldversion, ca.getVersion()) != 0))
            	  || upgradedExtendedService) {
            	// Make sure we upgrade the CAToken as well, if needed
                ca.getCAToken();
                setCA(ca);
            }
            // We have to do the same if CAToken was upgraded
            // Add CA to the cache
            CACacheManager.instance().addCA(getCaId().intValue(), ca);
        }
        return ca;              
    }
    
    /** 
     * Method that saves the CA to database.
     * @ejb.interface-method
     */
    public void setCA(CA ca) throws UnsupportedEncodingException {
        // We must base64 encode string for UTF safety
        HashMap a = new Base64PutHashMap();
        a.putAll((HashMap)ca.saveData());
        
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(baos);
        encoder.writeObject(a);
        encoder.close();
        setData(baos.toString("UTF8"));
        // remove the CA from the cache to force an update the next time we load it
        CACacheManager.instance().removeCA(getCaId().intValue());
    }   
    
    //
    // Fields required by Container
    //


    /**
     * Entity Bean holding data of a CA.
     * @param subjectdn
     * @param name of CA
     * @param status initial status
     * @param ca CA to store
     * @return caid
     * @ejb.create-method
     */
    public Integer ejbCreate(String subjectdn, String name, int status, CA ca) throws CreateException {
    	try {
    		
    		setCaId(new Integer(subjectdn.hashCode()));
    		setName(name);        
    		setSubjectDN(subjectdn);
    		setStatus(status);        
    		
    		
    		if(ca instanceof X509CA && ca.getCertificateChain().size() != 0){
    			setExpireTime(((X509Certificate) ca.getCACertificate()).getNotAfter().getTime());  
    			ca.setExpireTime(((X509Certificate) ca.getCACertificate()).getNotAfter()); 
    		}  
    		
    		setCA(ca);        
    		
    		log.debug("Created CA "+ name);
    		return new Integer(subjectdn.hashCode());
    	} catch(java.io.UnsupportedEncodingException e) {
    		log.error("CAData caught exception trying to create: ", e);
    		throw new CreateException(e.toString());
    	}
    }

    public void ejbPostCreate(String subjectdn, String name, int status, CA ca) {
        // Do nothing. Required.
    }
    
    
}
