<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page pageEncoding="ISO-8859-1"%>
<%@ page contentType="text/html; charset=@page.encoding@" %>

<%
   org.ejbca.ui.web.RequestHelper.setDefaultCharacterEncoding(request);
%>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <title>@EJBCA@ Certificate/CRL Retrieval</title>
    <link rel="stylesheet" href="../styles.css" type="text/css" />
    <script type="text/javascript" src="scripts/functions.js"></script>
    <script type="text/vbscript" src="scripts/functions.vbs"></script>
  </head>
  <body>
    <div class="logobar">
      <a href="../index.jsp"><img src="../images/logotype.png" alt="EJBCA-pki logotype" border="0"/></a>
    </div>
    <div class="menucontainer">
      <div class="menu">
        <ul>
          <li><div class="menuheader">Enrol</div>
            <ul>
              <li>
                <a href="../enrol/browser.jsp">Create Browser Certificate</a>
              </li>
              <li>
                <a href="../enrol/server.jsp">Create Server Certificate</a>
              </li>
              <li>
                <a href="../enrol/keystore.jsp">Create Keystore</a>
              </li>
            </ul>
          </li>  
          <li><div class="menuheader">Retrieve</div>
            <ul>
              <li>
                <a href="ca_certs.jsp">Fetch CA &amp; OCSP Certificates</a>
              </li>
              <li>
                <a href="ca_crls.jsp">Fetch CA CRLs</a>
              </li>
              <li>
                <a href="latest_cert.jsp">Fetch User's Latest Certificate</a>
              </li>
            </ul>
          </li>  
          <li><div class="menuheader">Miscellaneous</div>
            <ul>
              <li>
                <a href="list_certs.jsp">List  User's Certificates</a>
              </li>
              <li>
                <a href="check_status.jsp">Check Certificate Status</a>
              </li>
              <li>
              <a href="../adminweb/index.jsp">Administration</a>
            </li>
            </ul>
          </li>  
        </ul>
      </div>
    </div>
    <div class="main">
      <div class="content">
