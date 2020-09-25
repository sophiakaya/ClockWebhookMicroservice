package com.clock.webhook.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.clock.webhook.model.Webhook;
import com.clock.webhook.processor.WebhookProcessor;

@Service
public class ClockServiceImpl implements ClockService {

	private static final BlockingQueue<Webhook> WEBHOOK_QUEUE = new LinkedBlockingQueue<>();

	private Map<Webhook, Future> webhooks;

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

		if (this.getWebhooks().entrySet().stream().filter(x -> x.getKey().getUrl().equals(webhook.getUrl()))
				.map(Map.Entry::getKey).findFirst().orElse(null) != null) {

			return new ResponseEntity<>("The given url is already registered.", HttpStatus.CONFLICT);

		} else {

			WEBHOOK_QUEUE.add(webhook);

			executorService.submit(webhookProcessor);

			return new ResponseEntity<>("Url successfully registered", HttpStatus.OK);
		}

	}

	/**
	 * See {@inheritDoc}
	 */
	@Override
	public ResponseEntity<String> unregister(String url) {

		if (this.getWebhooks().entrySet().stream().filter(x -> x.getKey().getUrl().equals(url)).map(Map.Entry::getKey)
				.findFirst().orElse(null) == null) {

			return new ResponseEntity<>("Url not found", HttpStatus.NOT_FOUND);

		} else {

			Optional<Future> myFuture = Optional.ofNullable(this.getWebhooks().entrySet().stream()
					.filter(x -> x.getKey().getUrl().equals(url)).map(Map.Entry::getValue).findFirst().get());

			myFuture.get().cancel(true);

			this.getWebhooks().remove(this.getWebhooks().entrySet().stream()
					.filter(x -> x.getKey().getUrl().equals(url)).map(Map.Entry::getKey).findFirst().get());

			return new ResponseEntity<>("Url successfully unregistered", HttpStatus.OK);

		}

	}

	/**
	 * See {@inheritDoc}
	 */
	@Override
	public ResponseEntity<String> setFrequency(Webhook webhook) {

		if (this.getWebhooks().entrySet().stream().filter(x -> x.getKey().getUrl().equals(webhook.getUrl()))
				.map(Map.Entry::getKey).findFirst().orElse(null) == null) {

			return new ResponseEntity<>("Url not found", HttpStatus.NOT_FOUND);

		} else {

			Optional<Future> myFuture = Optional.ofNullable(
					this.getWebhooks().entrySet().stream().filter(x -> x.getKey().getUrl().equals(webhook.getUrl()))
							.map(Map.Entry::getValue).findFirst().get());

			myFuture.get().cancel(true);

			this.getWebhooks()
					.remove(this.getWebhooks().entrySet().stream()
							.filter(x -> x.getKey().getUrl().equals(webhook.getUrl())).map(Map.Entry::getKey)
							.findFirst().get());

			WEBHOOK_QUEUE.add(webhook);
			executorService.submit(webhookProcessor);

			return new ResponseEntity<>("Frequency succesfully updated", HttpStatus.OK);

		}

	}

	/**
	 * See {@inheritDoc}
	 */
	@Override
	public Map<Webhook, Future> getWebhooks() {

		if (webhooks == null)
			webhooks = new HashMap<>();

		return webhooks;
	}

	/**
	 * See {@inheritDoc}
	 */
	@Override
	public Queue<Webhook> getQueue() {
		return WEBHOOK_QUEUE;
	}

}
