package com.dalhousie.FundFusion.authentication.requestEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVarificationRequest {

    @NotBlank(message = "field is required")
    @NotNull
    private String otp;

    /**
     * Validates the OTP verification request fields.
     *
     * @throws IllegalArgumentException if any field is invalid.
     */
    public void validate() {
        if (isInvalidOtp(otp)) {
            throw new IllegalArgumentException("OTP cannot be empty.");
        }
    }

    private boolean isInvalidOtp(String otp) {
        return otp == null || otp.isBlank();
    }

}
