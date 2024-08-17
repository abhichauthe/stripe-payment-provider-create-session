package com.cpt.payments.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.exception.PaymentProcessException;
import com.cpt.payments.http.HttpRequest;
import com.cpt.payments.http.HttpRestTemplateEngine;
import com.cpt.payments.pojo.StripeProviderRequest;
import com.cpt.payments.pojo.StripeProviderResponse;
import com.cpt.payments.pojo.Transaction;
import com.cpt.payments.service.PaymentService;
import com.cpt.payments.service.formatter.request.ExpireSessionRequestHandler;
import com.cpt.payments.service.formatter.request.GetPaymentDetailsRequestHandler;
import com.cpt.payments.service.formatter.request.InitiatePaymentRequestHandler;
import com.cpt.payments.service.formatter.response.ExpireSessionResponseHandler;
import com.cpt.payments.service.formatter.response.GetPaymentDetailsResponseHandler;
import com.cpt.payments.service.formatter.response.InitiatePaymentResponseHandler;
import com.cpt.payments.util.LogMessage;
import com.google.gson.Gson;

@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

	@Autowired
	private InitiatePaymentRequestHandler initiatePaymentRequestHandler;

	@Autowired
	private HttpRestTemplateEngine httpRestTemplateEngine;
	
	@Autowired
	private InitiatePaymentResponseHandler initiatePaymentResponseHandler;


	@Autowired
	private GetPaymentDetailsRequestHandler getPaymentDetailsRequestHandler;

	@Autowired
	private GetPaymentDetailsResponseHandler getPaymentDetailsResponseHandler;
	
	
	@Autowired
	private ExpireSessionRequestHandler expireSessionRequestHandler;
	
	@Autowired
	private ExpireSessionResponseHandler expireSessionResponseHandler;
	
	
	@Override
	public StripeProviderResponse initiatePayment(StripeProviderRequest stripeProviderRequest) {

		LogMessage.log(LOGGER, "invoking initiatePayment with stripeProviderRequest:" + stripeProviderRequest);
		Gson gson = new Gson();
		HttpRequest httpRequest = initiatePaymentRequestHandler.prepareRequest(gson.toJson(stripeProviderRequest));

		ResponseEntity<String> response = httpRestTemplateEngine.execute(httpRequest);

		LogMessage.log(LOGGER, "got API response from Stripe||response:" + response);

		if (null == response) {
			LogMessage.log(LOGGER, " failed to connect to trustly provider -> " + response);
			throw new PaymentProcessException(HttpStatus.BAD_REQUEST,
					ErrorCodeEnum.FAILED_TO_CONNECT_TO_STRIPE.getErrorCode(),
					ErrorCodeEnum.FAILED_TO_CONNECT_TO_STRIPE.getErrorMessage());
		}

		return initiatePaymentResponseHandler.processResponse(response);
	}


	@Override
	public Transaction paymentDetails(Transaction transaction) {
		HttpRequest httpRequest = getPaymentDetailsRequestHandler.prepareRequest(transaction);
		ResponseEntity<String> response = httpRestTemplateEngine.execute(httpRequest);
		if (null == response) {
			LogMessage.log(LOGGER, " failed to connect to trustly provider -> " + response);
			throw new PaymentProcessException(HttpStatus.BAD_REQUEST,
					ErrorCodeEnum.FAILED_TO_CONNECT_TO_STRIPE.getErrorCode(),
					ErrorCodeEnum.FAILED_TO_CONNECT_TO_STRIPE.getErrorMessage());
		}

		return getPaymentDetailsResponseHandler.processResponse(response, transaction);
	}


	@Override
	public Transaction paymentExpire(Transaction transaction) {
		HttpRequest httpRequest = expireSessionRequestHandler.prepareRequest(transaction);
		ResponseEntity<String> response = httpRestTemplateEngine.execute(httpRequest);
		if (null == response) {
			LogMessage.log(LOGGER, " failed to connect to trustly provider -> " + response);
			throw new PaymentProcessException(HttpStatus.BAD_REQUEST,
					ErrorCodeEnum.FAILED_TO_CONNECT_TO_STRIPE.getErrorCode(),
					ErrorCodeEnum.FAILED_TO_CONNECT_TO_STRIPE.getErrorMessage());
		}

		return expireSessionResponseHandler.processResponse(response, transaction);
	}
	
}
