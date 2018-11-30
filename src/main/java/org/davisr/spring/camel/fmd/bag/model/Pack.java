package org.davisr.spring.camel.fmd.bag.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name="PACK")
@JsonInclude(Include.NON_NULL)
public class Pack {
	@Id @GeneratedValue @Column(name="ID")	
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "BAG_ID")
	// Prevent infinite recursion for JSON serialization
	// For many side of reference you have to use the parent's property name
	@JsonIgnoreProperties("packs")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Bag bag;

	@Column(name = "GTIN")
	private String gtin;

	@Column(name = "BATCH")
	private String batch;
	
	@Column(name = "SERIAL_NUMBER")
	private String serialNumber;
	
	@Column(name = "EXPIRY_DATE")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")  
	private Date expiry;
	
	@Column(name = "DECOMMISSIONED")
	private boolean decommissioned;
	
	private String packState;
}
