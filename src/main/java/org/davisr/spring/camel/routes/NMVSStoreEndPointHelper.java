package org.davisr.spring.camel.routes;

import java.util.HashMap;
import java.util.Map;

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
import org.davisr.spring.camel.fmd.keystore.KeyStoreInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("nmvsEndpointHelper")
public class NMVSStoreEndPointHelper {

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

	private static final Map<String, KeyStoreInfo> keyStores;
	static {
		keyStores = new HashMap<String, KeyStoreInfo>();
		keyStores.put("123", KeyStoreInfo.builder().id(123).keystoreName("cert/DAVI1001.p12").keystorePassword("wmD6xw1u").keyStoreType("PKCS12").build());
		keyStores.put("456", KeyStoreInfo.builder().id(123).keystoreName("cert/DAVI1001.p12").keystorePassword("wmD6xw1u").keyStoreType("PKCS12").build());
	}
	
    public void setupEndpoints(CamelContext ctx) {
		keyStores.forEach((key, value) -> {
			try {
				ctx.addEndpoint("cfx:fmd:" + key, fmdEndPoint(ctx, value));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
    public CxfEndpoint fmdEndPoint(CamelContext ctx, KeyStoreInfo keyInfo) {
    	CxfEndpoint ep = getBasicEndpoint(ctx);
    	ep.setCxfEndpointConfigurer(fmdEndpointConfigurer(keyInfo));
    	return ep;
    }
    
    public String[] routeTo(String store) {
    	return new String[] {"cfx:fmd:" + store};
    }
    
    private CxfEndpoint getBasicEndpoint (CamelContext ctx) {
    	CxfEndpoint ep = new CxfEndpoint();
    	ep.setCamelContext(ctx);
    	ep.setAddress("https://ws-single-transactions-int-bp.nmvs.eu:8443/WS_SINGLE_TRANSACTIONS_V1/SinglePackServiceV20");
    	ep.setServiceClass(org.davisr.spring.camel.nmvs.ISinglePackServices.class);
    	ep.setWsdlURL("WS_SINGLE_PACK.wsdl");
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
