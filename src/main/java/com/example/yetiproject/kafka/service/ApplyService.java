package com.example.yetiproject.kafka.service;

import javax.swing.plaf.IconUIResource;

import org.springframework.stereotype.Service;

import com.example.yetiproject.kafka.entity.Coupon;
import com.example.yetiproject.kafka.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplyService {
	private final CouponRepository couponRepository;
	public void apply(Long userId){
		long count = couponRepository.count();
		if(count > 100){
			return;
		}
		couponRepository.save(new Coupon(userId));
	}
}
