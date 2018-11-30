package org.davisr.spring.camel.fmd.nmvs.response;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FMDBagResponse {
	private Integer bagId;
	private String bagLabel;
	private ArrayList<FMDResponse> packResponses;
}
