package com.cpt.payments.stripe;

import java.util.List;

import lombok.Data;

@Data
public class StripeGetPaymentsDetails {
	private String id;
	private String object;
	private Integer amount_subtotal;
	private Integer amount_total;
	private String currency;
	private Boolean livemode;
	private String mode;
	private String payment_method_collection;
	private List<String> payment_method_types;
	private String payment_status;
	private String status;
	private String success_url;
}
