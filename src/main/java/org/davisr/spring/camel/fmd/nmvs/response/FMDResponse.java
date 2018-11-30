package org.davisr.spring.camel.fmd.nmvs.response;

import java.util.List;

import org.davisr.spring.camel.fmd.bag.model.Bag;
import org.davisr.spring.camel.fmd.bag.model.Pack;

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
	private List<String> reasons;
	private Pack pack;
}
