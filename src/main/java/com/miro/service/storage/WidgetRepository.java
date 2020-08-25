package com.miro.service.storage;

import java.util.List;
import java.util.Optional;

import com.miro.service.models.Widget;
import com.miro.service.requests.UpdateWidgetRequest;

/**
 * 
 * A repository providing widget storage operations.
 *
 */
public interface WidgetRepository {
	/**
	 * 
	 * @param id identifyer of the widget
	 * @return Optional#of(widget), if widget exists,
	 * 		   otherwise, Optional#empty()
	 * @throws StorageException when fails
	 */
	Optional<Widget> findWidgetById(int id);
	
	/**
	 * 
	 * @return fetches all the widgets sorted in Z-index order(from lowest to highest)
	 * @throws StorageException when fails
	 */
	List<Widget> listWidgetsSortedByZIndex();
	
	/**
	 * 
	 * Create a widget with the given parameters. If zIndex is {@link Optional#empty()} put in the front
	 * @param x
	 * @param y
	 * @param zIndex 
	 * @param height
	 * @param width
	 * @return newly created widget
	 * @throws StorageException when fails
	 */
	Widget createWidget(int x, int y, Optional<Integer> zIndex, double height, double width);
	
	/**
	 * Update the widget settings for the given parameters only. Widget will only have consistent view,
	 * i.e. will be updated only atomically.
	 * 
	 * @param id
	 * @param request
	 * @return updated widget if it existed in the moment of update; otherwise - {@link Optional#empty()}
	 * @throws StorageException when fails
	 */
	Optional<Widget> updateWidget(int id, UpdateWidgetRequest request);
	
	/*
	 * Delete the widget for the given id
	 * @throws StorageException when fails
	 */
	boolean deleteWidget(int id);
}
