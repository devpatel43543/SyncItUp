package com.dalhousie.FundFusion.user.repository;

import com.dalhousie.FundFusion.user.entity.PasswordReset;
import com.dalhousie.FundFusion.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordReset,Integer> {
    Optional<PasswordReset> findByUserId(Integer userId);
}
