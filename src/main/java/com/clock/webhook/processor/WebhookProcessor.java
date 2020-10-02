package com.clock.webhook.processor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clock.webhook.model.Webhook;
import com.clock.webhook.service.ClockService;
import com.clock.webhook.utils.ClockUtils;

@Service
public class WebhookProcessor implements Consumer<Webhook>, Runnable {

	private static BlockingQueue<Webhook> WEBHOOK_QUEUE = new LinkedBlockingQueue<>();

	@Autowired
	private ClockService clockService;

	private ScheduledExecutorService schedulerExecutor;

	private HttpClient client;

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	@PostConstruct
	public void init() {
		client = HttpClient.newBuilder().build();
		schedulerExecutor = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void accept(Webhook t) {
		WEBHOOK_QUEUE.add(t);
	}

	public Queue<Webhook> getQueue() {
		return WEBHOOK_QUEUE;
	}

	@Override
	public void run() {

		while (!this.getQueue().isEmpty()) {

			Webhook webhook = this.getQueue().poll();

			TimerTask repeatedTask = new TimerTask() {

				@Override
				public void run() {

					try {

						HttpRequest request = HttpRequest.newBuilder().uri(URI.create(webhook.getUrl()))
								.POST(BodyPublishers.ofString(LocalDateTime.now().format(formatter))).build();

						client.send(request, BodyHandlers.discarding());

					} catch (IOException | InterruptedException e) {
						// TODO Exception handling
						e.printStackTrace();
					}
				}
			};

			TimeUnit currentUnit = ClockUtils.getTimeUnitBy(webhook.getUnit());
			ScheduledFuture scheduleFuture = schedulerExecutor.scheduleAtFixedRate(repeatedTask, 0,
					Long.valueOf(webhook.getInterval()), currentUnit);

			clockService.addScheduledFuture(webhook, scheduleFuture);

		}

	}

}
