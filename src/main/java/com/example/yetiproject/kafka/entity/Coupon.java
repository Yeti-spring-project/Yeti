package com.example.yetiproject.kafka.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long memberId;

	public Coupon(Long memberId) {
		this.memberId = memberId;
	}

	public Coupon() {

	}

	public Long getId() {
		return id;
	}
}
