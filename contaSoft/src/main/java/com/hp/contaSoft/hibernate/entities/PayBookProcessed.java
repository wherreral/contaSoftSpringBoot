package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pay_book_processed")
public class PayBookProcessed extends Base {

	@Column
	private Long originalPayBookInstanceId;

	@Column
	private Long taxpayerId;

	@Column
	private String taxpayerRut;

	@Column
	private String taxpayerName;

	@Column
	private int version;

	@Column
	private String rut;

	@Column
	private String month;

	@Column(name = "period_year")
	private String year;

	@Column
	private String details;

	@Column
	private String fileName;

	@Column
	private String status;

	@Column
	private String familyId;

	public PayBookProcessed() {
	}
}
