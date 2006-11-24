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
 
package org.ejbca.ui.web.admin.loginterface;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.ejbca.core.ejb.ServiceLocator;
import org.ejbca.core.ejb.ca.store.ICertificateStoreSessionLocal;
import org.ejbca.core.ejb.ca.store.ICertificateStoreSessionLocalHome;
import org.ejbca.core.ejb.log.ILogSessionLocal;
import org.ejbca.core.ejb.log.ILogSessionLocalHome;
import org.ejbca.core.model.log.Admin;
import org.ejbca.core.model.log.LogConfiguration;
import org.ejbca.core.model.log.LogConstants;
import org.ejbca.core.model.log.LogEntry;
import org.ejbca.ui.web.admin.configuration.EjbcaWebBean;
import org.ejbca.ui.web.admin.configuration.InformationMemory;
import org.ejbca.util.StringTools;
import org.ejbca.util.query.BasicMatch;
import org.ejbca.util.query.LogMatch;
import org.ejbca.util.query.Query;

/**
 * A java bean handling the interface between EJBCA log module and JSP pages.
 *
 * @author  Philip Vendil
 * @version $Id: LogInterfaceBean.java,v 1.2 2006-11-24 11:44:52 anatom Exp $
 */
public class LogInterfaceBean implements java.io.Serializable {

    // Public constants.
    public static final int MAXIMUM_QUERY_ROWCOUNT = LogConstants.MAXIMUM_QUERY_ROWCOUNT;

    /** Creates new LogInterfaceBean */
    public LogInterfaceBean(){  
    }
    // Public methods.
    /**
     * Method that initialized the bean.
     *
     * @param request is a reference to the http request.
     */  
    public void initialize(HttpServletRequest request, EjbcaWebBean ejbcawebbean) throws  Exception{

      if(!initialized){
        admin           = new Admin(((X509Certificate[]) request.getAttribute( "javax.servlet.request.X509Certificate" ))[0]);
        
        final ServiceLocator locator = ServiceLocator.getInstance();
        ILogSessionLocalHome logsessionhome = (ILogSessionLocalHome) locator.getLocalHome(ILogSessionLocalHome.COMP_NAME);
        logsession = logsessionhome.create(); 
        
        ICertificateStoreSessionLocalHome certificatesessionhome = (ICertificateStoreSessionLocalHome) locator.getLocalHome(ICertificateStoreSessionLocalHome.COMP_NAME);
        certificatesession = certificatesessionhome.create();
        
        this.informationmemory = ejbcawebbean.getInformationMemory();
        
        initializeEventNameTables(ejbcawebbean);
                
        dnproxy = new SubjectDNProxy(admin, certificatesession);           
        
		HashMap caidtonamemap = ejbcawebbean.getInformationMemory().getCAIdToNameMap();
        
        // Add Internal CA Name if it doesn't exists
        if(caidtonamemap.get(new Integer(LogConstants.INTERNALCAID)) == null){
			caidtonamemap.put(new Integer(LogConstants.INTERNALCAID),ejbcawebbean.getText("INTERNALCA"));
        }
              
        logentriesview = new LogEntriesView(dnproxy, localinfoeventnamesunsorted, localerroreventnamesunsorted, localmodulenamesunsorted,  caidtonamemap);
        initialized =true; 
      }
    }

    /** 
     * Method that searches the log database for all events occurred related to the given query.
     *
     * @param query the query to use.
     * @param index point's where in result to begin returning data.
     * @param size the number of elements to return.
     */

    public LogEntryView[] filterByQuery(Query query, int index, int size) throws Exception {
      Collection logentries = logsession.query(query, informationmemory.getViewLogQueryString(), informationmemory.getViewLogCAIdString());
      logentriesview.setEntries(logentries);

      return logentriesview.getEntries(index,size);        
    }    

    /** 
     * Method that searches the log database for all events occurred related to the given username.
     * Used in the view user history page.
     *
     * @param username the username to search for
     * @param index point's where in result to begin returning data.
     * @param size the number of elements to return.
     */
    public LogEntriesView filterByUsername(String username, HashMap caidtonamemap) throws Exception {
      LogEntriesView returnval = new LogEntriesView(dnproxy, localinfoeventnamesunsorted, localerroreventnamesunsorted, localmodulenamesunsorted,  caidtonamemap);  
      String user = StringTools.strip(username);  
      Query query = new Query(Query.TYPE_LOGQUERY);
      query.add(LogMatch.MATCH_WITH_USERNAME, BasicMatch.MATCH_TYPE_EQUALS, user);
        
      Collection logentries = logsession.query(query,informationmemory.getViewLogQueryString(), informationmemory.getViewLogCAIdString());
      returnval.setEntries(logentries);

      return returnval;        
    }        
    
    /** 
     * Method that searches the log database for all events occurred within the last given minutes.
     * Used in the view user history page.
     *
     * @param time the time in minutes to look for.
     * @param index point's where in result to begin returning data.
     * @param size the number of elements to return.
     */
    public LogEntryView[] filterByTime(int time, int index, int size) throws Exception {
      Query query = new Query(Query.TYPE_LOGQUERY);
      Date starttime = new Date( (new Date()).getTime() - (time * 60000));
      
      query.add(starttime, new Date());
        
      Collection logentries = logsession.query(query,informationmemory.getViewLogQueryString(), informationmemory.getViewLogCAIdString());
      logentriesview.setEntries(logentries);

      return logentriesview.getEntries(index,size);        
    }            

    /* Method that returns the size of a query search */
    public int getResultSize(){
     return logentriesview.size();   
    }
    
    /* Method to resort filtered user data. */
    public void sortUserData(int sortby, int sortorder){
      logentriesview.sortBy(sortby,sortorder);
    }

    /* Method to return the logentries between index and size, if logentries is smaller than size, a smaller array is returned. */
    public LogEntryView[] getEntries(int index, int size){
      return logentriesview.getEntries(index, size);
    }

    public boolean nextButton(int index, int size){
      return index + size < logentriesview.size();
    }
    public boolean previousButton(int index){
      return index > 0 ;
    }
     
    /**
     * Loads the log configuration from the database.
     *
     * @return the logconfiguration
     */
    public LogConfiguration loadLogConfiguration(int caid) {
      return logsession.loadLogConfiguration(caid);   
    }    
        
    /**
     * Saves the log configuration to the database.
     *
     * @param logconfiguration the logconfiguration to save.
     */    
    public void saveLogConfiguration(int caid, LogConfiguration logconfiguration) {
      logsession.saveLogConfiguration(admin, caid, logconfiguration);   
    }    
    
   
 
    /**
     * Help methods that sets up id mappings between   event ids and  event names in local languange.
     *
     * @return a hasmap with error info eventname to id mappings.
     */  
    public HashMap getEventNameToIdMap(){             
      return localeventnamestoid;          
    }    
    
    /**
     * Help methods that sets up id mappings between  module ids and module names in local languange.
     *
     * @return a hasmap with error info eventname to id mappings.
     */  
    public HashMap getModuleNameToIdMap(){             
      return localmodulenamestoid;          
    }     

    /**
     * Help methods that translates info event names to the local languange.
     *
     * @return an array with local info eventnames.
     */
    public String[] getLocalInfoEventNames(){
      return localinfoeventnames;
    }        
    
    /**
     * Help methods that translates error event names to the local languange.
     *
     * @return an array with local info eventnames.
     */    
    public String[] getLocalErronEventNames(){
      return localerroreventnames;      
    }      
    
    /**
     * Help methods that returns an array with all translated event names.
     *
     * @return an array of all translated eventnames.
     */
    public String[] getAllLocalEventNames(){
      return alllocaleventnames;  
    }
    
    /**
     * Help methods that returns an array with all translated module names.
     *
     * @return an array of all translated eventnames.
     */
    public String[] getLocalModuleNames(EjbcaWebBean ejbcawebbean){
      Collection authorizedmodules = this.informationmemory.getAuthorizedModules();	
      String[] returnval = new String[authorizedmodules.size()];
      Iterator iter = authorizedmodules.iterator();
      int i = 0;
      while(iter.hasNext()){
          returnval[i] = ejbcawebbean.getText(LogEntry.MODULETEXTS[((Integer) iter.next()).intValue()]);
          i++;
      }
      
      return returnval;  
    }    
    
    // Private methods.
    private void initializeEventNameTables(EjbcaWebBean ejbcawebbean){
      int alleventsize = LogEntry.EVENTNAMES_INFO.length +  LogEntry.EVENTNAMES_ERROR.length; 
      alllocaleventnames = new String[alleventsize];
      localinfoeventnames = new String[LogEntry.EVENTNAMES_INFO.length];
      localinfoeventnamesunsorted = new String[LogEntry.EVENTNAMES_INFO.length];
      localeventnamestoid = new HashMap();
      for(int i = 0; i < localinfoeventnames.length; i++){
        localinfoeventnames[i] = ejbcawebbean.getText(LogEntry.EVENTNAMES_INFO[i]);
        localinfoeventnamesunsorted[i] = ejbcawebbean.getText(LogEntry.EVENTNAMES_INFO[i]);
        alllocaleventnames[i] = localinfoeventnames[i];
        localeventnamestoid.put(localinfoeventnames[i], new Integer(i));
      }
      Arrays.sort(localinfoeventnames);          
      
      localerroreventnamesunsorted = new String[LogEntry.EVENTNAMES_ERROR.length];      
      localerroreventnames = new String[LogEntry.EVENTNAMES_ERROR.length];
      for(int i = 0; i < localerroreventnames.length; i++){
        localerroreventnames[i] = ejbcawebbean.getText(LogEntry.EVENTNAMES_ERROR[i]);
        localerroreventnamesunsorted[i] = localerroreventnames[i];        
        alllocaleventnames[LogEntry.EVENTNAMES_INFO.length + i] = localerroreventnames[i];
        localeventnamestoid.put(localerroreventnames[i], new Integer(i + LogEntry.EVENT_ERROR_BOUNDRARY));
      }
      Arrays.sort(localerroreventnames);     
      Arrays.sort(alllocaleventnames);
      
      localmodulenames = new String[LogEntry.MODULETEXTS.length]; 
      localmodulenamesunsorted = new String[LogEntry.MODULETEXTS.length];       
      localmodulenamestoid = new HashMap(9);     
      for(int i = 0; i < localmodulenames.length; i++){
        localmodulenames[i] = ejbcawebbean.getText(LogEntry.MODULETEXTS[i]);   
        localmodulenamesunsorted[i] = localmodulenames[i];  
        localmodulenamestoid.put(localmodulenames[i], new Integer(i));
      }
      Arrays.sort(localmodulenames);
      
    }
    

    // Private fields.
    private ICertificateStoreSessionLocal  certificatesession;
    private ILogSessionLocal               logsession;
    private LogEntriesView                 logentriesview;
    private Admin                          admin;
    private SubjectDNProxy                 dnproxy;  
    private boolean                        initialized=false;
    private InformationMemory              informationmemory;
    
    private HashMap                        localeventnamestoid; 
    private HashMap                        localmodulenamestoid;
    private String[]                       localinfoeventnames;
    private String[]                       localerroreventnames;    
    private String[]                       localinfoeventnamesunsorted;
    private String[]                       localerroreventnamesunsorted;        
    private String[]                       alllocaleventnames;
    private String[]                       localmodulenames;
    private String[]                       localmodulenamesunsorted;    
    
}
