package com.example.yetiproject.kafka.service;

import static io.micrometer.common.annotation.AnnotationHandler.*;

import org.springframework.stereotype.Service;

import com.example.yetiproject.kafka.producer.CouponCreateProducer;
import com.example.yetiproject.kafka.repository.AppliedUserRepository;
import com.example.yetiproject.kafka.repository.CouponCountRepository;
import com.example.yetiproject.kafka.repository.CouponRepository;
@Service
public class ApplyService {
	private final CouponRepository couponRepository;

	private final CouponCountRepository couponCountRepository;

	private final CouponCreateProducer couponCreateProducer;

	private final AppliedUserRepository appliedUserRepository;

	public ApplyService(CouponRepository couponRepository, CouponCountRepository couponCountRepository,
		CouponCreateProducer couponCreateProducer, AppliedUserRepository appliedUserRepository) {
		this.couponRepository = couponRepository;
		this.couponCountRepository = couponCountRepository;
		this.couponCreateProducer = couponCreateProducer;
		this.appliedUserRepository = appliedUserRepository;
	}

	public void apply(Long userId){
		// lock start
		// 쿠폰발급 여부
		// if(발급됐다면) return
		//long count = couponRepository.count();
		Long count = couponCountRepository.increment();
		//log.info("쿠폰 개수: {}", count);

		if(count > 100){
			return;
		}
		//couponRepository.save(new Coupon(userId));
		couponCreateProducer.create(userId);
		//lock end
	}

}
