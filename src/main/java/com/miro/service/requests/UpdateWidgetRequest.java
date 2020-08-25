package com.miro.service.requests;

import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UpdateWidgetRequest {	
	private final Optional<Integer> xCoordinate;
	private final Optional<Integer> yCoordinate;
	private final Optional<Integer> zIndex;
	
	private final Optional<Double> height;
	private final Optional<Double> width;
	
	@JsonCreator
	public UpdateWidgetRequest(@JsonProperty("x") Optional<Integer> x, @JsonProperty("y") Optional<Integer> y, @JsonProperty("zIndex") Optional<Integer> zIndex, @JsonProperty("height") Optional<Double> height, @JsonProperty("width") Optional<Double> width) {
		this.xCoordinate = x;
		this.yCoordinate = y;
		this.zIndex = zIndex;
		this.height = height;
		this.width = width;
	}
	
	public Optional<Integer> xCoordinate() {
		return xCoordinate;
	}

	public Optional<Integer> yCoordinate() {
		return yCoordinate;
	}

	public Optional<Integer> zIndex() {
		return zIndex;
	}

	public Optional<Double> height() {
		return height;
	}
	
	public Optional<Double> width() {
		return width;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
