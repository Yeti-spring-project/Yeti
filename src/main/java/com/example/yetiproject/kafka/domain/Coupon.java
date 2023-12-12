package com.example.yetiproject.kafka.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;

	public Coupon(Long userId) {
		this.userId = userId;
	}

	public Coupon() {

	}

	public Long getId() {
		return id;
	}
}
