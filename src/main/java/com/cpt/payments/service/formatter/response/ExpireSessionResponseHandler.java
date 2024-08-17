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

@Component("ExpireSessionResponseHandler")
public class ExpireSessionResponseHandler {
	private static final Logger LOGGER = LogManager.getLogger(ExpireSessionResponseHandler.class);

	@Autowired
	private Gson gson;

	public Transaction processResponse(ResponseEntity<String> response, Transaction transaction) {
		LogMessage.log(LOGGER,
				"status received from stripe for expire session is :: " + response.getStatusCode().value());
		if (null == response.getBody() || 200 != response.getStatusCode().value()) {
			LogMessage.log(LOGGER, " failed to retrieve payment at stripe provider -> " + response);
			return transaction;

		}

		StripeGetPaymentsDetails providerResponse = gson.fromJson(response.getBody(), StripeGetPaymentsDetails.class);

		LogMessage.log(LOGGER, "providerResponse:" + providerResponse);

		if ("expired".equalsIgnoreCase(providerResponse.getStatus())
				&& "unpaid".equalsIgnoreCase(providerResponse.getPayment_status())) {
			LogMessage.log(LOGGER, ":: stripe payment is success :: ");
			transaction.setTxnStatusId(TransactionStatusEnum.FAILED.getId());
			transaction.setProviderCode("EXPIRED");
			transaction.setProviderMessage("Failed by system, after 3 times retry");
		}

		LogMessage.log(LOGGER, "response received from stripe while get payment details :: " + providerResponse);
		return transaction;
	}
}
