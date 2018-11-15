package org.davisr.spring.camel.fmd.bag.database;

import java.util.List;

import org.davisr.spring.camel.fmd.bag.model.Bag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BagSearchResults {
	List<Bag> bags;
}
