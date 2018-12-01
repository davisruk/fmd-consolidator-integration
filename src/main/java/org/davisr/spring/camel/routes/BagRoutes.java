package org.davisr.spring.camel.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.CxfEndpointConfigurer;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.AbstractWSDLBasedEndpointFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.davisr.spring.camel.fmd.bag.model.Bag;
import org.davisr.spring.camel.fmd.nmvs.request.FMDRequest;
import org.davisr.spring.camel.fmd.nmvs.response.FMDResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BagRoutes extends RouteBuilder {

    @Value("${server.port}")
    String port;

    @Value("${server.address}")
    String host;

    @Value("${http.client.ssl.key-store}")
    private String keyStore;
    
    @Value("${http.proxy.server}")
    private String proxyServer;
    
    @Value("${http.proxy.port}")
    private Integer proxyPort;
    
    @Value("${http.client.ssl.key-store.password}")
    private String keyStorePassword;
    
    @Value("${http.client.ssl.key-store.type}")
    private String keyStoreType;

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

    	rest("/verify")
		.post()
			.outType(FMDResponse.class)
			.type(Bag.class)
			.to("direct:verifyBag");
    	
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
    	
    	from("direct:verifyBag")
			.routeId("verifyBag")
			.to("direct:createBag")
			.bean("singlePackRequestBuilder", "getPacksFromBag")
			.split(body(), new BaggregationStrategy())
				.bean("singlePackRequestBuilder", "buildG110Request")
				.setHeader("operationName", constant("G110Verify"))
				.setHeader("operationNamespace", constant("urn:services.nmvs.eu:v2.0"))
				.to(fmdEndPoint(getContext()))
			.end()
			.to("bean:fmdResponseBuilder?method=buildBagResponse(${body}, ${property.originalRequest})")
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

    	from("direct:dispenseBag")
			.routeId("dispenseBag")
			.to("direct:createBag")
			.bean("singlePackRequestBuilder", "getPacksFromBag")
			.split(body(), new BaggregationStrategy())
				.bean("singlePackRequestBuilder", "buildG120Request")
				.setHeader("operationName", constant("G120Dispense"))
				.setHeader("operationNamespace", constant("urn:services.nmvs.eu:v2.0"))
				.to(fmdEndPoint(getContext()))
			.end()
			.to("bean:fmdResponseBuilder?method=buildBagResponse(${body}, ${property.originalRequest})")
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

    	from("direct:undoDispenseBag")
			.routeId("undoDispenseBag")
			.to("direct:createBag")
			.bean("singlePackRequestBuilder", "getPacksFromBag")
			.split(body(), new BaggregationStrategy())
				.bean("singlePackRequestBuilder", "buildG121Request")
				.setHeader("operationName", constant("G121UndoDispense"))
				.setHeader("operationNamespace", constant("urn:services.nmvs.eu:v2.0"))
				.to(fmdEndPoint(getContext()))
			.end()
			.to("bean:fmdResponseBuilder?method=buildBagResponse(${body}, ${property.originalRequest})")
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

    	from("direct:fmdRequest")
			.routeId("fmdRequest")
			.setProperty("originalRequest", body())
			.choice()
			.when().method("singlePackRequestBuilder", "isVerifyRequest")
				.bean("singlePackRequestBuilder", "getBag")
				.to("direct:verifyBag")
			.when().method("singlePackRequestBuilder", "isDispenseRequest")
				.bean("singlePackRequestBuilder", "getBag")
				.to("direct:dispenseBag")
			.when().method("singlePackRequestBuilder", "isUndoDispenseRequest")
				.bean("singlePackRequestBuilder", "getBag")
				.to("direct:undoDispenseBag")
				
			.end();
	}

    public CxfEndpoint fmdEndPoint(CamelContext ctx) {
    	CxfEndpoint ep = new CxfEndpoint();
    	ep.setCamelContext(ctx);
    	ep.setAddress("https://ws-single-transactions-int-bp.nmvs.eu:8443/WS_SINGLE_TRANSACTIONS_V1/SinglePackServiceV20");
    	ep.setServiceClass(org.davisr.spring.camel.nmvs.ISinglePackServices.class);
    	ep.setWsdlURL("WS_SINGLE_PACK.wsdl");
    	ep.setCxfEndpointConfigurer(fmdEndpointConfigurer());
    	return ep;
    }
    
    private CxfEndpointConfigurer fmdEndpointConfigurer() {
    	return new CxfEndpointConfigurer() {

			@Override
			public void configure(AbstractWSDLBasedEndpointFactory factoryBean) {
			}

			@Override
			public void configureClient(Client client) {
	            
				HTTPConduit conduit = (HTTPConduit) client.getConduit();
				if (proxyServer != null && proxyServer.length() > 0) {
					
		            HTTPClientPolicy policy = new HTTPClientPolicy();
	                policy.setProxyServer(proxyServer);
	                policy.setProxyServerPort(proxyPort);
	                conduit.setClient(policy);
				}
                try {
                    TLSClientParameters tls = new TLSClientParameters();

                    KeyStoreParameters keyStoreParameters = new KeyStoreParameters();
                    keyStoreParameters.setResource(keyStore);
                    keyStoreParameters.setPassword(keyStorePassword);
                    keyStoreParameters.setType(keyStoreType);

                    KeyManagersParameters keyManagersParameters = new KeyManagersParameters();
                    keyManagersParameters.setKeyStore(keyStoreParameters);
                    keyManagersParameters.setKeyPassword(keyStorePassword);
                	tls.setKeyManagers(keyManagersParameters.createKeyManagers());
	                conduit.setTlsClientParameters(tls);
                } catch (Exception e) {
                	e.printStackTrace();
                }
			}

			@Override
			public void configureServer(Server server) {
			}
    	};
    }
}
