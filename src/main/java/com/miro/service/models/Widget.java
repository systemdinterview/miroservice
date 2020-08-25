package com.miro.service.models;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Widget {
	private final int widgetId;
	
	private final int xCoordinate;
	private final int yCoordinate;
	private final int zIndex;
	
	private final double height;
	private final double width;
	
	private final Instant lastUpdateAt;
	
	@JsonCreator
	public Widget(@JsonProperty("widgetId") int widgetId, int x, int y, int zIndex, double height, double width) {
		this.widgetId = widgetId;
		this.xCoordinate = x;
		this.yCoordinate = y;
		this.zIndex = zIndex;
		this.height = height;
		this.width = width;
		this.lastUpdateAt = Instant.now();
	}
	
	@JsonProperty("widgetId")
	public int widgetId() {
		return widgetId;
	}
	
	@JsonProperty("x")
	public int xCoordinate() {
		return xCoordinate;
	}

	@JsonProperty("y")
	public int yCoordinate() {
		return yCoordinate;
	}

	@JsonProperty("zIndex")
	public int zIndex() {
		return zIndex;
	}

	@JsonProperty("height")
	public double height() {
		return height;
	}
	
	@JsonProperty("width")
	public double width() {
		return width;
	}
	
	@JsonProperty("updatedAt")
	public ZonedDateTime updatedAt() {
		return lastUpdateAt.atZone(ZoneId.systemDefault());
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(obj, this, Arrays.asList("lastUpdateAt"));
	}
}
