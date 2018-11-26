package org.davisr.spring.camel.fmd.nmvs.response;

import org.davisr.spring.camel.nmvs.G110Response;
import org.springframework.stereotype.Component;

@Component("singlePackResponseBuilder")
public class SinglePackResponseBuilder {

	public FMDResponse buildG110Response (G110Response response) {
		return FMDResponse.builder()
		.code(response.getBody().getReturnCode().getCode().name())
		.description(response.getBody().getReturnCode().getDesc())
		.packState(response.getBody().getPack().getState().name())
		.build();
	}
}
