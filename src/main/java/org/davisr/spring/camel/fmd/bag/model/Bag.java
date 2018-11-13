package org.davisr.spring.camel.fmd.bag.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name="BAG")
public class Bag {
	@Id @GeneratedValue @Column(name="ID")
	private Integer id;

	@Column (name="LABEL_CODE")
	private String labelCode;

	@OneToMany(mappedBy = "bag", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Pack> packs;
}
