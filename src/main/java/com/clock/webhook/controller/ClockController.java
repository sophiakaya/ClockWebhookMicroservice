package com.clock.webhook.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clock.webhook.model.Webhook;
import com.clock.webhook.service.ClockService;

@RestController
@RequestMapping("/clock")
public class ClockController {

	@Autowired
	ClockService clockService;

	@PostMapping(path = "/register", consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> register(@Valid @RequestBody Webhook webhook) {

		return clockService.register(webhook);

	}

	@PostMapping(path = "/unregister")
	public ResponseEntity<?> unregister(@RequestParam(value = "callbackUrl") String url) {

		return clockService.unregister(url);

	}

	@PutMapping(path = "/frequency", consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> changeFrequency(@Valid @RequestBody Webhook webhook) {

		return clockService.setFrequency(webhook);

	}
}
