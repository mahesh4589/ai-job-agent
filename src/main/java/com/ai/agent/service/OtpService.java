package com.ai.agent.service;

import com.ai.agent.dto.ForgotPasswordRequest;
import com.ai.agent.dto.ResetPasswordRequest;
import com.ai.agent.dto.VerifyOtpRequest;

public interface OtpService {

    void sendOtp(ForgotPasswordRequest email);

    void verifyOtp(VerifyOtpRequest request);

    void resetPassword(ResetPasswordRequest request);
}