package com.dalhousie.FundFusion.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailTemplatesTest {

    @Test
    void testOtpEmailTemplate_NotNull() {
        // Verify that the OTP email template is not null
        assertNotNull(EmailTemplates.getOtpEmailTemplate());

        // Replace placeholder and validate the content
        String otp = "123456";
        String formattedOtpTemplate = String.format(EmailTemplates.getOtpEmailTemplate(), otp);
        assertNotNull(formattedOtpTemplate);
        assertEquals("""
                <p>Hello,</p>
                <p>Your OTP for email verification is:</p>
                <h2>123456</h2>
                <p>This OTP is valid for 5 minutes.</p>
            """.stripIndent(), formattedOtpTemplate.stripIndent());
    }

    @Test
    void testForgotPasswordEmailTemplate_NotNull() {
        // Verify that the Forgot Password email template is not null
        assertNotNull(EmailTemplates.getForgotPasswordEmailTemplate());

        // Replace placeholder and validate the content
        String resetLink = "https://example.com/reset";
        String formattedForgotPasswordTemplate = String.format(EmailTemplates.getForgotPasswordEmailTemplate(), resetLink);
        assertNotNull(formattedForgotPasswordTemplate);
        assertEquals("""
                <p>Hello,</p>
                <p>You have requested to reset your password. Please click the link below to create a new password. 
                This link is valid for only 5 minutes for your security.</p>
                <p>If you did not request this change, please ignore this email.</p>
                <p>Click the link below to reset your password:</p>
                <p><a href="https://example.com/reset">Reset My Password</a></p>
                <p>For your safety, please do not share this link with anyone.</p>
                <p>Thank you!</p>
            """.stripIndent(), formattedForgotPasswordTemplate.stripIndent());
    }
}
