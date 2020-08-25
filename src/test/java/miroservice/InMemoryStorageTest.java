package miroservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;

import com.miro.service.models.Widget;
import com.miro.service.requests.UpdateWidgetRequest;
import com.miro.service.storage.InMemoryWidgetRepository;
import com.miro.service.storage.WidgetRepository;

public class InMemoryStorageTest {

	private WidgetRepository repository;
	private volatile int counter;
	
	@Before
	public void setUp() {
		repository = new InMemoryWidgetRepository();
	}
	
	@Test
	public void testCreate() {
		final Widget widget1 = createRandomWidget();
		final Widget insertWidget1 = insertWidget(widget1, true);
		assertEquals(widget1, insertWidget1);
		
		final Widget insertWidget2 = insertWidget(widget1, false);
		assertTrue(insertWidget1.zIndex() < insertWidget2.zIndex());
	}

	@Test
	public void testFindAndUpdate() {
		final Widget widget1 = createRandomWidget();
		final Widget insertWidget1 = insertWidget(widget1, true);
		
		final Optional<Widget> foundWidget = repository.findWidgetById(widget1.widgetId());
		assertTrue(foundWidget.isPresent());
		assertEquals(insertWidget1, foundWidget.get());

		assertFalse(repository.findWidgetById(123123).isPresent());
		
		final UpdateWidgetRequest request = new UpdateWidgetRequest(Optional.empty(), Optional.of(100), Optional.empty(), Optional.empty(), Optional.empty());
		final Optional<Widget> maybeUpdatedWidget = repository.updateWidget(widget1.widgetId(), request);
		assertTrue(maybeUpdatedWidget.isPresent());
		
		final Widget updatedWidget = maybeUpdatedWidget.get();
		
		assertEquals(updatedWidget.widgetId(), widget1.widgetId());
		assertEquals(updatedWidget.xCoordinate(), widget1.xCoordinate());
		assertEquals(updatedWidget.yCoordinate(), 100);
		assertEquals(updatedWidget.height(), widget1.height(), 0.0001);
		assertEquals(updatedWidget.width(), widget1.width(), 0.0001);

	}
	
	@Test
	public void testDuplicateZIndex() {
		final Widget widget1 = createRandomWidget();
		
		final Widget insertWidget1 = insertWidget(widget1, 1);
		assertEquals(insertWidget1.zIndex(), 1);

		final Widget widget2 = createRandomWidget();
		final Widget insertWidget2 = insertWidget(widget2, 1);
		assertEquals(insertWidget2.zIndex(), 1);
		
		final Optional<Widget> foundWidget = repository.findWidgetById(widget1.widgetId());
		assertTrue(foundWidget.isPresent());
		assertNotEquals(insertWidget1.zIndex(), foundWidget.get().zIndex());
		
		final Optional<Widget> foundWidget2 = repository.findWidgetById(widget1.widgetId());
		assertTrue(foundWidget2.isPresent());
		assertEquals(insertWidget2.zIndex(), foundWidget2.get().zIndex());
	}


	@Test
	public void testEmptyList() {
		final List<Widget> listedWidgets = repository.listWidgetsSortedByZIndex();
		assertEquals(0, listedWidgets.size());
	}
	
	@Test
	public void testListAndDelete() {
		final List<Widget> widgets = new ArrayList<>();
		widgets.add(insertWidget(createRandomWidget(), false));
		widgets.add(insertWidget(createRandomWidget(), false));
		widgets.add(insertWidget(createRandomWidget(), false));
		Collections.sort(widgets, (w1, w2) -> Integer.compare(w1.zIndex(), w2.zIndex()));

		List<Widget> listedWidgets = repository.listWidgetsSortedByZIndex();
		assertEquals(widgets, listedWidgets);
		
		final Widget removedWidget = widgets.remove(2);
		assertTrue(repository.deleteWidget(removedWidget.widgetId()));

		listedWidgets = repository.listWidgetsSortedByZIndex();
		assertEquals(widgets, listedWidgets);
		
		final Widget widget4 = insertWidget(createRandomWidget(), false);
		widgets.add(widget4);
		Collections.sort(widgets, (w1, w2) -> Integer.compare(w1.zIndex(), w2.zIndex()));
		
		listedWidgets = repository.listWidgetsSortedByZIndex();
		assertEquals(widgets, listedWidgets);
		
		for (final Widget w: widgets) {
			assertTrue(repository.deleteWidget(w.widgetId()));
		}
		
		assertEquals(0, repository.listWidgetsSortedByZIndex().size());
	}
	
	@Test
	public void testEmptyDelete() {
		assertFalse(repository.deleteWidget(1));
	}


	private Widget createRandomWidget() {
		final int id = counter++;
		final int x = ThreadLocalRandom.current().nextInt(10);
		final int y = ThreadLocalRandom.current().nextInt(10);
		final int z = ThreadLocalRandom.current().nextInt(10);
		final float height = ThreadLocalRandom.current().nextFloat();
		final float width = ThreadLocalRandom.current().nextFloat();
		
		final Widget widget = new Widget(id, x, y, z, height, width);
		return widget;
	}
	
	private Widget insertWidget(Widget widget, boolean withZIndex) {
		final Optional<Integer> zIndex = withZIndex ? Optional.of(widget.zIndex()) : Optional.empty();
		final Widget result = repository.createWidget(widget.xCoordinate(), widget.yCoordinate(), zIndex, widget.height(), widget.width());
		
		return result;
	}
	
	private Widget insertWidget(Widget widget, int zIndex) {
		final Optional<Integer> z =  Optional.of(widget.zIndex());
		final Widget result = repository.createWidget(widget.xCoordinate(), widget.yCoordinate(), z, widget.height(), widget.width());
		
		return result;
	}

}
