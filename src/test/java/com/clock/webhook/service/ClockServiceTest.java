package com.clock.webhook.service;

import java.util.concurrent.ExecutorService;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.clock.webhook.model.Webhook;

@RunWith(MockitoJUnitRunner.class)
public class ClockServiceTest {

	@Mock
	private Webhook webhook;

	@InjectMocks
	@Autowired
	private ClockServiceImpl clockService;

	@Mock
	private ExecutorService executorService;

	@BeforeEach
	public void init() {

		MockitoAnnotations.initMocks(this);

	}

	/**
	 * Test {@link ClockService#register(Webhook)}
	 */
	@Test
	public void registerSuccessTest() {

		Mockito.when(webhook.getUrl()).thenReturn("https://webhook.site/8faf1e95-ec5d-4c18-ac30-d2cc3e795a17");
		Mockito.when(webhook.getInterval()).thenReturn(10);
		Mockito.when(webhook.getUnit()).thenReturn("S");

		ResponseEntity<String> result = clockService.register(webhook);

		Assert.assertEquals(HttpStatus.OK, result.getStatusCode());

	}

	/**
	 * TODO Test {@link ClockService#register(Webhook)}
	 */
	public void registerErrorTest() {

	}

	/**
	 * TODO Test {@link ClockService#unregister(String)}
	 */
	public void unregisterSuccessTest() {

	}

	/**
	 * TODO Test {@link ClockService#unregister(String)}
	 */
	public void unregisterErrorTest() {

	}

	/**
	 * TODO Test {@link ClockService#setFrequency(Webhook)}
	 */
	public void setFrequencySuccessTest() {

	}

	/**
	 * TODO Test {@link ClockService#setFrequency(Webhook)}
	 */
	public void setFrequencyErrorTest() {

	}
}
