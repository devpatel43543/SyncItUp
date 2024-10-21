    package com.dalhousie.FundFusion.authentication.service;

    import com.dalhousie.FundFusion.exception.TokenExpiredException;
    import com.dalhousie.FundFusion.authentication.entity.Otp;
    import com.dalhousie.FundFusion.authentication.repository.OtpRepository;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.time.Instant;
    import java.util.Optional;
    import java.util.Random;

    @Service
    @RequiredArgsConstructor
    public class OtpServiceImpl implements OtpService {
        private final OtpRepository otpRepository;
        private final long EXPIRATION_DURATION = 1000L * 60 * 10;
        @Transactional
        @Override
        public Otp generateOtp(Integer userId) {
            Optional<Otp> existingOtp = otpRepository.findByUserId(userId);
            existingOtp.ifPresent(this::deleteOtp);

            String otpValue = String.valueOf(100000 + new Random().nextInt(900000));
            Otp otp = Otp.builder()
                    .otp(otpValue)
                    .expiryDate(Instant.now().plusMillis(EXPIRATION_DURATION))
                    .userId(userId)
                    .build();

            return otpRepository.save(otp);
        }
        public Otp resendOtp(Integer userId) {
            Optional<Otp> existingOtp = otpRepository.findByUserId(userId);
            existingOtp.ifPresent(this::deleteOtp);
            return generateOtp(userId);
        }

        @Transactional
        @Override
        public void deleteOtp(Otp otp) {
            if (otp != null) {
                otpRepository.deleteByUserId(otp.getUserId());
            }
        }


        @Override
        public Optional<Otp> findByOtp(String otpValue) {
            return otpRepository.findByOtp(otpValue);
        }

        public boolean isOtpValid(Otp otp) {
            if (otp == null || otp.getExpiryDate() == null) {
                return false;
            }
            if (otp.getExpiryDate().isBefore(Instant.now())) {
                throw new TokenExpiredException("otp has expired.");
            }

            return true;
        }
    }
