package org.davisr.spring.camel.fmd.bag.database;

import org.davisr.spring.camel.fmd.bag.model.Pack;
import org.davisr.spring.camel.fmd.bag.repo.PackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("packDbService")
public class PackDatabaseService {
	@Autowired
	PackRepository packRepo;
	
	public Pack getPackById(Integer id) {
		return packRepo.findById(id).orElse(null);
	}
	
	public PackSearchResults getAllPacks() {
		return PackSearchResults.builder().packs(packRepo.findAll()).build();
	}
}
