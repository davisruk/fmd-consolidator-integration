package org.davisr.spring.camel.fmd.keystore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeyStoreInfo {
	private Integer id;
	private String keystorePassword;
	private String keystoreName;
	private String keyStoreType;
	
}
