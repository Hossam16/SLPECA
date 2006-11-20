<%@ page pageEncoding="ISO-8859-1"%>
<%@ page contentType="text/html; charset=@page.encoding@" %>
<%@page errorPage="/errorpage.jsp" import="java.util.*, org.ejbca.ui.web.admin.configuration.EjbcaWebBean,org.ejbca.core.model.ra.raadmin.GlobalConfiguration, org.ejbca.core.model.SecConst, org.ejbca.core.model.authorization.AuthorizationDeniedException,
    org.ejbca.ui.web.RequestHelper,org.ejbca.ui.web.admin.cainterface.CAInterfaceBean, org.ejbca.core.model.ca.certificateprofiles.CertificateProfile, org.ejbca.ui.web.admin.cainterface.CertificateProfileDataHandler, 
               org.ejbca.core.model.ca.certificateprofiles.CertificateProfileExistsException, org.ejbca.ui.web.admin.rainterface.CertificateView, org.ejbca.core.model.ra.raadmin.DNFieldExtractor"%>

<html>
<jsp:useBean id="ejbcawebbean" scope="session" class="org.ejbca.ui.web.admin.configuration.EjbcaWebBean" />
<jsp:useBean id="cabean" scope="session" class="org.ejbca.ui.web.admin.cainterface.CAInterfaceBean" />

<%! // Declarations 
  static final String ACTION                              = "action";
  static final String ACTION_EDIT_CERTIFICATEPROFILES     = "editcertificateprofiles";
  static final String ACTION_EDIT_CERTIFICATEPROFILE      = "editcertificateprofile";

  static final String CHECKBOX_VALUE           = CertificateProfile.TRUE;

//  Used in profiles.jsp
  static final String BUTTON_EDIT_CERTIFICATEPROFILES      = "buttoneditcertificateprofile"; 
  static final String BUTTON_DELETE_CERTIFICATEPROFILES    = "buttondeletecertificateprofile";
  static final String BUTTON_ADD_CERTIFICATEPROFILES       = "buttonaddcertificateprofile"; 
  static final String BUTTON_RENAME_CERTIFICATEPROFILES    = "buttonrenamecertificateprofile";
  static final String BUTTON_CLONE_CERTIFICATEPROFILES     = "buttonclonecertificateprofile";

  static final String SELECT_CERTIFICATEPROFILES           = "selectcertificateprofile";
  static final String TEXTFIELD_CERTIFICATEPROFILESNAME    = "textfieldcertificateprofilename";
  static final String HIDDEN_CERTIFICATEPROFILENAME        = "hiddencertificateprofilename";
 
// Buttons used in profile.jsp
  static final String BUTTON_SAVE              = "buttonsave";
  static final String BUTTON_CANCEL            = "buttoncancel";
 
  static final String TEXTFIELD_VALIDITY               = "textfieldvalidity";
  static final String TEXTFIELD_CRLDISTURI             = "textfieldcrldisturi";
  static final String TEXTFIELD_CRLISSUER              = "textfieldcrlissuer";

  static final String TEXTFIELD_CERTIFICATEPOLICYID    = "textfieldcertificatepolicyid";
  static final String TEXTFIELD_POLICYNOTICE_CPSURL    = "textfielpolicynoticedcpsurl";
  static final String TEXTAREA_POLICYNOTICE_UNOTICE    = "textareapolicynoticeunotice";
	
  static final String TEXTFIELD_OCSPSERVICELOCATOR     = "textfieldocspservicelocatoruri";
  static final String TEXTFIELD_CNPOSTFIX              = "textfieldcnpostfix";
  static final String TEXTFIELD_PATHLENGTHCONSTRAINT   = "textfieldpathlengthconstraint";
  static final String TEXTFIELD_QCSSEMANTICSID         = "textfieldqcsemanticsid";
  static final String TEXTFIELD_QCSTATEMENTRANAME      = "textfieldqcstatementraname";
  static final String TEXTFIELD_QCETSIVALUELIMIT       = "textfieldqcetsivaluelimit";
  static final String TEXTFIELD_QCETSIVALUELIMITEXP    = "textfieldqcetsivaluelimitexp";
  static final String TEXTFIELD_QCETSIVALUELIMITCUR    = "textfieldqcetsivaluelimitcur";
  static final String TEXTFIELD_QCCUSTOMSTRINGOID      = "textfieldqccustomstringoid";
  static final String TEXTFIELD_QCCUSTOMSTRINGTEXT     = "textfieldqccustomstringtext";
  
  static final String CHECKBOX_BASICCONSTRAINTS                   = "checkboxbasicconstraints";
  static final String CHECKBOX_BASICCONSTRAINTSCRITICAL           = "checkboxbasicconstraintscritical";
  static final String CHECKBOX_KEYUSAGE                           = "checkboxkeyusage";
  static final String CHECKBOX_KEYUSAGECRITICAL                   = "checkboxkeyusagecritical";
  static final String CHECKBOX_SUBJECTKEYIDENTIFIER               = "checkboxsubjectkeyidentifier";
  static final String CHECKBOX_SUBJECTKEYIDENTIFIERCRITICAL       = "checkboxsubjectkeyidentifiercritical";
  static final String CHECKBOX_AUTHORITYKEYIDENTIFIER             = "checkboxauthoritykeyidentifier";
  static final String CHECKBOX_AUTHORITYKEYIDENTIFIERCRITICAL     = "checkboxauthoritykeyidentifiercritical";
  static final String CHECKBOX_SUBJECTALTERNATIVENAME             = "checkboxsubjectalternativename";
  static final String CHECKBOX_SUBJECTALTERNATIVENAMECRITICAL     = "checkboxsubjectalternativenamecritical";
  static final String CHECKBOX_SUBJECTDIRATTRIBUTES               = "checksubjectdirattributes";
  static final String CHECKBOX_CRLDISTRIBUTIONPOINT               = "checkboxcrldistributionpoint";
  static final String CHECKBOX_USEDEFAULTCRLDISTRIBUTIONPOINT     = "checkboxusedefaultcrldistributionpoint";
  static final String CHECKBOX_CRLDISTRIBUTIONPOINTCRITICAL       = "checkboxcrldistributionpointcritical";
  static final String CHECKBOX_USECERTIFICATEPOLICIES             = "checkusecertificatepolicies";
  static final String CHECKBOX_CERTIFICATEPOLICIESCRITICAL        = "checkcertificatepoliciescritical";
  static final String CHECKBOX_ALLOWVALIDITYOVERRIDE              = "checkallowvalidityoverride";
  static final String CHECKBOX_ALLOWKEYUSAGEOVERRIDE              = "checkallowkeyusageoverride";
  static final String CHECKBOX_USEEXTENDEDKEYUSAGE                = "checkuseextendedkeyusage";
  static final String CHECKBOX_EXTENDEDKEYUSAGECRITICAL           = "checkboxextendedkeyusagecritical";
  static final String CHECKBOX_USEOCSPSERVICELOCATOR              = "checkuseocspservicelocator";
  static final String CHECKBOX_USEDEFAULTOCSPSERVICELOCALTOR      = "checkusedefaultocspservicelocator";
  static final String CHECKBOX_USEMSTEMPLATE                      = "checkusemstemplate";
  static final String CHECKBOX_USECNPOSTFIX                       = "checkusecnpostfix";
  static final String CHECKBOX_USESUBJECTDNSUBSET                 = "checkusesubjectdnsubset";
  static final String CHECKBOX_USESUBJECTALTNAMESUBSET            = "checkusesubjectaltnamesubset";
  static final String CHECKBOX_USEPATHLENGTHCONSTRAINT            = "checkusepathlengthconstraint";
  static final String CHECKBOX_USEQCSTATEMENT                     = "checkuseqcstatement";
  static final String CHECKBOX_QCSTATEMENTCRITICAL                = "checkqcstatementcritical";
  static final String CHECKBOX_USEPKIXQCSYNTAXV2                  = "checkpkixqcsyntaxv2";
  static final String CHECKBOX_USEQCETSIQCCOMPLIANCE              = "checkqcetsiqcompliance";
  static final String CHECKBOX_USEQCETSIVALUELIMIT                = "checkqcetsivaluelimit";
  static final String CHECKBOX_USEQCETSISIGNATUREDEVICE           = "checkqcetsisignaturedevice";
  static final String CHECKBOX_USEQCCUSTOMSTRING                  = "checkqccustomstring";

  static final String SELECT_AVAILABLEBITLENGTHS                  = "selectavailablebitlengths";
  static final String SELECT_KEYUSAGE                             = "selectkeyusage";
  static final String SELECT_EXTENDEDKEYUSAGE                     = "selectextendedkeyusage";
  static final String SELECT_TYPE                                 = "selecttype";
  static final String SELECT_AVAILABLECAS                         = "selectavailablecas";
  static final String SELECT_AVAILABLEPUBLISHERS                  = "selectavailablepublishers";
  static final String SELECT_MSTEMPLATE                           = "selectmstemplate";
  static final String SELECT_SUBJECTDNSUBSET                      = "selectsubjectdnsubset";
  static final String SELECT_SUBJECTALTNAMESUBSET                 = "selectsubjectaltnamesubset";

  // Declare Language file.

%>
<% 

  // Initialize environment
  String certprofile = null;
  String includefile = "certificateprofilespage.jspf"; 
  boolean  triedtoeditfixedcertificateprofile   = false;
  boolean  triedtodeletefixedcertificateprofile = false;
  boolean  triedtoaddfixedcertificateprofile    = false;
  boolean  certificateprofileexists             = false;
  boolean  certificateprofiledeletefailed       = false;

  GlobalConfiguration globalconfiguration = ejbcawebbean.initialize(request, "/ca_functionality/edit_certificate_profiles"); 
                                            cabean.initialize(request, ejbcawebbean); 

  String THIS_FILENAME            =  globalconfiguration.getCaPath()  + "/editcertificateprofiles/editcertificateprofiles.jsp";
  
  boolean issuperadministrator = false;
  try{
    issuperadministrator = ejbcawebbean.isAuthorizedNoLog("/super_administrator");
  }catch(AuthorizationDeniedException ade){}   

  String[] keyusagetexts = CertificateView.KEYUSAGETEXTS;
  String[] extendedkeyusagetexts = CertificateView.EXTENDEDKEYUSAGETEXTS;
int[]    defaultavailablebitlengths = CertificateProfile.DEFAULTBITLENGTHS;
%>
 
<head>
  <title><%= globalconfiguration .getEjbcaTitle() %></title>
  <base href="<%= ejbcawebbean.getBaseUrl() %>">
  <link rel=STYLESHEET href="<%= ejbcawebbean.getCssFile() %>">
  <script language=javascript src="<%= globalconfiguration .getAdminWebPath() %>ejbcajslib.js"></script>
</head>
<body>

<%  // Determine action 
  RequestHelper.setDefaultCharacterEncoding(request);
  if( request.getParameter(ACTION) != null){
    if( request.getParameter(ACTION).equals(ACTION_EDIT_CERTIFICATEPROFILES)){
      if( request.getParameter(BUTTON_EDIT_CERTIFICATEPROFILES) != null){
          // Display  profilepage.jsp
         certprofile = request.getParameter(SELECT_CERTIFICATEPROFILES);
         if(certprofile != null){
           if(!certprofile.trim().equals("")){
             if(!certprofile.endsWith("(FIXED)")){ 
               includefile="certificateprofilepage.jspf"; 
             }else{
                triedtoeditfixedcertificateprofile=true;
                certprofile= null;
             }
           } 
           else{ 
            certprofile= null;
          } 
        }
        if(certprofile == null){   
          includefile="certificateprofilespage.jspf";     
        }
      }
      if( request.getParameter(BUTTON_DELETE_CERTIFICATEPROFILES) != null) {
          // Delete profile and display profilespage. 
          certprofile = request.getParameter(SELECT_CERTIFICATEPROFILES);
          if(certprofile != null){
            if(!certprofile.trim().equals("")){
              if(!certprofile.endsWith("(FIXED)")){ 
                certificateprofiledeletefailed = !cabean.removeCertificateProfile(certprofile);
              }else{
                triedtodeletefixedcertificateprofile=true;
              }
            }
          }
          includefile="certificateprofilespage.jspf";             
      }
      if( request.getParameter(BUTTON_RENAME_CERTIFICATEPROFILES) != null){ 
         // Rename selected profile and display profilespage.
       String newcertificateprofilename = request.getParameter(TEXTFIELD_CERTIFICATEPROFILESNAME);
       String oldcertificateprofilename = request.getParameter(SELECT_CERTIFICATEPROFILES);
       if(oldcertificateprofilename != null && newcertificateprofilename != null){
         if(!newcertificateprofilename.trim().equals("") && !oldcertificateprofilename.trim().equals("")){
           if(!oldcertificateprofilename.endsWith("(FIXED)")){ 
             try{
               cabean.renameCertificateProfile(oldcertificateprofilename.trim(),newcertificateprofilename.trim());
             }catch( CertificateProfileExistsException e){
               certificateprofileexists=true;
             }
           }else{
              triedtoeditfixedcertificateprofile=true;
           }        
         }
       }      
       includefile="certificateprofilespage.jspf"; 
      }
      if( request.getParameter(BUTTON_ADD_CERTIFICATEPROFILES) != null){
         // Add profile and display profilespage.
         certprofile = request.getParameter(TEXTFIELD_CERTIFICATEPROFILESNAME);
         if(certprofile != null){
           if(!certprofile.trim().equals("")){
             if(!certprofile.endsWith("(FIXED)")){
               try{
                 cabean.addCertificateProfile(certprofile.trim());
               }catch( CertificateProfileExistsException e){
                 certificateprofileexists=true;
               }
             }else{
               triedtoaddfixedcertificateprofile=true; 
             }
           }      
         }
         includefile="certificateprofilespage.jspf"; 
      }
      if( request.getParameter(BUTTON_CLONE_CERTIFICATEPROFILES) != null){
         // clone profile and display profilespage.
       String newcertificateprofilename = request.getParameter(TEXTFIELD_CERTIFICATEPROFILESNAME);
       String oldcertificateprofilename = request.getParameter(SELECT_CERTIFICATEPROFILES);
       if(oldcertificateprofilename != null && newcertificateprofilename != null){
         if(!newcertificateprofilename.trim().equals("") && !oldcertificateprofilename.trim().equals("")){
             if(oldcertificateprofilename.endsWith("(FIXED)"))
               oldcertificateprofilename = oldcertificateprofilename.substring(0,oldcertificateprofilename.length()-8);
             try{ 
               cabean.cloneCertificateProfile(oldcertificateprofilename.trim(),newcertificateprofilename.trim());
             }catch( CertificateProfileExistsException e){
               certificateprofileexists=true;
             }
         }
       }      
          includefile="certificateprofilespage.jspf"; 
      }
    }
    if( request.getParameter(ACTION).equals(ACTION_EDIT_CERTIFICATEPROFILE)){
         // Display edit access rules page.
       certprofile = request.getParameter(HIDDEN_CERTIFICATEPROFILENAME);
       if(certprofile != null){
         if(!certprofile.trim().equals("")){
           if(request.getParameter(BUTTON_SAVE) != null){
             CertificateProfile certificateprofiledata = cabean.getCertificateProfile(certprofile);
             // Save changes.
       
             String value = request.getParameter(TEXTFIELD_VALIDITY);
             if(value != null){
               value=value.trim();
               if(!value.equals(""))
                 certificateprofiledata.setValidity(Long.parseLong(value));
             }
  
             boolean use = false;
             value = request.getParameter(CHECKBOX_ALLOWVALIDITYOVERRIDE);
             if(value != null){
                use = value.equals(CHECKBOX_VALUE);
                certificateprofiledata.setAllowValidityOverride(use);
             }
             else
                certificateprofiledata.setAllowValidityOverride(false);
             
             value = request.getParameter(CHECKBOX_BASICCONSTRAINTS);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseBasicConstraints(use);
                 value = request.getParameter(CHECKBOX_BASICCONSTRAINTSCRITICAL); 
                 if(value != null){
                   certificateprofiledata.setBasicConstraintsCritical(value.equals(CHECKBOX_VALUE));
                 } 
                 else
                   certificateprofiledata.setBasicConstraintsCritical(false);
             }
             else{
                 certificateprofiledata.setUseBasicConstraints(false);
                 certificateprofiledata.setBasicConstraintsCritical(false); 
             }      
             
             use = false;
             value = request.getParameter(CHECKBOX_USEPATHLENGTHCONSTRAINT);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUsePathLengthConstraint(use);
                 value = request.getParameter(TEXTFIELD_PATHLENGTHCONSTRAINT); 
                 if(value != null){
                   certificateprofiledata.setPathLengthConstraint(Integer.parseInt(value));
                 } 
             }
             else{
                 certificateprofiledata.setUsePathLengthConstraint(false);
                 certificateprofiledata.setPathLengthConstraint(0); 
             }             
       
             use = false;
             value = request.getParameter(CHECKBOX_KEYUSAGE);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseKeyUsage(use);
                 value = request.getParameter(CHECKBOX_KEYUSAGECRITICAL); 
                 if(value != null)
                   certificateprofiledata.setKeyUsageCritical(value.equals(CHECKBOX_VALUE)); 
                 else
                   certificateprofiledata.setKeyUsageCritical(false); 
             }  
             else{
                 certificateprofiledata.setUseKeyUsage(false);
                 certificateprofiledata.setKeyUsageCritical(false); 
             }
    
             use = false;
             value = request.getParameter(CHECKBOX_SUBJECTKEYIDENTIFIER);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseSubjectKeyIdentifier(use);
                 value = request.getParameter(CHECKBOX_SUBJECTKEYIDENTIFIERCRITICAL); 
                 if(value != null)
                   certificateprofiledata.setSubjectKeyIdentifierCritical(value.equals(CHECKBOX_VALUE)); 
                 else
                   certificateprofiledata.setSubjectKeyIdentifierCritical(false); 
             }
             else{
                 certificateprofiledata.setUseSubjectKeyIdentifier(false);
                 certificateprofiledata.setSubjectKeyIdentifierCritical(false); 
             }

             use = false;
             value = request.getParameter(CHECKBOX_AUTHORITYKEYIDENTIFIER);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseAuthorityKeyIdentifier(use);
                 value = request.getParameter(CHECKBOX_AUTHORITYKEYIDENTIFIERCRITICAL); 
                 if(value != null)
                   certificateprofiledata.setAuthorityKeyIdentifierCritical(value.equals(CHECKBOX_VALUE)); 
                 else
                   certificateprofiledata.setAuthorityKeyIdentifierCritical(false); 
             }
             else{
                 certificateprofiledata.setUseAuthorityKeyIdentifier(false);
                 certificateprofiledata.setAuthorityKeyIdentifierCritical(false); 
             }

             use = false;
             value = request.getParameter(CHECKBOX_SUBJECTALTERNATIVENAME);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseSubjectAlternativeName(use);
                 value = request.getParameter(CHECKBOX_SUBJECTALTERNATIVENAMECRITICAL); 
                 if(value != null)
                   certificateprofiledata.setSubjectAlternativeNameCritical(value.equals(CHECKBOX_VALUE)); 
                 else
                   certificateprofiledata.setSubjectAlternativeNameCritical(false); 
             }
             else{
                 certificateprofiledata.setUseSubjectAlternativeName(false);
                 certificateprofiledata.setSubjectAlternativeNameCritical(false); 
             }

             value = request.getParameter(CHECKBOX_SUBJECTDIRATTRIBUTES);
             if(value != null){                  
                  certificateprofiledata.setUseSubjectDirAttributes(value.equals(CHECKBOX_VALUE));
             } else {
                 certificateprofiledata.setUseSubjectDirAttributes(false);
             }

             use = false;
             value = request.getParameter(CHECKBOX_CRLDISTRIBUTIONPOINT);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseCRLDistributionPoint(use);
                 value = request.getParameter(CHECKBOX_CRLDISTRIBUTIONPOINTCRITICAL); 
                 if(value != null)
                   certificateprofiledata.setCRLDistributionPointCritical(value.equals(CHECKBOX_VALUE)); 
                 else
                   certificateprofiledata.setCRLDistributionPointCritical(false); 
                   
                 value = request.getParameter(CHECKBOX_USEDEFAULTCRLDISTRIBUTIONPOINT); 
                 if(value != null)
                   certificateprofiledata.setUseDefaultCRLDistributionPoint(value.equals(CHECKBOX_VALUE)); 
                 else
                   certificateprofiledata.setUseDefaultCRLDistributionPoint(false); 
                   
                 value = request.getParameter(TEXTFIELD_CRLDISTURI);
                 if(value != null && !certificateprofiledata.getUseDefaultCRLDistributionPoint()){
                   value=value.trim();
                   certificateprofiledata.setCRLDistributionPointURI(value);
                 } 
                 value = request.getParameter(TEXTFIELD_CRLISSUER);
                 if(value != null && !certificateprofiledata.getUseDefaultCRLDistributionPoint()){
                   value=value.trim();
                   certificateprofiledata.setCRLIssuer(value);
                 } 
                 
             }
             else{
                 certificateprofiledata.setUseCRLDistributionPoint(false);
                 certificateprofiledata.setCRLDistributionPointCritical(false); 
                 certificateprofiledata.setCRLDistributionPointURI("");
             } 

             use = false;
             value = request.getParameter(CHECKBOX_USECERTIFICATEPOLICIES);
             if(value != null) {
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseCertificatePolicies(use);
                 value = request.getParameter(CHECKBOX_CERTIFICATEPOLICIESCRITICAL); 
                 if(value != null) {
				   certificateprofiledata.setCertificatePoliciesCritical(value.equals(CHECKBOX_VALUE)); 
                 } else {
					 certificateprofiledata.setCertificatePoliciesCritical(false); 
				 }

				 value = request.getParameter(TEXTFIELD_CERTIFICATEPOLICYID);
				 if(value != null){
				   value = value.trim();
				   certificateprofiledata.setCertificatePolicyId(value);
				 }
						
					value = request.getParameter(TEXTFIELD_POLICYNOTICE_CPSURL);
					if( value != null && !"".equals(value) ){
						value = value.trim();
						certificateprofiledata.setCpsUrl(value);
					}
						
					value = request.getParameter(TEXTAREA_POLICYNOTICE_UNOTICE);
					if( value != null && !"".equals(value) ){
						value = value.trim();
						certificateprofiledata.setUserNoticeText(value);
					}
 
            } else{
                 certificateprofiledata.setUseCertificatePolicies(false);
                 certificateprofiledata.setCertificatePoliciesCritical(false); 
                 certificateprofiledata.setCertificatePolicyId("");
                 certificateprofiledata.setCpsUrl("");
                 certificateprofiledata.setUserNoticeText("");
             } 

              String[] values = request.getParameterValues(SELECT_AVAILABLEBITLENGTHS); 
              if(values != null){
                int[] abl = new int[values.length];
                for(int i=0; i< values.length;i++){
                  abl[i] = Integer.parseInt(values[i]);
                }
                certificateprofiledata.setAvailableBitLengths(abl);
              }



              values = request.getParameterValues(SELECT_KEYUSAGE);
              boolean[] ku = new boolean[ keyusagetexts.length]; 
              if(values != null){
                 for(int i=0; i < values.length; i++){
                    ku[Integer.parseInt(values[i])] = true;
                 }
              }
              certificateprofiledata.setKeyUsage(ku);      
 
             value = request.getParameter(CHECKBOX_USEEXTENDEDKEYUSAGE);
             if(value != null && value.equals(CHECKBOX_VALUE)){
               certificateprofiledata.setUseExtendedKeyUsage(true); 
               value = request.getParameter(CHECKBOX_EXTENDEDKEYUSAGECRITICAL); 
               if(value != null)
                 certificateprofiledata.setExtendedKeyUsageCritical(value.equals(CHECKBOX_VALUE));
               else
                 certificateprofiledata.setExtendedKeyUsageCritical(false);
                 
               values = request.getParameterValues(SELECT_EXTENDEDKEYUSAGE);
               ArrayList eku = new ArrayList(); 
                if(values != null){
                   for(int i=0; i < values.length; i++){
                      eku.add(new Integer(values[i]));
                   }
                }
                certificateprofiledata.setExtendedKeyUsage(eku);    
              }
              else{
                certificateprofiledata.setUseExtendedKeyUsage(false); 
                certificateprofiledata.setExtendedKeyUsageCritical(false); 
                certificateprofiledata.setExtendedKeyUsage(new ArrayList());        
              }

              value = request.getParameter(SELECT_TYPE);
              int type  = CertificateProfile.TYPE_ENDENTITY;
              if(value != null){
                type = Integer.parseInt(value);
              }
              certificateprofiledata.setType(type);    
              
              value = request.getParameter(CHECKBOX_ALLOWKEYUSAGEOVERRIDE);
              if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setAllowKeyUsageOverride(use);
              }
              else
                 certificateprofiledata.setAllowKeyUsageOverride(false);

              values = request.getParameterValues(SELECT_AVAILABLECAS);
              ArrayList availablecas = new ArrayList(); 
              if(values != null){
                 for(int i=0; i < values.length; i++){
                    if(Integer.parseInt(values[i]) == CertificateProfile.ANYCA){
                      availablecas = new ArrayList();
                      availablecas.add(new Integer(CertificateProfile.ANYCA));
                      break;  
                    }
                    availablecas.add(new Integer(values[i]));
                 }
              }
              certificateprofiledata.setAvailableCAs(availablecas);

              values = request.getParameterValues(SELECT_AVAILABLEPUBLISHERS);
              ArrayList availablepublishers = new ArrayList(); 
              if(values != null){
                 for(int i=0; i < values.length; i++){
                    availablepublishers.add(new Integer(values[i]));
                 }
              }
              certificateprofiledata.setPublisherList(availablepublishers);

             use = false;
             value = request.getParameter(CHECKBOX_USEOCSPSERVICELOCATOR);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseOCSPServiceLocator(use);

                 value = request.getParameter(CHECKBOX_USEDEFAULTOCSPSERVICELOCALTOR);
                 if(value != null){
                   certificateprofiledata.setUseDefaultOCSPServiceLocator(value.equals(CHECKBOX_VALUE));
                 }else{
                   certificateprofiledata.setUseDefaultOCSPServiceLocator(false);
                 }          
                  
                 value = request.getParameter(TEXTFIELD_OCSPSERVICELOCATOR);
                 if(value != null && !certificateprofiledata.getUseDefaultOCSPServiceLocator()){
                   value=value.trim();
                   certificateprofiledata.setOCSPServiceLocatorURI(value);
                 } 
             }
             else{
                 certificateprofiledata.setUseOCSPServiceLocator(false);                 
                 certificateprofiledata.setOCSPServiceLocatorURI("");
             }
              
             use = false;
             value = request.getParameter(CHECKBOX_USEMSTEMPLATE);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseMicrosoftTemplate(use);

                 value = request.getParameter(SELECT_MSTEMPLATE);
                 if(value != null){
                   value=value.trim();
                   certificateprofiledata.setMicrosoftTemplate(value);
                 } 
             }
             else{
                 certificateprofiledata.setUseMicrosoftTemplate(false);                 
                 certificateprofiledata.setMicrosoftTemplate("");
             }

             use = false;
             value = request.getParameter(CHECKBOX_USECNPOSTFIX);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseCNPostfix(use);

                 value = request.getParameter(TEXTFIELD_CNPOSTFIX);
                 if(value != null){
                   certificateprofiledata.setCNPostfix(value);
                 } 
             }
             else{
                 certificateprofiledata.setUseCNPostfix(false);                 
                 certificateprofiledata.setCNPostfix("");
             }
             
             use = false;
             value = request.getParameter(CHECKBOX_USESUBJECTDNSUBSET);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseSubjectDNSubSet(use);

                 values = request.getParameterValues(SELECT_SUBJECTDNSUBSET);
                 if(values != null){
                     ArrayList usefields = new ArrayList();
                     for(int i=0;i< values.length;i++){
                         usefields.add(new Integer(values[i]));	
                     }                     
                     certificateprofiledata.setSubjectDNSubSet(usefields);
                 }
             }
             else{
                 certificateprofiledata.setUseSubjectDNSubSet(false);                 
                 certificateprofiledata.setSubjectDNSubSet(new ArrayList());
             }
             
             use = false;
             value = request.getParameter(CHECKBOX_USESUBJECTALTNAMESUBSET);
             if(value != null){
                 use = value.equals(CHECKBOX_VALUE);
                 certificateprofiledata.setUseSubjectAltNameSubSet(use);

                 values = request.getParameterValues(SELECT_SUBJECTALTNAMESUBSET);
                 if(values != null){
                     ArrayList usefields = new ArrayList();
                     for(int i=0;i< values.length;i++){
                         usefields.add(new Integer(values[i]));	
                     }                     
                     certificateprofiledata.setSubjectAltNameSubSet(usefields);
                 }
             }
             else{
                 certificateprofiledata.setUseSubjectAltNameSubSet(false);                 
                 certificateprofiledata.setSubjectAltNameSubSet(new ArrayList());
             }
             
             certificateprofiledata.setUseQCStatement(false);
             certificateprofiledata.setQCStatementCritical(false);
             certificateprofiledata.setUsePkixQCSyntaxV2(false);
             certificateprofiledata.setUseQCEtsiQCCompliance(false);
             certificateprofiledata.setUseQCEtsiSignatureDevice(false);
             certificateprofiledata.setUseQCEtsiValueLimit(false);
             certificateprofiledata.setQCSemanticsId("");
             certificateprofiledata.setQCStatementRAName("");
             certificateprofiledata.setQCEtsiValueLimit(0);
             certificateprofiledata.setQCEtsiValueLimitExp(0);
             certificateprofiledata.setQCEtsiValueLimitCurrency("");
             certificateprofiledata.setUseQCCustomString(false);
             certificateprofiledata.setQCCustomStringOid("");
             certificateprofiledata.setQCCustomStringText("");
             
             value = request.getParameter(CHECKBOX_USEQCSTATEMENT);
             if(value != null){                  
                  certificateprofiledata.setUseQCStatement(value.equals(CHECKBOX_VALUE));
                  
                  if(certificateprofiledata.getUseQCStatement()){
                     value = request.getParameter(CHECKBOX_QCSTATEMENTCRITICAL);
                     if(value != null) {
                       certificateprofiledata.setQCStatementCritical(value.equals(CHECKBOX_VALUE));
                     }
                     value = request.getParameter(CHECKBOX_USEPKIXQCSYNTAXV2);
                     if(value != null) {
                       certificateprofiledata.setUsePkixQCSyntaxV2(value.equals(CHECKBOX_VALUE));
                     }
                     value = request.getParameter(CHECKBOX_USEQCETSIQCCOMPLIANCE);
                     if(value != null) {
                       certificateprofiledata.setUseQCEtsiQCCompliance(value.equals(CHECKBOX_VALUE));
                     }
                     value = request.getParameter(CHECKBOX_USEQCETSISIGNATUREDEVICE);
                     if(value != null) {
                       certificateprofiledata.setUseQCEtsiSignatureDevice(value.equals(CHECKBOX_VALUE));
                     }
                     value = request.getParameter(CHECKBOX_USEQCETSIVALUELIMIT);
                     if(value != null) {
                       certificateprofiledata.setUseQCEtsiValueLimit(value.equals(CHECKBOX_VALUE));
                       certificateprofiledata.setQCEtsiValueLimit(new Integer(request.getParameter(TEXTFIELD_QCETSIVALUELIMIT)).intValue());
                       certificateprofiledata.setQCEtsiValueLimitExp(new Integer(request.getParameter(TEXTFIELD_QCETSIVALUELIMITEXP)).intValue());  
                       certificateprofiledata.setQCEtsiValueLimitCurrency(request.getParameter(TEXTFIELD_QCETSIVALUELIMITCUR));                                                                    
                     }                     
                     value = request.getParameter(CHECKBOX_USEQCCUSTOMSTRING);
                     if(value != null) {
                       certificateprofiledata.setUseQCCustomString(value.equals(CHECKBOX_VALUE));
                       certificateprofiledata.setQCCustomStringOid(request.getParameter(TEXTFIELD_QCCUSTOMSTRINGOID));
                       certificateprofiledata.setQCCustomStringText(request.getParameter(TEXTFIELD_QCCUSTOMSTRINGTEXT));  
                     }                     
                     certificateprofiledata.setQCSemanticsId(request.getParameter(TEXTFIELD_QCSSEMANTICSID));
                     certificateprofiledata.setQCStatementRAName(request.getParameter(TEXTFIELD_QCSTATEMENTRANAME));
                  }
             }
             
              cabean.changeCertificateProfile(certprofile,certificateprofiledata);
           }
           if(request.getParameter(BUTTON_CANCEL) != null){
              // Don't save changes.
           }
             includefile="certificateprofilespage.jspf";
         }
      }
    }
  }

 // Include page
  if( includefile.equals("certificateprofilepage.jspf")){ 
%>
   <%@ include file="certificateprofilepage.jspf" %>
<%}
  if( includefile.equals("certificateprofilespage.jspf")){ %>
   <%@ include file="certificateprofilespage.jspf" %> 
<%}

   // Include Footer 
   String footurl =   globalconfiguration.getFootBanner(); %>
   
  <jsp:include page="<%= footurl %>" />

</body>
</html>