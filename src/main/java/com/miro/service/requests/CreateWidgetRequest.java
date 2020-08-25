package com.miro.service.requests;

import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class CreateWidgetRequest {	
	private final int xCoordinate;
	private final int yCoordinate;
	private final Optional<Integer> zIndex;
	
	private final double height;
	private final double width;
	
	@JsonCreator
	public CreateWidgetRequest(@JsonProperty("widgetId") int widgetId, @JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("zIndex") Optional<Integer> zIndex, @JsonProperty("height") double height, @JsonProperty("width") double width) {
		this.xCoordinate = x;
		this.yCoordinate = y;
		this.zIndex = zIndex;
		this.height = height;
		this.width = width;
	}
	
	public int xCoordinate() {
		return xCoordinate;
	}

	public int yCoordinate() {
		return xCoordinate;
	}

	public Optional<Integer> zIndex() {
		return zIndex;
	}

	public double height() {
		return height;
	}
	
	public double width() {
		return width;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
