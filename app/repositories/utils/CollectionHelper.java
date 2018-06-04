package repositories.utils;

import scala.Function2;

import java.util.*;
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

    public static <T> List<T> getContainedItemsByComparator(final Collection<T> base, final Collection<T> toContain, BiFunction<T, T, Boolean> comparator) {
        Objects.requireNonNull(base);
        Objects.requireNonNull(toContain);
        final Iterator<T> toContainIterator = toContain.iterator();
        final List<T> returnList = new ArrayList<>();
        toContain.forEach(toContainItem -> {
            for (T aBase : base) {
                if (comparator.apply(toContainItem, aBase)) {
                    returnList.add(toContainItem);
                    break;
                }
            }
        });
        return returnList;
    }

    /**
     * Checks, if an item is contained in the base list by using a custom comparator.
     * @param base The list in which the items could be contained.
     * @param comparator Comparator used to compare each element in the list. The comparator must provide the item to compare!
     * @return Returns true if the item is contained in the base list.
     */
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
