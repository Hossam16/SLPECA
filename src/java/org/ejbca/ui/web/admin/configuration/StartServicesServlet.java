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
 
package org.ejbca.ui.web.admin.configuration;

import java.io.IOException;

import javax.ejb.CreateException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.ejbca.core.ejb.ServiceLocator;
import org.ejbca.core.ejb.services.IServiceTimerSessionLocalHome;

/**
 * Servlet used to start services by calling the ServiceSession.load() at startup<br>
 *
 * 
 *
 * @version $Id: StartServicesServlet.java,v 1.4 2006-11-18 16:19:56 anatom Exp $
 * 
 * @web.servlet name = "StartServices"
 *              display-name = "StartServicesServlet"
 *              description="Servlet used to start services by calling the ServiceSession.load()"
 *              load-on-startup = "1"
 *
 * @web.servlet-mapping url-pattern = "/configuration/startservices"
 * 
 * @web.env-entry description="Determines if log4j should be initilized explicitly, needed for glassfish"
 *   name="LOG4JCONFIG"
 *   type="java.lang.String"
 *   value="${logging.log4j.config}"
 * 
 * @version $Id: StartServicesServlet.java,v 1.4 2006-11-18 16:19:56 anatom Exp $
 */
public class StartServicesServlet extends HttpServlet {

    /**
     * Method used to remove all active timers
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
        log.debug(">destroy calling ServiceSession.unload");
        try {
			getServiceHome().create().unload();
		} catch (CreateException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
	    }
		super.destroy();
	}

	private static Logger log = Logger.getLogger(StartServicesServlet.class);



    private IServiceTimerSessionLocalHome servicehome = null;

    private synchronized IServiceTimerSessionLocalHome getServiceHome() throws IOException {
        try{
            if(servicehome == null){
            	servicehome = (IServiceTimerSessionLocalHome)ServiceLocator.getInstance().getLocalHome(IServiceTimerSessionLocalHome.COMP_NAME);
            }
          } catch(Exception e){
             throw new java.io.IOException("Authorization Denied");
          }
          return servicehome;
    }
      

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.debug(">init calling ServiceSession.load");
        try {
			getServiceHome().create().load();
		} catch (CreateException e) {
			log.debug(e);
			throw new ServletException(e);
		} catch (IOException e) {
			log.debug(e);
			throw new ServletException(e);
	    }
        log.debug(">init initializing log4j");
        String configfile = ServiceLocator.getInstance().getString("java:comp/env/LOG4JCONFIG");
        if (!StringUtils.equals(configfile, "false")) {
            // Configure log4j
            // Set up a simple configuration that logs on the console.
            BasicConfigurator.configure();
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException {
        log.debug(">doPost()");
        doGet(req, res);
        log.debug("<doPost()");
    } //doPost

    public void doGet(HttpServletRequest req,  HttpServletResponse res) throws java.io.IOException, ServletException {
        log.debug(">doGet()");
        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Servlet doesn't support requests is only loaded on startup.");
        log.debug("<doGet()");
    } // doGet

}
