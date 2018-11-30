package org.davisr.spring.camel.fmd.bag.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name="BAG")
@JsonInclude(Include.NON_NULL)
public class Bag {
	@Id @GeneratedValue @Column(name="ID")
	private Integer id;

	@Column (name="LABEL_CODE")
	private String labelCode;
	
	// Prevent infinite recursion for JSON serialization
	// NOTE - for 1 side of 1:M you have to specify the class name of the property in the child, not the property name!!
	@JsonIgnoreProperties("Bag")
	@OneToMany( fetch=FetchType.EAGER, mappedBy = "bag", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Pack> packs;
}
