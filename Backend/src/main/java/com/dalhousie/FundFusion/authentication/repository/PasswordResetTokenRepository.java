package com.dalhousie.FundFusion.authentication.repository;

import com.dalhousie.FundFusion.authentication.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordReset,Integer> {
    Optional<PasswordReset> findByUserId(Integer userId);
}
