package com.miro.service.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
	
	public static final Response OK_RESPONSE = new Response("OK");
	
	private final String message;
	
	public Response(String message) {
		this.message = message;
	}
	
	@JsonProperty("message")
	public String message() {
		return message;
	}
}
