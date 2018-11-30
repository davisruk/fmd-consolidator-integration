package org.davisr.spring.camel.fmd.nmvs.request;

import org.davisr.spring.camel.fmd.bag.model.Bag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FMDRequest {
	private String operation;
	private Bag bag;
}
