package com.cpt.payments.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpt.payments.constants.ControllerEndpoints;
import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.exception.PaymentProcessException;
import com.cpt.payments.pojo.StripeProviderRequest;
import com.cpt.payments.pojo.StripeProviderResponse;
import com.cpt.payments.pojo.Transaction;
import com.cpt.payments.service.PaymentService;
import com.cpt.payments.util.LogMessage;

@RestController
@RequestMapping(ControllerEndpoints.PAYMENT_BASE_URI)
public class PaymentController {

	private static final Logger LOGGER = LogManager.getLogger(PaymentController.class);

	@Autowired
	PaymentService paymentService;
	
	@PostMapping(ControllerEndpoints.PROCESS_PAYMENT)
	public ResponseEntity<StripeProviderResponse> initiatePayment(@RequestBody StripeProviderRequest stripeProviderRequest) {
		LogMessage.setLogMessagePrefix(ControllerEndpoints.PROCESS_PAYMENT);

		LogMessage.log(LOGGER, " processing stripe payment with request ::: " + stripeProviderRequest);

		// TODO this is temporary for negative testing.
		if (stripeProviderRequest.getProductDescription().equals("error")) {
			LogMessage.log(LOGGER, " Raising temporary error -> ");
			throw new PaymentProcessException(HttpStatus.BAD_REQUEST,
					ErrorCodeEnum.FAILED_TO_CONNECT_TO_STRIPE.getErrorCode(),
					ErrorCodeEnum.FAILED_TO_CONNECT_TO_STRIPE.getErrorMessage());
		}
		
		StripeProviderResponse response = paymentService.initiatePayment(stripeProviderRequest);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(ControllerEndpoints.PAYMENT_DETAILS)
	public ResponseEntity<Transaction> paymentDetails(@RequestBody Transaction transaction) {
		LogMessage.setLogMessagePrefix(ControllerEndpoints.PAYMENT_DETAILS);

		LogMessage.log(LOGGER, " processing stripe get payment with request ::: " + transaction);
		return new ResponseEntity<>(paymentService.paymentDetails(transaction), HttpStatus.OK);
	}
	
	@PostMapping(ControllerEndpoints.PAYMENT_EXPIRE)
	public ResponseEntity<Transaction> paymentExpire(@RequestBody Transaction transaction) {
		LogMessage.setLogMessagePrefix(ControllerEndpoints.PAYMENT_DETAILS);

		LogMessage.log(LOGGER, " processing stripe get payment with request ::: " + transaction);
		return new ResponseEntity<>(paymentService.paymentExpire(transaction), HttpStatus.OK);
	}

}
