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
		return bagRepo.save(bag);
	}
}
