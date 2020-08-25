package com.miro.service.storage;

import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;

import com.miro.service.models.Widget;
import com.miro.service.requests.UpdateWidgetRequest;

public class InMemoryWidgetRepository implements WidgetRepository {

	private static final long LOCK_TIMEOUT = 100;
	private static final int DEFAULT_INCREMENT = 100;
	
	private volatile int identifier;
	private volatile int zIndexCounter;
	
	private final HashMap<Integer, Widget> widgetsByIndex;
	private final TreeMap<Integer, Integer> zIndexToWidgetId;
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public InMemoryWidgetRepository() {
		identifier = 0;
		zIndexCounter = 0;
		widgetsByIndex = new HashMap<>();
		zIndexToWidgetId = new TreeMap<>();
	}

	@Override
	public List<Widget> listWidgetsSortedByZIndex() {
		final ReadLock readLock = lock.readLock();
		final boolean couldLock;
		
		try {
			if ((couldLock = readLock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS))) {
				final var result = widgetsByIndex
						.values()
						.stream()
						.sorted((w1, w2) -> Integer.compare(w1.zIndex(), w2.zIndex()))
					    .collect(Collectors.toList());
				
				return result;
			}
		
		} catch(final InterruptedException ex) {
			throw new StorageException("Failed to list widgets", ex);
			
		} finally {
			readLock.unlock();
		}
		
		if (!couldLock) {
			throw new StorageException("Failed to get lock to list widgets");
		}
		
		throw new IllegalStateException("Should not reach here");
	}

	@Override
	public boolean deleteWidget(int id) {
		final WriteLock writeLock = lock.writeLock();
		final boolean couldLock;

		try {
			if ((couldLock = writeLock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS))) {
				final Widget widget = widgetsByIndex.remove(id);
				
				if (widget != null) {
					zIndexToWidgetId.remove(widget.zIndex(), widget.widgetId());
				}
				
				return widget != null;
			}
			
		} catch (final InterruptedException ex) {
			throw new StorageException("Failed to list widgets", ex);
		
		} finally {
			writeLock.unlock();
		}
	
		if (!couldLock) {
			throw new StorageException("Failed to get write lock to delete widget");
		}
		
		throw new IllegalStateException("Should not reach here");
	}

	@Override
	public Optional<Widget> updateWidget(int id, UpdateWidgetRequest request) {
		final WriteLock writeLock = lock.writeLock();
		final boolean couldLock;

		try {
			if ((couldLock = writeLock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS))) {
				final Widget result = widgetsByIndex.compute(id,  (id1, currentWidget)-> {
					if (currentWidget == null) {
						return null;
					}
					
					final int currentZIndex = currentWidget.zIndex();
					
					final Widget updatedWidget = new Widget(
							currentWidget.widgetId(),
							request.xCoordinate().orElse(currentWidget.xCoordinate()),
							request.yCoordinate().orElse(currentWidget.yCoordinate()),
							request.zIndex().orElse(currentWidget.zIndex()),
							request.height().orElse(currentWidget.height()), 
							request.width().orElse(currentWidget.width())
					);
					
					if (currentZIndex != updatedWidget.zIndex()) {
						zIndexToWidgetId.remove(currentZIndex);
					}
					
					return updatedWidget;
				});
				
				if (result == null) {
					return Optional.empty();
				}
				
				if (zIndexToWidgetId.containsKey(result.zIndex())) {
					rearrangeWidgets(result.widgetId(), result.zIndex());
				}

				zIndexToWidgetId.put(result.zIndex(), result.widgetId());
				return Optional.of(result);
			}
			
		} catch (final InterruptedException ex) {
			throw new StorageException("Failed to list widgets", ex);
		
		} finally {
			writeLock.unlock();
		}
	
		if (!couldLock) {
			throw new StorageException("Failed to get write lock to delete widget");
		}
		
		throw new IllegalStateException("Should not reach here");
	}

	@Override
	public Optional<Widget> findWidgetById(int id) {
		final ReadLock readLock = lock.readLock();
		final boolean couldLock;
		
		try {
			if ((couldLock = readLock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS))) {
				final Widget widget = widgetsByIndex.get(id);
				return widget == null ? Optional.<Widget>empty() : Optional.of(widget);
			}
		
		} catch(final InterruptedException ex) {
			throw new StorageException("Failed to list widgets", ex);
			
		} finally {
			readLock.unlock();
		}
		
		if (!couldLock) {
			throw new StorageException("Failed to get lock to list widgets");
		}
		
		throw new IllegalStateException("Should not reach here");
	}

	@Override
	public Widget createWidget(int x, int y, Optional<Integer> zIndex, double height, double width) {
		final WriteLock writeLock = lock.writeLock();
		final boolean couldLock;

		try {
			if ((couldLock = writeLock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS))) {
				final int z = zIndex.orElse(zIndexCounter += delta());

				final Widget result = new Widget(identifier++, x, y, z, height, width);
				if (widgetsByIndex.containsKey(result.widgetId())) {
					throw new IllegalStateException("Autoincrement value duplicate for " + result.widgetId());
				}
				
				if (zIndexToWidgetId.containsKey(result.zIndex())) {
					rearrangeWidgets(result.widgetId(), result.zIndex());
				}
				
				widgetsByIndex.putIfAbsent(result.widgetId(), result);
				zIndexToWidgetId.put(result.zIndex(), result.widgetId());
				
				return result;
			}
		} catch (final InterruptedException ex) {
			throw new StorageException("Failed to list widgets", ex);
		
		} finally {
			writeLock.unlock();
		}
	
		if (!couldLock) {
			throw new StorageException("Failed to get write lock to delete widget");
		}
		
		throw new IllegalStateException("Should not reach here");
	}

	private void rearrangeWidgets(int widgetId, int zIndex) {
		final WriteLock writeLock = lock.writeLock();
		final boolean couldLock;

		try {
			if ((couldLock = writeLock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS))) {
				final NavigableMap<Integer, Integer> keysToUpdate = zIndexToWidgetId.subMap(zIndex, true, zIndexCounter, true);
				final TreeMap<Integer, Integer> map = new TreeMap<>(keysToUpdate);

				int max = Integer.MIN_VALUE;
				
				for (final int z: map.descendingKeySet()) {
					final int id = map.get(z);
					final Widget w = widgetsByIndex.get(id);
					
					if (w == null) {
						throw new IllegalStateException("");
					}
				
					final int newZIndex = w.zIndex() + 1;
					max = Math.max(max, newZIndex);
					
					final Widget newWidget = new Widget(
							w.widgetId(),
							w.xCoordinate(),
							w.yCoordinate(),
							newZIndex,
							w.height(),
							w.width()
						);

					zIndexToWidgetId.remove(z);
					zIndexToWidgetId.put(newZIndex, w.widgetId());
					widgetsByIndex.put(id, newWidget);
				}
				
				zIndex = Math.max(zIndex, max);
				return;
			}
			
			
		} catch (final InterruptedException ex) {
			throw new StorageException("Failed to list widgets", ex);
		
		} finally {
			writeLock.unlock();
		}
	
		if (!couldLock) {
			throw new StorageException("Failed to get write lock to delete widget");
		}
		
		throw new IllegalStateException("Should not reach here");

	}

	private int delta() {
		return ThreadLocalRandom.current().nextInt(100) + 10;
	}

}
