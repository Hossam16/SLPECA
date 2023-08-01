/*************************************************************************
 *                                                                       *
 *  CESeCore: CE Security Core                                           *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/
package org.cesecore.config;

import java.io.Serializable;

import org.cesecore.configuration.ConfigurationBase;
import org.cesecore.internal.InternalResources;

/**
 * Handles global CESeCore configuration values. 
 * 
 * @version $Id$
 */
public class GlobalCesecoreConfiguration extends ConfigurationBase implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private static final InternalResources intres = InternalResources.getInstance();
    
    /** A fixed maximum value to ensure that  */
    private static final int FIXED_MAXIMUM_QUERY_COUNT = 25_000;
    
    public static final String CESECORE_CONFIGURATION_ID = "CESECORE_CONFIGURATION";
    
    private static final String MAXIMUM_QUERY_COUNT_KEY = "maximum.query.count";
    private static final String MAXIMUM_QUERY_TIMEOUT_KEY = "maximum.query.timeout";
    
    private static final String REDACT_PII_DATA_DEFAULT = "redact.pii.default";
    private static final String REDACT_PII_DATA_ENFORCED = "redact.pii.enforced";
    
    @Override
    public void upgrade() {
    }

    @Override
    public String getConfigurationId() {
        return CESECORE_CONFIGURATION_ID;
    }
    
    /** 
     * Use a default value when End entity profile redaction settings is not accessible e.g. 
     * in peers(RA/VA), EMPTY end entity profile, exception messages with multiple sources etc 
     */
    public boolean getRedactPiiByDefault() {
        final Object res = data.get(REDACT_PII_DATA_DEFAULT);
        return res == null ? false : (boolean) res;
    }
    
    public void setRedactPiiByDefault(boolean redactPiiByDefault) {
        data.put(REDACT_PII_DATA_DEFAULT, redactPiiByDefault);
    }
    
    /** 
     * This flag may be enabled(true) to redact PII data irrespective of the setting at End entity profile
     * It will allow customer to redact data for a CA node without editing every single profile 
     */
    public boolean getRedactPiiEnforced() {
        final Object res = data.get(REDACT_PII_DATA_ENFORCED);
        return res == null ? false : (boolean) res;
    }
    
    public void setRedactPiiEnforced(boolean redactPiiEnforced) {
        data.put(REDACT_PII_DATA_ENFORCED, redactPiiEnforced);
    }
    
    /** @return the maximum size of the result from SQL select queries */
    public int getMaximumQueryCount() {
        final Object num = data.get(MAXIMUM_QUERY_COUNT_KEY);
        return num == null ? 500 : (int) num;
    }
    
    /**
     * Set's the maximum query count
     * 
     * @param maximumQueryCount the maximum query count
     * @throws InvalidConfigurationException if value was negative or above the limit set by {@link GlobalCesecoreConfiguration#MAXIMUM_QUERY_COUNT_KEY}
     */
    public void setMaximumQueryCount(int maximumQueryCount) throws InvalidConfigurationException { 
        if (maximumQueryCount > FIXED_MAXIMUM_QUERY_COUNT) {
            throw new InvalidConfigurationException(intres.getLocalizedMessage("globalconfig.error.querysizetoolarge", maximumQueryCount, FIXED_MAXIMUM_QUERY_COUNT));
        }
        if (maximumQueryCount < 1) {
            throw new InvalidConfigurationException(intres.getLocalizedMessage("globalconfig.error.querysizetoolow"));
        }
        data.put(MAXIMUM_QUERY_COUNT_KEY, maximumQueryCount);
    }

    /** @return database dependent query timeout hint in milliseconds or 0 if this is disabled. */
    public long getMaximumQueryTimeout() {
        final Object num = data.get(MAXIMUM_QUERY_TIMEOUT_KEY);
        return num == null ? 10000L : (long) num;
    }

    /** Set's the database dependent query timeout hint in milliseconds or 0 if this is disabled. */
    public void setMaximumQueryTimeout(final long maximumQueryTimeoutMs) { 
        data.put(MAXIMUM_QUERY_TIMEOUT_KEY, Math.max(maximumQueryTimeoutMs, 0L));
    }
}
