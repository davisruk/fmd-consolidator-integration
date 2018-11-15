package org.davisr.spring.camel.fmd.bag.repo;

import org.davisr.spring.camel.fmd.bag.model.Bag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BagRepository extends JpaRepository<Bag, Integer>{

}
