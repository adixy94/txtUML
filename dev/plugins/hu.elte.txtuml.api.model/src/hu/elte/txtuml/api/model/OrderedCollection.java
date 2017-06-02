package hu.elte.txtuml.api.model;

import hu.elte.txtuml.api.model.GeneralCollection.NonUnique;
import hu.elte.txtuml.api.model.GeneralCollection.Ordered;

//TODO document
public abstract class OrderedCollection<E, C extends OrderedCollection<E, C>> extends AbstractOrderedCollection<E, C>
		implements Ordered<E>, NonUnique<E> {

	protected OrderedCollection() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public final <C2 extends GeneralCollection<? super E>,
		C3 extends GeneralCollection<?>> C2 as(Class<C3> collectionType) {
		if (NonUnique.class.isAssignableFrom(collectionType)) {
			return (C2) asUnsafe(collectionType);
		} else {
			// TODO exception handling
			throw new Error();
		}
	}

	@Override
	public final int countOf(E element) {
		int count = 0;
		for (E e : getBackend()) {
			if (e.equals(element)) {
				++count;
			}
		}
		return count;
	}

	@Override
	public final GeneralCollection<E> unbound() {
		return asOrderedAnyUnsafe();
	}

}
