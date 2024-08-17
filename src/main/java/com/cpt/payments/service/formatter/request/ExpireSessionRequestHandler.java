package com.cpt.payments.service.formatter.request;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.cpt.payments.http.HttpRequest;
import com.cpt.payments.pojo.Transaction;
import com.cpt.payments.util.LogMessage;

@Component("ExpireSessionRequestHandler")
public class ExpireSessionRequestHandler {
	private static final Logger LOGGER = LogManager.getLogger(ExpireSessionRequestHandler.class);

	@Value("${stripe.initiate.payment.url}")
	private String initiatePaymentUrl;

	@Value("${stripe.token}")
	private String token;

	public HttpRequest prepareRequest(Transaction transaction) {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBasicAuth(token, "");
		
		HttpRequest httpRequest = HttpRequest.builder()
				.httpMethod(HttpMethod.POST)
				.headers(httpHeaders)
				.url(initiatePaymentUrl + "/" + transaction.getProviderReference()+"/expire")
				.build();
		
		LogMessage.log(LOGGER, " preparing expire payment request completed | url:" + httpRequest.getUrl());
		return httpRequest;
	}
}
