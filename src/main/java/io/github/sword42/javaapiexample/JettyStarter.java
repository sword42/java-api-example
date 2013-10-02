package io.github.sword42.javaapiexample;

import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.webapp.*;
import org.eclipse.jetty.annotations.*;
import org.eclipse.jetty.server.nio.*;

public class JettyStarter {
	public static void main(String[] args) throws Exception{
		java.lang.System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");
		Server server = new Server();
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setOutputBufferSize(32768);
        ServerConnector http = new ServerConnector(server,new HttpConnectionFactory(http_config));
        http.setPort(8081);
        http.setIdleTimeout(30000);		
        server.setConnectors(new Connector[] { http });

		server.setStopAtShutdown(true);		
		ProtectionDomain domain = JettyStarter.class.getProtectionDomain();
		URL location = domain.getCodeSource().getLocation();
		
		// create a web app and configure it to the root context of the server
		WebAppContext webapp = new WebAppContext();
		webapp.setDescriptor("WEB-INF/web.xml");
		webapp.setConfigurations(new Configuration[]{ new AnnotationConfiguration(), 
													new WebXmlConfiguration(), 
													new WebInfConfiguration(), 
													new MetaInfConfiguration() });
		webapp.setContextPath("/");
		webapp.setWar(location.toExternalForm());
		System.out.println("WAR URL: "+location.toExternalForm());
		
		server.setHandler(webapp);        
		server.start();
		server.join(); 
	
	/*
		Server server = new Server();		
		Connector connector = new SelectChannelConnector();
		connector.setPort(8888);
		connector.setHost("127.0.0.1");
		server.addConnector(connector);
		server.setStopAtShutdown(true);
	 
		ProtectionDomain protectionDomain = JettyStarter.class.getProtectionDomain();
		URL location = protectionDomain.getCodeSource().getLocation();
		
		WebAppContext context = new WebAppContext();
		context.setServer(server);
		context.setContextPath("/");
		context.setWar(location.toExternalForm());
		System.out.println("WAR URL: ${location.toExternalForm()}");
		server.addHandler(context);
		
		try {
			server.start();
			System.in.read();
			server.stop();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}*/
	
	/*
		ProtectionDomain domain = JettyStarter.class.getProtectionDomain();
		URL location = domain.getCodeSource().getLocation();

		// create a web app and configure it to the root context of the server
		WebAppContext webapp = new WebAppContext();
		webapp.setDescriptor("WEB-INF/web.xml");
		webapp.setConfigurations(new Configuration[]{ new AnnotationConfiguration(), 
													new WebXmlConfiguration(), 
													new WebInfConfiguration(), 
													new MetaInfConfiguration() });
		webapp.setContextPath("/");
		webapp.setWar(location.toExternalForm());

		// starts the embedded server and bind it on 8081 port
		Server server = new Server(8081);
		server.setHandler(webapp);        
		server.start();
		server.join(); 
		*/
	}
}
