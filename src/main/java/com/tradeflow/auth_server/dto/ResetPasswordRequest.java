package com.tradeflow.auth_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
	@NotBlank(message = "New password is required")
	@Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
	private String newPassword;

	public ResetPasswordRequest() {}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
