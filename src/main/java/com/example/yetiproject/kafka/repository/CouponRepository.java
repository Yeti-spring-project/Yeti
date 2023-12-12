package com.example.yetiproject.kafka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.yetiproject.kafka.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
