package com.cpt.payments.stripe;

import lombok.Data;

@Data
public class StripeCoreResponse {

	private String id;
	private String amount_total;
	private String mode;
	private String payment_method_collection;
	private String payment_status;
	private String status;
	private String url;
	private ErrorDetails error; 
	
}
