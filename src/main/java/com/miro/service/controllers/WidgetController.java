package com.miro.service.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.miro.service.models.Widget;
import com.miro.service.requests.CreateWidgetRequest;
import com.miro.service.requests.Response;
import com.miro.service.requests.UpdateWidgetRequest;
import com.miro.service.storage.WidgetRepository;

@RestController
public class WidgetController {
	
	private final Logger logger = LoggerFactory.getLogger(WidgetController.class);
	
	@Autowired
	WidgetRepository widgetRepository;
	
	@GetMapping(
		value = "/api/widget/{id}",
		produces = "application/json", 
		consumes = "application/json"
	)
	@ResponseBody
	public Widget getWidget(@PathVariable("id") int widgetId) {
		final Optional<Widget> result = widgetRepository.findWidgetById(widgetId);
		if (!result.isPresent()) {
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "widget not found"
			);
		}
		
		return result.get();
	}

	@PostMapping(
		value = "/api/widget",
		produces = "application/json", 
		consumes = "application/json"
	)
	@ResponseBody
	public Widget createWidget(@RequestBody CreateWidgetRequest request) {
		final Widget result = widgetRepository.createWidget(request.xCoordinate(), request.yCoordinate(), request.zIndex(), request.height(), request.width());
		return result;
	}
	
	@PutMapping(
		value = "/api/widget/{id}",
		produces = "application/json", 
		consumes = "application/json"
	)
	@ResponseBody
	public Widget updateWidget(@PathVariable("id") int widgetId, @RequestBody UpdateWidgetRequest request) {
		final Optional<Widget> result = widgetRepository.updateWidget(widgetId, request);
		if (!result.isPresent()) {
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "widget not found"
					);
		}

		return result.get();
	}

	@DeleteMapping(
		value = "/api/widget/{id}",
		produces = "application/json", 
		consumes = "application/json"
	)
	public Response deleteWidget(@PathVariable("id") int widgetId) {
		if (!widgetRepository.deleteWidget(widgetId)) {
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "widget not found"
					);
		}
		
		return Response.OK_RESPONSE;
	}

	@GetMapping(
		value = "/api/widget",
		produces = "application/json", 
		consumes = "application/json"
	)
	@ResponseBody		
	public List<Widget> listWidgets() {
		final List<Widget> result = widgetRepository.listWidgetsSortedByZIndex();
		return result;
	}
}