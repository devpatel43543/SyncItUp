package com.dalhousie.FundFusion.util;

public class EmailTemplates {
    public static final String OTP_EMAIL_TEMPLATE = """
        <p>Hello,</p>
        <p>Your OTP for email verification is:</p>
        <h2>%s</h2>
        <p>This OTP is valid for 5 minutes.</p>
    """;

    public static final String FORGOT_PASSWORD_EMAIL_TEMPLATE = """
        <p>Hello,</p>
        <p>You have requested to reset your password. Please click the link below to create a new password. 
        This link is valid for only 5 minutes for your security.</p>
        <p>If you did not request this change, please ignore this email.</p>
        <p>Click the link below to reset your password:</p>
        <p><a href="%s">Reset My Password</a></p>
        <p>For your safety, please do not share this link with anyone.</p>
        <p>Thank you!</p>
    """;
}
