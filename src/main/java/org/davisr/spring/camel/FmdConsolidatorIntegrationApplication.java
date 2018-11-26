package org.davisr.spring.camel;

import javax.annotation.PostConstruct;

import org.apache.camel.impl.DefaultShutdownStrategy;
import org.apache.camel.spi.ShutdownStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FmdConsolidatorIntegrationApplication {

    @Bean 
    public ShutdownStrategy shutdownStrategy() { 
             DefaultShutdownStrategy strategy = new DefaultShutdownStrategy(); 
             strategy.setTimeout(10); 
             return strategy; 
    }
    
	public static void main(String[] args) {
		SpringApplication.run(FmdConsolidatorIntegrationApplication.class, args);
	}

	@PostConstruct
	public void startDBManager() {
			
		//hsqldb
		//DatabaseManagerSwing.main(new String[] { "--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", "" });

		//derby
		//DatabaseManagerSwing.main(new String[] { "--url", "jdbc:derby:memory:testdb", "--user", "", "--password", "" });

		//h2
		//DatabaseManagerSwing.main(new String[] { "--url", "jdbc:h2:mem:testdb", "--user", "sa", "--password", "" });

	}	
}
