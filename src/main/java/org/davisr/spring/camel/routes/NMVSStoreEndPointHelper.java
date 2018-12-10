package org.davisr.spring.camel.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.CxfEndpointConfigurer;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.AbstractWSDLBasedEndpointFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.davisr.spring.camel.StoreProperties;
import org.davisr.spring.camel.fmd.keystore.KeyStoreInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NMVSStoreEndPointHelper {

    @Value("${http.proxy.server}")
    private String proxyServer;
    
    @Value("${http.proxy.port}")
    private Integer proxyPort;
    
    @Value("${nmvs.address}")
    private String nmvsAddress;
    
    @Value("${nmvs.wsdl.url}")
    private String nmvsWsdlUrl;
    
    @Autowired
	private StoreProperties storeProps;
    
	public void setupEndpoints(CamelContext ctx) {

		storeProps.getKeyStoreInfo().forEach(value -> {
			try {
				ctx.addEndpoint("cfx:fmd:" + value.getId(), fmdEndPoint(ctx, value));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
    public CxfEndpoint fmdEndPoint(CamelContext ctx, KeyStoreInfo keyInfo) {
    	CxfEndpoint ep = getBasicEndpoint(ctx);
    	ep.setCxfEndpointConfigurer(fmdEndpointConfigurer(keyInfo));
    	ep.setProducerCacheKey(keyInfo.getCacheKey());
    	return ep;
    }
    
    public String[] routeTo(String store) {
    	return new String[] {"cfx:fmd:" + store};
    }
    
    private CxfEndpoint getBasicEndpoint (CamelContext ctx) {
    	CxfEndpoint ep = new CxfEndpoint();
    	ep.setCamelContext(ctx);
    	ep.setAddress(nmvsAddress);
    	ep.setServiceClass(org.davisr.spring.camel.nmvs.ISinglePackServices.class);
    	ep.setWsdlURL(nmvsWsdlUrl);
    	return ep;
    }
    
    private CxfEndpointConfigurer fmdEndpointConfigurer(KeyStoreInfo keyInfo) {
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
                    keyStoreParameters.setResource(keyInfo.getKeystoreName());
                    keyStoreParameters.setPassword(keyInfo.getKeystorePassword());
                    keyStoreParameters.setType(keyInfo.getKeyStoreType());

                    KeyManagersParameters keyManagersParameters = new KeyManagersParameters();
                    keyManagersParameters.setKeyStore(keyStoreParameters);
                    keyManagersParameters.setKeyPassword(keyInfo.getKeystorePassword());
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
