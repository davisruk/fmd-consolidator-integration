package org.davisr.spring.camel.fmd.bag.database;

import java.util.Optional;

import org.davisr.spring.camel.fmd.bag.model.Bag;
import org.davisr.spring.camel.fmd.bag.repo.BagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("bagDbService")
public class BagDatabaseService {

	@Autowired
	BagRepository bagRepo;
	
	public BagSearchResults getAllBags() {
		return BagSearchResults.builder().bags(bagRepo.findAll()).build();
	}
	
	public Optional<Bag> getBagById(Integer id) {
		return bagRepo.findById(id);
	}
	
	public Bag saveBag (Bag bag) {
		bag.getPacks().forEach(pack -> pack.setBag(bag));
		Bag repoBag = bagRepo.findByLabelCode(bag.getLabelCode());
		// this is wrong!!! if bag exists we need to compare it
		// with the repo version and add / remove packs
		// based on the bag in the request
		// easier to leave this up to end system and use update / create
		// mechanism instead - create should return error if label exists.
		if (repoBag != null) {
			return repoBag;
		}
		return bagRepo.save(bag);
	}
}
