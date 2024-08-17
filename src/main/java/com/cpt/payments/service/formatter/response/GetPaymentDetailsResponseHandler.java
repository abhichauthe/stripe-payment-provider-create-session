package com.cpt.payments.service.formatter.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.cpt.payments.constants.TransactionStatusEnum;
import com.cpt.payments.pojo.Transaction;
import com.cpt.payments.stripe.StripeGetPaymentsDetails;
import com.cpt.payments.util.LogMessage;
import com.google.gson.Gson;

@Component("GetPaymentDetailsResponseHandler")
public class GetPaymentDetailsResponseHandler {
	private static final Logger LOGGER = LogManager.getLogger(GetPaymentDetailsResponseHandler.class);

	@Autowired
	private Gson gson;

	public Transaction processResponse(ResponseEntity<String> response, Transaction transaction) {
		LogMessage.log(LOGGER,
				"status received from stripe while get payment details :: " + response.getStatusCode().value());
		if (null == response.getBody() || 200 != response.getStatusCode().value()) {
			LogMessage.log(LOGGER, " failed to retrieve payment at stripe provider -> " + response);
			return transaction;

		}

		StripeGetPaymentsDetails providerResponse = gson.fromJson(response.getBody(), StripeGetPaymentsDetails.class);

		LogMessage.log(LOGGER, "providerResponse:" + providerResponse);

		if ("complete".equalsIgnoreCase(providerResponse.getStatus())
				&& "paid".equalsIgnoreCase(providerResponse.getPayment_status())) {
			LogMessage.log(LOGGER, ":: stripe payment is success :: ");
			transaction.setTxnStatusId(TransactionStatusEnum.APPROVED.getId());
		}
		
		/*
		 * if (transaction.getRetryCount() >= 3 &&
		 * "open".equalsIgnoreCase(providerResponse.getStatus())) {
		 * LogMessage.log(LOGGER, ":: retry limit reached :: ");
		 * serviceHelper.expireStripeSession(transaction); }
		 */

		LogMessage.log(LOGGER, "response received from stripe while get payment details :: " + providerResponse);
		return transaction;
	}
}
