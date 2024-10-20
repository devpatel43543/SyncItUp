package com.dalhousie.FundFusion.user.requestEntity;
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


}
