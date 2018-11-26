package org.davisr.spring.camel.fmd.nmvs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FMDResponse {
	private String code;
	private String description;
	private String packState;
}
