package com.cpt.payments.service.formatter.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.cpt.payments.http.HttpRequest;
import com.cpt.payments.pojo.StripeProviderRequest;
import com.cpt.payments.util.LogMessage;
import com.google.gson.Gson;

@Service
@Qualifier("InitiatePaymentRequestHandler")
public class InitiatePaymentRequestHandler {
	private static final Logger LOGGER = LogManager.getLogger(InitiatePaymentRequestHandler.class);

	@Value("${stripe.initiate.payment.url}")
	private String initiatePaymentUrl;

	@Value("${stripe.token}")
	private String token;

	@Autowired
	private Gson gson;
	
	private final String MODE_PAYMENT = "payment";

	public HttpRequest prepareRequest(String request) {
		StripeProviderRequest stripeProviderRequest = gson.fromJson(request, StripeProviderRequest.class);

		Map<String, String> initiatePaymentRequestPayload = prepareRequestFormPayload(stripeProviderRequest);
		LogMessage.log(LOGGER, " preparing initiate payment request payload completed with values :: "
				+ initiatePaymentRequestPayload);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBasicAuth(token, "");
		
		HttpRequest httpRequest = HttpRequest.builder()
				.httpMethod(HttpMethod.POST)
				.formRequestPayload(initiatePaymentRequestPayload)
				.headers(httpHeaders)
				.url(initiatePaymentUrl)
				.build();
		
		LogMessage.log(LOGGER, " preparing initiate payment request completed.");
		return httpRequest;
	}

	private Map<String, String> prepareRequestFormPayload(StripeProviderRequest stripeProviderRequest) {
		Map<String, String> initiatePaymentRequestPayload = new HashMap<>();

		initiatePaymentRequestPayload.put("line_items[0][price_data][currency]", 
				stripeProviderRequest.getCurrency());
		initiatePaymentRequestPayload.put("line_items[0][price_data][product_data][name]",
				stripeProviderRequest.getProductDescription());
		
		initiatePaymentRequestPayload.put("line_items[0][price_data][unit_amount]",
				String.valueOf(stripeProviderRequest.getAmount() * 100).split("\\.")[0]); //25.0
		
		initiatePaymentRequestPayload.put("line_items[0][quantity]", 
				String.valueOf(stripeProviderRequest.getQuantity()));
		
		initiatePaymentRequestPayload.put("mode", MODE_PAYMENT);
		initiatePaymentRequestPayload.put("currency", 
				stripeProviderRequest.getCurrency());
		
		initiatePaymentRequestPayload.put("success_url", stripeProviderRequest.getSuccessUrl());
		initiatePaymentRequestPayload.put("cancel_url", stripeProviderRequest.getCancelUrl());

		LogMessage.log(LOGGER, "STRIPE form data is :: " + initiatePaymentRequestPayload);
		return initiatePaymentRequestPayload;
	}




}
