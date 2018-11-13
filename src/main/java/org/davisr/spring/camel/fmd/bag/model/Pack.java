package org.davisr.spring.camel.fmd.bag.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity(name="PACK")
public class Pack {
	@Id @GeneratedValue @Column(name="ID")	
	private Integer id;
	
	@Fetch(FetchMode.JOIN)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BAG_ID")
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
}
