package com.dalhousie.FundFusion.authentication.repository;

import com.dalhousie.FundFusion.authentication.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Integer> {
    Optional<Otp> findByOtp(String otp);
    Optional<Otp> findByUserId(Integer userId);  // Added this to find OTP by userId
    void deleteByUserId(Integer userId);

}
