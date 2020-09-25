package com.clock.webhook.service;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Future;

import org.springframework.http.ResponseEntity;

import com.clock.webhook.model.Webhook;

public interface ClockService {

	/**
	 * Register to a Clock webhook service
	 * 
	 * @param A {@link Webhook}
	 * @return A {@link ResponseEntity}
	 */
	ResponseEntity<String> register(Webhook webhook);

	/**
	 * Unregister from a Clock webhook service
	 * 
	 * @param A {@link String} url
	 * @return A {@link ResponseEntity}
	 */
	ResponseEntity<?> unregister(String url);

	/**
	 * Set the frequency of a Clock webbook
	 * 
	 * @param A {@link Webhook}
	 * @return A {@link ResponseEntity}
	 */
	ResponseEntity<?> setFrequency(Webhook webhook);

	/**
	 * Get the webhooks on the queue
	 * 
	 * @return A {@link Queue} of a {@link Webhook}
	 */
	Queue<Webhook> getQueue();

	/**
	 * 
	 * @return A {@link Map} of a {@link Webhook} and a {@link Future}
	 */
	Map<Webhook, Future> getWebhooks();

}
