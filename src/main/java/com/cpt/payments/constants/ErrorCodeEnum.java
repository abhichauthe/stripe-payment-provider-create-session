package com.cpt.payments.constants;

import lombok.Getter;

public enum ErrorCodeEnum {
	GENERIC_EXCEPTION("50001","Something went wrong, please try later"), 
	FAILED_TO_CONNECT_TO_STRIPE("50002","Failed to connect to stripe provider"),
	FAILED_TO_INITIATE_PAYMENT_AT_STRIPE("50003","Failed to initiate payment at Stripe");
	
	@Getter
	private String errorCode;
	@Getter
	private String errorMessage;
	
	private ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode=errorCode;
		this.errorMessage=errorMessage;
	}
	
}
