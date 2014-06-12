package teetime.util.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReservableArrayList<T> implements List<T> {

	private final T[] elements;

	private int lastFreeIndex, lastFreeReservedIndex;

	@SuppressWarnings("unchecked")
	public ReservableArrayList(final int initialSize) {
		this.elements = (T[]) new Object[initialSize];
	}

	public void reservedAdd(final T element) {
		this.elements[this.lastFreeReservedIndex++] = element;
	}

	public void commit() {
		// TODO set elements to null
		this.lastFreeIndex = this.lastFreeReservedIndex;
	}

	public void rollback() {
		this.lastFreeReservedIndex = this.lastFreeIndex;
	}

	@Override
	public int size() {
		return this.lastFreeIndex;
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public boolean contains(final Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		return this.elements;
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(final T e) {
		this.elements[this.lastFreeIndex++] = e;
		this.lastFreeReservedIndex = this.lastFreeIndex;
		return true;
	}

	@Override
	public boolean remove(final Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(final Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		this.lastFreeIndex = this.lastFreeReservedIndex = 0;
	}

	@Override
	public final T get(final int index) {
		T element = this.elements[index];
		return element;
	}

	@Override
	public T set(final int index, final T element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(final int index, final T element) {
		// TODO Auto-generated method stub

	}

	@Override
	public T remove(final int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(final Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(final Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<T> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<T> listIterator(final int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> subList(final int fromIndex, final int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public T getLast() {
		T element = this.get(this.lastFreeIndex - 1);
		return element;
	}

	public T reservedRemoveLast() {
		T element = this.get(this.lastFreeReservedIndex--);
		return element;
	}
}
