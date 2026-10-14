package com.api.auth_service.service;

import com.api.auth_service.dto.*;

public interface OtpService {
    void sendRegistrationOtp(String email);

    void verifyRegistrationOtp(VerifyOtpRequest request);

    void sendOtp(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
