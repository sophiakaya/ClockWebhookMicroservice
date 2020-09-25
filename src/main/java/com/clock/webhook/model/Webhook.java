package com.clock.webhook.model;

import javax.validation.constraints.NotNull;

public class Webhook {

	@NotNull(message = "Url cannot be null")
	private String url;

	@NotNull(message = "Interval name cannot be null")
	private int interval;

	@NotNull(message = "Unit can be S for seconds, M for minutes and H for hours")
	private String unit;

	public Webhook(String url, int interval, String unit) {
		this.url = url;
		this.interval = interval;
		this.unit = unit;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
