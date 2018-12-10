package org.davisr.spring.camel;

import java.util.List;

import org.davisr.spring.camel.fmd.keystore.KeyStoreInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component("storeProps")
@EnableConfigurationProperties
@ConfigurationProperties(prefix="stores")
@Data
public class StoreProperties {
	private List<KeyStoreInfo> keyStoreInfo;
}
