package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;


/**
 * @see IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private IDictionary<T, Integer> setIndex;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        pointers = new int[10];
        setIndex = new ChainedHashDictionary<>();
    }

    @Override
    public void makeSet(T item) {
        if (setIndex.containsKey(item)) {
            throw new IllegalArgumentException();
        }

        int indexOf = setIndex.size();
        if (indexOf == pointers.length) {
            int[] resizedArray = new int[pointers.length * 2];
            for (KVPair<T, Integer> pair: setIndex) {
                resizedArray[pair.getValue()] = pointers[pair.getValue()];
            }
            pointers = resizedArray;
        }
        setIndex.put(item, indexOf);
        pointers[indexOf] = -1;
    }


    @Override
    public int findSet(T item) {
        if (!setIndex.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int itemIndex = setIndex.get(item);
        if (pointers[itemIndex] < 0) {
            return itemIndex;
        } else {
            return findSetHelper(itemIndex);
        }
    }

    private int findSetHelper(int indexOfItem) {
        int theItemIndex = pointers[indexOfItem];
        if (theItemIndex < 0) {
            return indexOfItem;
        } else {
            pointers[indexOfItem] = findSetHelper(theItemIndex);
            return (pointers[indexOfItem]);
        }
    }

    @Override
    public void union(T item1, T item2) {
        if (!(setIndex.containsKey(item1)) || !setIndex.containsKey(item2)) {
            throw new IllegalArgumentException();
        }

        int representative1 = findSet(item1);
        int representative2 = findSet(item2);

        if (representative1 != representative2) {
            int rankOfRep1 = makeRank(pointers[representative1]);
            int rankOfRep2 = makeRank(pointers[representative2]);

            if (rankOfRep1 < rankOfRep2) {
                pointers[representative1] = representative2;
            } else if (rankOfRep2 < rankOfRep1) {
                pointers[representative2] = representative1;

            } else {
                pointers[representative2] = representative1;
                pointers[representative1] = makeRank(representative1 + 1);
            }

        }
    }

    private int makeRank(int initial) {
        return ((initial * -1) - 1);
    }
}
