package fq;
import java.io.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weblogic.j2ee.descriptor.wl.QueueBean;
import weblogic.management.configuration.DomainMBean;
import weblogic.management.configuration.JMSConnectionFactoryMBean;
import weblogic.management.configuration.JMSQueueMBean;
import weblogic.management.configuration.JMSServerMBean;
import weblogic.management.configuration.JMSSystemResourceMBean;
import weblogic.management.j2ee.SessionBeanMBean;
import weblogic.management.mbeans.custom.JMSSystemResource;
import weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean;
import weblogic.management.jmx.MBeanServerInvocationHandler;
public class input {
    protected final Log logger = LogFactory.getLog(getClass());
      private JMXConnector jmxConn = null;
      DomainMBean domainMBean = null;
      DomainRuntimeServiceMBean domainRuntimeServiceMBean = null;
      SessionBeanMBean sessionManagementMBean = null;
      public MBeanServerConnection openConnection(String hostname, int port, String username, String password) throws IOException,MalformedURLException {
            JMXServiceURL serviceURL = new JMXServiceURL("t3", hostname, port, "/jndi/" + DomainRuntimeServiceMBean.MBEANSERVER_JNDI_NAME);
            Hashtable<String,Object> h = new Hashtable<String,Object>();
            h.put(Context.SECURITY_PRINCIPAL, username);
            h.put(Context.SECURITY_CREDENTIALS, password);
            h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");
            //h.put("jmx.remote.x.request.waiting.timeout", new Long(60000));
            return JMXConnectorFactory.connect(serviceURL, h).getMBeanServerConnection();
      }    

      public void closeConnection() {
            try {
                  jmxConn.close();
            } catch (IOException ex) {
                  logger.error("Exception in closing connection: "+ex.getMessage());
            }finally{
                  try{
                        jmxConn.close();
                  } catch (IOException ex) {
                        logger.error("Exception in final stage of closing connection: "+ex.getMessage());
                  }          
            }
      }
      
      public static void main(String [] args)
      {
    	  PrintStream o;
            fetch x = new fetch();
            try {
        	      o = new PrintStream(new File("A.html"));
                  MBeanServerConnection bco = x.openConnection("hostname", 7001, "username", "Password");

                  DomainRuntimeServiceMBean domainRuntimeServiceMBean = (DomainRuntimeServiceMBean) MBeanServerInvocationHandler.newProxyInstance(bco, new ObjectName(DomainRuntimeServiceMBean.OBJECT_NAME));       
                  DomainMBean dem = domainRuntimeServiceMBean.getDomainConfiguration();
                  JMSSystemResourceMBean[] jmsSRs = dem.getJMSSystemResources();
                   
                  System.setOut(o);
                  System.out.println("<!DOCTYPE html>");
                  System.out.println("<html");
                  System.out.println("<head>");
                  System.out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"mystyle.css\">");
                  System.out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>");
                  System.out.println("<script src=\"https://ajax.aspnetcdn.com/ajax/jquery/jquery-1.9.0.js\"></script>");
                  System.out.println("<script>");
                  
                  System.out.println("$(document).ready(function () {");
                  System.out.println("$('#btnRight').click(function (e) {\nvar selectedOpts = $('#lstBox1 option:selected');");
                  System.out.println("if (selectedOpts.length == 0)\n {alert(\"Nothing to move.\");\ne.preventDefault();\n}");
                  System.out.println("$('#lstBox2').append($(selectedOpts).clone());\n$(selectedOpts).remove();\ne.preventDefault();\n });"); 	    
                  System.out.println("$('#btnAllRight').click(function (e) {\nvar selectedOpts = $('#lstBox1 option');\nif (selectedOpts.length == 0) {\nalert(\"Nothing to move.\");\n e.preventDefault();}");	        
                	        
                  System.out.println("$('#lstBox2').append($(selectedOpts).clone());\n$(selectedOpts).remove();\n e.preventDefault();\n});\n$('#btnLeft').click(function (e) {\nvar selectedOpts = $('#lstBox2 option:selected');\nif (selectedOpts.length == 0) {");	        
                  System.out.println("alert(\"Nothing to move.\");\n e.preventDefault();\n}\n$('#lstBox1').append($(selectedOpts).clone());\n$(selectedOpts).remove();\ne.preventDefault();\n});");
                  System.out.println("$('#btnAllLeft').click(function (e) {\nvar selectedOpts = $('#lstBox2 option');");            
                	           
                  System.out.println("if (selectedOpts.length == 0) {\nalert(\"Nothing to move.\");\ne.preventDefault();\n}\n$('#lstBox1').append($(selectedOpts).clone());\n$(selectedOpts).remove();\ne.preventDefault();\n});\n});\n</script>\n<title> Dashboard </title>\n</head>");
                	
                  
                  System.out.println("<body>\n<div class=\"subject-info-box-1\">\n<select multiple=\"multiple\" id='lstBox1' class=\"form-control\" size=\"20\" style=\"width:500px\">");
                  int i=0;
                  JMSServerMBean[] jmsSvrs = dem.getJMSServers();
                  for(JMSServerMBean jmsSvr : jmsSvrs)
                  {	
                	    i++;
                	  	System.setOut(o);
                        System.out.println("JMS Servername: "+jmsSvr.getName());
                  }

                  for(JMSSystemResourceMBean jmsSR : jmsSRs)
                  {
                        System.err.println(jmsSR.getName());
                        QueueBean[] qbeans = jmsSR.getJMSResource().getQueues();
                        for(QueueBean qbean : qbeans)
                        {
                        	 System.setOut(o);
                             System.out.println("<option value=\""+qbean.getJNDIName()+"\"> \"JNDI NAME: "+qbean.getJNDIName()+" queuename : "+qbean.getName()+"\"</option>");
                        }
                  } 
            	}
            	
                  catch (Exception e) 
                  {
                  // TODO Auto-generated catch block
                	  e.printStackTrace();
                  }
                 System.out.println("<div class=\"subject-info-arrows text-center\">");
                 System.out.println("<input type=\"button\" id=\"btnAllRight\" value=\">>\" class=\"btn btn-default\" /><br />");
                 System.out.println("<input type=\"button\" id=\"btnRight\" value=\">\" class=\"btn btn-default\" /><br />");
                 System.out.println("<input type=\"button\" id=\"btnLeft\" value=\"<\" class=\"btn btn-default\" /><br />");
                 System.out.println("<input type=\"button\" id=\"btnAllLeft\" value=\"<<\" class=\"btn btn-default\" />\n</div>");
                 
                 System.out.println("<div class=\"subject-info-box-2\">\n<select multiple=\"multiple\" id='lstBox2' class=\"form-control\" size=\"20\" style=\"width:500px\">\n</select>\n</div>\n<div class=\"clearfix\"></div>");
                 System.out.println("<input type=\"submit\" value=\"Next Page\">\n</body>\n</html>");
            	}
}
