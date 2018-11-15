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
				.to("direct:getBagById")
	    	.get()
				.to("direct:getAllBags");				
    
    	rest("/packs")
			.get("/{id}")
			.to("direct:getPackById")
	    	.get()
			.to("direct:getAllPacks");				

    	
    	from("direct:createBag")
    		.routeId("createBag")
    		.bean("bagDbService", "saveBag");
    	
    	from("direct:getBagById")
			.routeId("getBagById")
			.bean("bagDbService", "getBagById(${header.id})");

    	from("direct:getAllBags")
			.routeId("getAllBags")
			.bean("bagDbService", "getAllBags");
    	
    	from("direct:getPackById")
			.routeId("getPackById")
			.bean("packDbService", "getPackById(${header.id})");
    	
    	from("direct:getAllPacks")
			.routeId("getAllPacks")
			.bean("packDbService", "getAllPacks");
	}

}
