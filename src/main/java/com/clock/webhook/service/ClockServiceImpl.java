package com.clock.webhook.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.clock.webhook.model.Webhook;
import com.clock.webhook.processor.WebhookProcessor;

@Service
public class ClockServiceImpl implements ClockService {

	private Map<String, Map<Webhook, Future>> webhooks;

	private ExecutorService executorService;

	@Autowired
	private WebhookProcessor webhookProcessor;

	@Autowired
	public ClockServiceImpl() {
	}

	@PostConstruct
	public void init() {
		executorService = Executors.newSingleThreadExecutor();
	}

	/**
	 * See {@inheritDoc}
	 */
	@Override
	public ResponseEntity<String> register(Webhook webhook) {

		if (this.getWebhooks().containsKey(webhook.getUrl())) {

			return new ResponseEntity<>("Url is already registered.", HttpStatus.CONFLICT);

		} else {

			try {
				webhookProcessor.accept(webhook);
				Future<ScheduledFuture> future = executorService.submit(webhookProcessor);

				ScheduledFuture scheduleFuture = future.get();

				Map<Webhook, Future> map = new HashMap<>();
				map.put(webhook, scheduleFuture);
				this.getWebhooks().put(webhook.getUrl(), map);

			} catch (InterruptedException | ExecutionException e) {
				// TODO Exception handling
				e.printStackTrace();
			}

			return new ResponseEntity<>("Url successfully registered", HttpStatus.OK);
		}

	}

	/**
	 * See {@inheritDoc}
	 */
	@Override
	public ResponseEntity<String> unregister(String url) {

		if (!this.getWebhooks().containsKey(url)) {

			return new ResponseEntity<>("Url not found", HttpStatus.NOT_FOUND);

		} else {

			Optional<Future> future = Optional.ofNullable(
					this.getWebhooks().get(url).entrySet().stream().map(Map.Entry::getValue).findFirst().get());

			future.get().cancel(true);

			this.getWebhooks().remove(url);

			return new ResponseEntity<>("Url successfully unregistered", HttpStatus.OK);

		}

	}

	/**
	 * See {@inheritDoc}
	 */
	@Override
	public ResponseEntity<String> setFrequency(Webhook webhook) {

		if (!this.getWebhooks().containsKey(webhook.getUrl())) {

			return new ResponseEntity<>("Url not found", HttpStatus.NOT_FOUND);

		} else {

			Optional<Future> oldFuture = Optional.ofNullable(this.getWebhooks().get(webhook.getUrl()).entrySet()
					.stream().map(Map.Entry::getValue).findFirst().get());

			oldFuture.get().cancel(true);

			this.getWebhooks().remove(webhook.getUrl());

			try {
				webhookProcessor.accept(webhook);
				Future<ScheduledFuture> future = executorService.submit(webhookProcessor);

				ScheduledFuture scheduleFuture = future.get();

				Map<Webhook, Future> map = new HashMap<>();
				map.put(webhook, scheduleFuture);
				this.getWebhooks().put(webhook.getUrl(), map);

			} catch (InterruptedException | ExecutionException e) {
				// TODO Exception handling
				e.printStackTrace();
			}

			return new ResponseEntity<>("Frequency succesfully updated", HttpStatus.OK);

		}

	}

	/**
	 * Get the webhooks key - value map
	 * 
	 * @return A {@link Map} of a {@link Webhook} and a {@link Future}
	 */
	private Map<String, Map<Webhook, Future>> getWebhooks() {

		if (webhooks == null)
			webhooks = new HashMap<>();

		return webhooks;
	}

}
