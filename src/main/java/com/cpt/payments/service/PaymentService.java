package com.cpt.payments.service;

import com.cpt.payments.pojo.StripeProviderRequest;
import com.cpt.payments.pojo.StripeProviderResponse;
import com.cpt.payments.pojo.Transaction;

public interface PaymentService {
	
	StripeProviderResponse initiatePayment(StripeProviderRequest stripeProviderRequest);
	
	Transaction paymentDetails(Transaction transaction);
	
	Transaction paymentExpire(Transaction transaction);

}
