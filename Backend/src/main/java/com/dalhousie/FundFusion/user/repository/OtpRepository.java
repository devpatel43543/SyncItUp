package com.dalhousie.FundFusion.user.repository;

import com.dalhousie.FundFusion.user.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Integer> {
    Optional<Otp> findByOtp(String otp);
    Optional<Otp> findByUserId(Integer userId);  // Added this to find OTP by userId
    void deleteByUserId(Integer userId);

}
