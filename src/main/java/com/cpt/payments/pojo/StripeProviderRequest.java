package com.cpt.payments.pojo;

import lombok.Data;

@Data
public class StripeProviderRequest {
	private String transactionReference;
	private String currency;
	private double amount;
	private Long quantity;
	private String productDescription;
	private String successUrl;
	private String cancelUrl;

}
