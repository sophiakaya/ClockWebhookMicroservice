package com.clock.webhook.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

	private Map<String, Map<Webhook, ScheduledFuture>> webhooks;

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

			webhookProcessor.accept(webhook);
			executorService.submit(webhookProcessor);

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

			Optional<ScheduledFuture> future = Optional.ofNullable(
					this.getWebhooks().get(url).entrySet().stream().map(Map.Entry::getValue).findFirst().get());

			if (future.isPresent())
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

			Optional<ScheduledFuture> oldFuture = Optional.ofNullable(this.getWebhooks().get(webhook.getUrl())
					.entrySet().stream().map(Map.Entry::getValue).findFirst().get());

			if (oldFuture.isPresent())
				oldFuture.get().cancel(true);

			this.getWebhooks().remove(webhook.getUrl());

			webhookProcessor.accept(webhook);
			executorService.submit(webhookProcessor);

			return new ResponseEntity<>("Frequency succesfully updated", HttpStatus.OK);

		}

	}

	/**
	 * See {@inheritDoc}
	 */
	@Override
	public void addScheduledFuture(Webhook webhook, ScheduledFuture scheduleFuture) {

		Map<Webhook, ScheduledFuture> map = new HashMap<>();
		map.put(webhook, scheduleFuture);
		this.getWebhooks().put(webhook.getUrl(), map);

	}

	/**
	 * Get the list of webhooks with their future executor
	 * 
	 * @return A {@link Map} of a {@link Webhook} and a {@link Future}
	 */
	private Map<String, Map<Webhook, ScheduledFuture>> getWebhooks() {

		if (webhooks == null)
			webhooks = new HashMap<>();

		return webhooks;
	}

}
