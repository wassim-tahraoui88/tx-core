package com.tahraoui.txcore.collections;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObservableList<T> implements List<T> {

	public interface Listener<T> {
		void onItemAdded(T item);
		void onItemInserted(int index, T item);
		void onItemUpdated(int index, T oldItem, T newItem);
		void onItemRemoved(T item);
		void onListCleared();
		void onBulkUpdate(Collection<T> added, Collection<T> removed);
	}

	public static class ListenerAdapter<T> implements Listener<T> {
		@Override public void onItemAdded(T item) {}
		@Override public void onItemInserted(int index, T item) {}
		@Override public void onItemUpdated(int index, T oldItem, T newItem) {}
		@Override public void onItemRemoved(T item) {}
		@Override public void onListCleared() {}
		@Override public void onBulkUpdate(Collection<T> added, Collection<T> removed) {}
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
	private void onBulkUpdate(Collection<T> added, Collection<T> removed) {
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
	public boolean add(T item) {
		synchronized (lock) {
			backing.add(item);
		}
		onItemAdded(item);
		return true;
	}
	public void add(int index, T element) {
		synchronized (lock) {
			backing.add(index, element);
		}
		onItemInserted(index, element);
	}
	public T set(int index, T newElement) {
		T oldElement;
		synchronized (lock) {
			oldElement = backing.set(index, newElement);
		}
		onItemUpdated(index, oldElement, newElement);
		return oldElement;
	}
	public boolean remove(Object o) {
		boolean removed;
		synchronized (lock) {
			removed = backing.remove(o);
		}
		if (removed) onItemRemoved((T) o);
		return removed;
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
	public boolean addAll(Collection<? extends T> items) {
		synchronized (lock) {
			backing.addAll(items);
		}
		onBulkUpdate(List.copyOf(items), List.of());
		return true;
	}
	public boolean addAll(int index, Collection<? extends T> c) {
		synchronized (lock) {
			backing.addAll(index, c);
		}
		onBulkUpdate(List.copyOf(c), List.of());
		return true;
	}
	public boolean removeAll(Collection<?> c) {
		List<T> removedItems;
		synchronized (lock) {
			removedItems = backing.stream()
					.filter(c::contains)
					.collect(Collectors.toList());
			if (!removedItems.isEmpty()) backing.removeAll(c);
		}
		if (!removedItems.isEmpty()) onBulkUpdate(List.of(), removedItems);
		return true;
	}
	public boolean removeIf(Predicate<? super T> filter) {
		List<T> removedItems;
		synchronized (lock) {
			removedItems = backing.stream().filter(filter).collect(Collectors.toList());
			if (!removedItems.isEmpty()) backing.removeIf(filter);
		}
		if (!removedItems.isEmpty()) onBulkUpdate(List.of(), removedItems);
		return true;
	}
	public void replaceAll(Collection<? extends T> items) {
		List<T> removedItems;
		synchronized (lock) {
			removedItems = new ArrayList<>(backing);
			backing.clear();
			backing.addAll(items);
		}
		onBulkUpdate(List.copyOf(items), removedItems);
	}
	public boolean retainAll(Collection<?> c) {
		List<T> removedItems;
		synchronized (lock) {
			removedItems = backing.stream()
					.filter(item -> !c.contains(item))
					.collect(Collectors.toList());
			if (!removedItems.isEmpty()) backing.retainAll(new HashSet<>(c));
		}
		if (!removedItems.isEmpty()) onBulkUpdate(List.of(), removedItems);
		return true;
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
	public boolean contains(Object element) {
		synchronized (lock) {
			return backing.contains(element);
		}
	}
	public boolean containsAll(Collection<?> c) {
		synchronized (lock) {
			for (var e : c) {
				if (!contains(e)) return false;
			}
			return true;
		}
	}
	public int indexOf(Object element) {
		synchronized (lock) {
			return backing.indexOf(element);
		}
	}
	public int lastIndexOf(Object element) {
		synchronized (lock) {
			return backing.lastIndexOf(element);
		}
	}
	//endregion

	//region Views
	public Stream<T> stream() {
		synchronized (lock) {
			return backing.stream();
		}
	}
	public Iterator<T> iterator() {
		synchronized (lock) {
			return new ArrayList<>(backing).iterator();
		}
	}
	public Object[] toArray() {
		synchronized (lock) {
			return backing.toArray();
		}
	}
	public <T1> T1[] toArray(T1[] a) {
		synchronized (lock) {
			return backing.toArray(a);
		}
	}
	public ListIterator<T> listIterator() {
		synchronized (lock) {
			return backing.listIterator();
		}
	}
	public ListIterator<T> listIterator(int index) {
		synchronized (lock) {
			return backing.listIterator(index);
		}
	}
	public List<T> subList(int fromIndex, int toIndex) {
		synchronized (lock) {
			return backing.subList(fromIndex, toIndex);
		}
	}
	public List<T> asUnmodifiable() {
		synchronized (lock) {
			return List.copyOf(backing);
		}
	}
	//endregion
}
