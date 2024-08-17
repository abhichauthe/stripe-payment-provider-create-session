package com.cpt.payments.service.formatter.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.exception.PaymentProcessException;
import com.cpt.payments.pojo.StripeProviderResponse;
import com.cpt.payments.stripe.StripeCoreResponse;
import com.cpt.payments.util.LogMessage;
import com.google.gson.Gson;

@Service
@Qualifier("InitiatePaymentResponseHandler")
public class InitiatePaymentResponseHandler {
	private static final Logger LOGGER = LogManager.getLogger(InitiatePaymentResponseHandler.class);

	@Autowired
	private Gson gson;

	public StripeProviderResponse processResponse(ResponseEntity<String> response) {
		LogMessage.log(LOGGER,
				"status received from trustly while initiating payment :: " + response.getStatusCode().value());
		if (null == response.getBody()) {
			LogMessage.log(LOGGER, " failed to initiate payment at trustly provider -> " + response);
			throw new PaymentProcessException(HttpStatus.BAD_REQUEST,
					ErrorCodeEnum.FAILED_TO_INITIATE_PAYMENT_AT_STRIPE.getErrorCode(),
					ErrorCodeEnum.FAILED_TO_INITIATE_PAYMENT_AT_STRIPE.getErrorMessage());
		}

		if (HttpStatus.OK.value() != response.getStatusCode().value()) {// failure
			handleNon200Response(response);// it throws exception, and furthre processing will not happen.
		}

		// Successful response.
		StripeCoreResponse providerResponse = gson.fromJson(response.getBody(), StripeCoreResponse.class);

		LogMessage.log(LOGGER, "providerResponse:" + providerResponse);

		StripeProviderResponse stripeProviderResponse = StripeProviderResponse.builder()
				.paymentId(providerResponse.getId())
				.redirectUrl(providerResponse.getUrl())
				.build();
		
		LogMessage.log(LOGGER, "response received from stripe while initiating payment :: " + providerResponse);
		return stripeProviderResponse;
	}

	private void handleNon200Response(ResponseEntity<String> response) {
		StripeCoreResponse providerResponse = gson.fromJson(response.getBody(), StripeCoreResponse.class);
		if (providerResponse.getError() != null) {// Received Trustly ErrorResponse
			LogMessage.log(LOGGER, " failed to initiate payment at stripe provider -> " + response);

			// Return Stripe error to Payment Processing service.
			LogMessage.log(LOGGER, " Throwing error with Stripe Error Response");
			
			String errorCode = providerResponse.getError().getType();// type_c:code_dc:decline_code
			throw new PaymentProcessException(
					HttpStatus.BAD_REQUEST, 
					errorCode,
					providerResponse.getError().getMessage(), 
					true);
		} else {
			LogMessage.log(LOGGER, " HTTP Status Code !200, & not TRUSTLY Valid Response||response:" + response);
			throw new PaymentProcessException(HttpStatus.BAD_REQUEST,
					ErrorCodeEnum.FAILED_TO_INITIATE_PAYMENT_AT_STRIPE.getErrorCode(),
					ErrorCodeEnum.FAILED_TO_INITIATE_PAYMENT_AT_STRIPE.getErrorMessage());
		}
	}

}
