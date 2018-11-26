package org.davisr.spring.camel.routes;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.CxfEndpointConfigurer;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.AbstractWSDLBasedEndpointFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.davisr.spring.camel.fmd.bag.model.Bag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BagRoutes extends RouteBuilder {

    @Value("${server.port}")
    String port;

    @Value("${server.address}")
    String host;

    @Value("${http.client.ssl.trust-store}")
    private String keyStore;
    
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

    	rest("/dispense")
    		.get("/{id}")
    			.to("direct:dispenseBag");
    	
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
    	
    	// currently only a single pack - change to bag later 
    	from("direct:dispenseBag")
			.routeId("dispenseBag")
			.to("direct:getPackById")
			.bean("singlePackRequestBuilder", "buildG110Request")
    		.setHeader("operationName", constant("G110Verify"))
    		.setHeader("operationNamespace", constant("urn:services.nmvs.eu:v2.0"))
    		.to(fmdEndPoint(getContext()))
    		.log("The response was ${body[0]}");
    	
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
                HTTPClientPolicy policy = new HTTPClientPolicy();
                policy.setProxyServer("gateway.zscalertwo.net");
                policy.setProxyServerPort(9480);
                conduit.setClient(policy);
                TLSClientParameters tls = new TLSClientParameters();

                KeyStoreParameters keyStoreParameters = new KeyStoreParameters();
                keyStoreParameters.setResource(keyStore);
                //keyStoreParameters.setResource("C:\\Java\\jdk\\1.8.0_162\\jre\\lib\\security\\cacerts");
                keyStoreParameters.setPassword("changeit");
                keyStoreParameters.setType("JKS");

                KeyManagersParameters keyManagersParameters = new KeyManagersParameters();
                keyManagersParameters.setKeyStore(keyStoreParameters);
                keyManagersParameters.setKeyPassword("wmD6xw1u");

                TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
                trustManagersParameters.setKeyStore(keyStoreParameters);
                tls.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });
                tls.setDisableCNCheck(true);
                
                try {
	                tls.setKeyManagers(keyManagersParameters.createKeyManagers());
	                tls.setTrustManagers(trustManagersParameters.createTrustManagers());
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
