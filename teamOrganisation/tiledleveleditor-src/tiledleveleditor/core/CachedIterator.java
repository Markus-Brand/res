package tiledleveleditor.core;

import java.util.Iterator;

/**
 * An iterator wich replaces the hasNext()==false with next()==null, by caching
 * one element
 *
 * @author Erik
 * @param <T>
 */
public class CachedIterator<T> implements Iterator<T> {
	
	public static <K> Iterable<K> iterable(ItemGetter<K> getter) {
		return new Iterable<K>() {

			@Override
			public Iterator<K> iterator() {
				return new CachedIterator<K>(getter);
			}
		};
	}

	/**
	 * Override tryNext() when using this constructor
	 */
	public CachedIterator() {
		getter = null;
	}

	public CachedIterator(ItemGetter<T> getter) {
		this.getter = getter;
	}

	protected T cache = null;
	private ItemGetter<T> getter = null;

	@Override
	public boolean hasNext() {
		if (cache != null) {
			return true;
		}
		cache = tryNext();
		return cache != null;
	}

	@Override
	public T next() {
		if (cache == null && !hasNext()) {
			return null;
		}
		T t = cache;
		cache = null;
		return t;
	}

	/**
	 * override this when no getter is specified
	 *
	 * @return null by default
	 */
	public T tryNext() {
		return getter == null ? null : getter.tryNext();
	}

	@FunctionalInterface
	public interface ItemGetter<T> {

		public abstract T tryNext();
	}
}

