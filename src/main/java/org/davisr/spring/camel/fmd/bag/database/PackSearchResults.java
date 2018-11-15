package org.davisr.spring.camel.fmd.bag.database;

import java.util.List;

import org.davisr.spring.camel.fmd.bag.model.Pack;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackSearchResults {
	private List<Pack> packs;
}
