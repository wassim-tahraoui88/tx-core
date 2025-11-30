package com.tahraoui.txcore.collections;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ObservableList<T> {

	public interface Listener<T> {
		void onItemAdded(T item);
		void onItemInserted(int index, T item);
		void onItemUpdated(int index, T oldItem, T newItem);
		void onItemRemoved(T item);
		void onListCleared();
		void onBulkUpdate(List<T> added, List<T> removed);
	}

	public static class ListenerAdapter<T> implements Listener<T> {
		@Override public void onItemAdded(T item) {}
		@Override public void onItemInserted(int index, T item) {}
		@Override public void onItemUpdated(int index, T oldItem, T newItem) {}
		@Override public void onItemRemoved(T item) {}
		@Override public void onListCleared() {}
		@Override public void onBulkUpdate(List<T> added, List<T> removed) {}
	}

	private final List<T> backing;
	private final List<Listener<T>> listeners;
	private final Object lock = new Object();

	public ObservableList() {
		this.backing = new ArrayList<>();
		this.listeners = new CopyOnWriteArrayList<>();
	}

	//region Listener Management
	public void addListener(Listener<T> listener) { listeners.add(listener); }
	public void removeListener(Listener<T> listener) { listeners.remove(listener); }
	private void onItemAdded(T item) {
		for (var listener : listeners)
			listener.onItemAdded(item);
	}
	private void onItemInserted(int index, T item) {
		for (var listener : listeners)
			listener.onItemInserted(index, item);
	}
	private void onItemUpdated(int index, T oldItem, T newItem) {
		for (var listener : listeners)
			listener.onItemUpdated(index, oldItem, newItem);
	}
	private void onItemRemoved(T item) {
		for (var listener : listeners)
			listener.onItemRemoved(item);
	}
	private void onListCleared() {
		for (var listener : listeners)
			listener.onListCleared();
	}
	private void onBulkUpdate(List<T> added, List<T> removed) {
		for (var listener : listeners)
			listener.onBulkUpdate(added, removed);
	}
	//endregion

	//region Single-Item Operations
	public T get(int index) {
		synchronized (lock) {
			return backing.get(index);
		}
	}
	public void add(T item) {
		synchronized (lock) {
			backing.add(item);
		}
		onItemAdded(item);
	}
	public void insert(int index, T item) {
		synchronized (lock) {
			backing.add(index, item);
		}
		onItemInserted(index, item);
	}
	public void update(int index, T newItem) {
		T oldItem;
		synchronized (lock) {
			oldItem = backing.set(index, newItem);
		}
		onItemUpdated(index, oldItem, newItem);
	}
	public T remove (int index) {
		T removedItem;
		synchronized (lock) {
			removedItem = backing.remove(index);
		}
		onItemRemoved(removedItem);
		return removedItem;
	}
	public void clear() {
		synchronized (lock) {
			backing.clear();
		}
		onListCleared();
	}
	//endregion

	//region Bulk Operations
	public void addAll(Collection<T> items) {
		synchronized (lock) {
			backing.addAll(items);
		}
		var addedItems = List.copyOf(items);
		onBulkUpdate(addedItems, List.of());
	}
	public void removeIf(Predicate<T> filter) {
		List<T> removedItems;
		synchronized (lock) {
			removedItems = backing.stream().filter(filter).collect(Collectors.toList());
			if (!removedItems.isEmpty()) backing.removeIf(filter);
		}
		if (!removedItems.isEmpty()) onBulkUpdate(List.of(), removedItems);
	}
	public void replaceAll(Collection<T> items) {
		List<T> removedItems;
		synchronized (lock) {
			removedItems = List.copyOf(backing);
			backing.clear();
			backing.addAll(items);
		}
		var addedItems = List.copyOf(items);
		onBulkUpdate(addedItems, removedItems);
	}
	//endregion

	//region Miscellaneous
	public int size() {
		synchronized (lock) {
			return backing.size();
		}
	}
	public boolean isEmpty() {
		synchronized (lock) {
			return backing.isEmpty();
		}
	}
	public List<T> asUnmodifiable() {
		synchronized (lock) {
			return List.copyOf(backing);
		}
	}
	//endregion
}
