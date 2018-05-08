package repositories.utils;

import scala.Function2;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CollectionHelper {
    private CollectionHelper() {

    }

    /**
     * Retains only the items that are also in the shouldBeContained collection.
     * @param base The base collection. This collection might get items removed, if the comparator returns false.
     * @param comparator A comparator that returns true, if the item should be retained, or false if it should be removed.
     */
    public static <T> boolean retainAllByComparator(final Collection<T> base, final Function<T, Boolean> comparator) {
        Objects.requireNonNull(base);
        boolean collectionWasChanged = false;
        final Iterator<T> baseIterator = base.iterator();

        while(baseIterator.hasNext()) {
            if (!comparator.apply(baseIterator.next())) {
                baseIterator.remove();
                collectionWasChanged = true;
            }
        }

        return collectionWasChanged;
    }

    public static <T> boolean containsByComparator(final Collection<T> base, final Function<T, Boolean> comparator) {
        Objects.requireNonNull(base);

        for (T item : base) {
            if (comparator.apply(item)) {
                return true;
            }
        }

        return false;
    }
}
