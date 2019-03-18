package org.davisr.spring.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.davisr.spring.camel.fmd.nmvs.request.FMDRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BagRoutes extends RouteBuilder {

    @Value("${server.port}")
    String port;

    @Value("${server.address}")
    String host;

    @Autowired
    NMVSStoreEndPointHelper endpointConfig;
    
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

    	rest("/fmd")
    		.post()
			.type(FMDRequest.class)
			.to("direct:fmdRequest");
    		
    	rest("/bags")
			.get("/{id}")
				.to("direct:getBagById")
	    	.get()
				.to("direct:getAllBags");				
    
    	rest("/packs")
			.get("/{id}")
				.to("direct:getPackById")
	    	.get()
				.to("direct:getAllPacks");				

    	endpointConfig.setupEndpoints(getContext());
    	
    	from("direct:createBag")
    		.routeId("createBag")
    		.bean("bagDbService", "saveBag")
    		.setHeader(Exchange.CONTENT_TYPE, constant("application/json"));
    	
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
    	
    	from("direct:dispenseBag")
			.routeId("dispenseBag")
			.split(body(), new BaggregationStrategy())
				.bean("singlePackRequestBuilder", "buildG120Request")
				.setHeader("operationName", constant("G120Dispense"))
				.setHeader("operationNamespace", constant("urn:services.nmvs.eu:v2.0"))
				.recipientList().method(NMVSStoreEndPointHelper.class, "routeTo(${property.originalRequest.store.id})")
			.end();

    	from("direct:undoDispenseBag")
			.routeId("undoDispenseBag")
			.split(body(), new BaggregationStrategy())
				.bean("singlePackRequestBuilder", "buildG121Request")
				.setHeader("operationName", constant("G121UndoDispense"))
				.setHeader("operationNamespace", constant("urn:services.nmvs.eu:v2.0"))
				.recipientList().method(NMVSStoreEndPointHelper.class, "routeTo(${property.originalRequest.store.id})")
			.end();
    	
    	from("direct:verifyBag")
		.routeId("verifyBag")
		.split(body(), new BaggregationStrategy())
			.bean("singlePackRequestBuilder", "buildG110Request")
			.setHeader("operationName", constant("G110Verify"))
			.setHeader("operationNamespace", constant("urn:services.nmvs.eu:v2.0"))
			.recipientList().method(NMVSStoreEndPointHelper.class, "routeTo(${property.originalRequest.store.id})")
		.end();

    	from("direct:stolenBag")
		.routeId("stolenBag")
		.split(body(), new BaggregationStrategy())
			.bean("singlePackRequestBuilder", "buildG180Request")
			.setHeader("operationName", constant("G180Stolen"))
			.setHeader("operationNamespace", constant("urn:services.nmvs.eu:v2.0"))
			.recipientList().method(NMVSStoreEndPointHelper.class, "routeTo(${property.originalRequest.store.id})")
		.end();

    	from("direct:fmdRequest")
			.routeId("fmdRequest")
			.setProperty("originalRequest", body())
			.bean("singlePackRequestBuilder", "getBag")
			//.to("direct:createBag") -- error with bag save, need to verify that label is unique
			.bean("singlePackRequestBuilder", "getPacksFromBag")
			.choice()
				.when().method("singlePackRequestBuilder", "isVerifyRequest(${property.originalRequest})")
					.to("direct:verifyBag")
				.when().method("singlePackRequestBuilder", "isDispenseRequest(${property.originalRequest})")
					.to("direct:dispenseBag")
				.when().method("singlePackRequestBuilder", "isStolenRequest(${property.originalRequest})")
					.to("direct:stolenBag")
				.when().method("singlePackRequestBuilder", "isUndoDispenseRequest(${property.originalRequest})")
					.to("direct:undoDispenseBag")
				.end()
			.to("bean:fmdResponseBuilder?method=buildBagResponse(${body}, ${property.originalRequest})")
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

	}
}
