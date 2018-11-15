package org.davisr.spring.camel.fmd.database;

import java.sql.SQLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class DBConfig {

	@Bean(name = "h2WebServer", initMethod="start", destroyMethod="stop")
	public org.h2.tools.Server h2WebServer() throws SQLException {
	    return org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
	}


	@Bean(initMethod="start", destroyMethod="stop")
	@DependsOn(value = "h2WebServer")
	public org.h2.tools.Server h2Server() throws SQLException {
	    return org.h2.tools.Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
	}
}
