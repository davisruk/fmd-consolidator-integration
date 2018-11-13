package org.davisr.spring.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.davisr.spring.camel.fmd.bag.model.Bag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BagRoutes extends RouteBuilder {

    @Value("${server.port}")
    String port;

    @Value("${server.address}")
    String host;

	@Override
	public void configure() throws Exception {
    	restConfiguration()
			.host(host)
			.port(port)
			.apiContextPath("/api-doc")
			.apiProperty("api.title", "Test REST API")
			.apiProperty("api.version", "v1")
			.apiContextRouteId("doc-api")
			.component("servlet")
			.bindingMode(RestBindingMode.auto);

    	rest("/bags")
		.post()
			.type(Bag.class)
			.to("direct:createBag")
		.get("/{id}")
			.outType(Bag.class)
			.to("direct:getBag");
    
    	from("direct:createBag")
    		.to("jpa:org.davisruk.spring.camel.fmd.bag.model.Bag");
    	
    	from("direct:getBag")
			.to("sql:select * from BAG join PACK where BAG.id = :#${header.id}?dataSource=dataSource&outputType=SelectList")
			.bean("bagTransformer", "mapBag");
	
	}

}
