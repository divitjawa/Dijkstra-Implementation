package datastructures.concrete.dictionaries;

//import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see datastructures.interfaces.IDictionary
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field.
    // We will be inspecting it in our private tests.
    private Pair<K, V>[] pairs;
    private int size;
    private int initialSize;
    // You may add extra fields or helper methods though!

    public ArrayDictionary() {
        size = 0;
        initialSize = 10;
        pairs = makeArrayOfPairs(initialSize);
    }


    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            throw new NoSuchKeyException();
        } else {
            return pairs[returnIndexOf(key)].value;
        }

    }

    private int returnIndexOf(K key) {
        for (int i = 0; i < size; i++) {
            if ((pairs[i].key == null && key == null)|| pairs[i].key!= null && pairs[i].key.equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void put(K key, V value) {
        Pair<K, V> latestPair = new Pair<>(key, value);
        if (!containsKey(key)) {
            if (size == initialSize) {
                initialSize *=2;
                Pair<K, V>[] biggerArray = makeArrayOfPairs(initialSize);
                for (int i = 0; i < size; i++) {
                    biggerArray[i] = pairs[i];
                }
                biggerArray[size] = latestPair;
                size++;
                pairs = biggerArray;
            } else {
                pairs[size] = latestPair;
                size++;
            }
        } else {
            pairs[returnIndexOf(key)] = latestPair;
        }
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            throw new NoSuchKeyException();
        } else {
            V returnedValue = pairs[returnIndexOf(key)].value;
            if (returnIndexOf(key)!= size - 1) {
                pairs[returnIndexOf(key)] = pairs[size - 1];
            } else {
                pairs[size - 1] = null;
            }
            size--;
            return returnedValue;
        }
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        int keyIndex = returnIndexOf(key);
        if (keyIndex != -1) {
            return pairs[keyIndex].value;
        } else {
            return defaultValue;
        }

    }

    @Override
    public boolean containsKey(K key) {
        return returnIndexOf(key)!= -1;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<K, V>(0, size, pairs);
    }

    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        // Add any fields you need to store state information
        int index;
        int size;
        Pair<K, V>[] arrayPointer;

        public ArrayDictionaryIterator(int index, int size, Pair<K, V>[] pairs) {
            this.index = index;
            this.size = size;
            arrayPointer = pairs;
        }

        public boolean hasNext() {
            return(this.index < size);
        }

        public KVPair<K, V> next() {
            // Return the next KVPair in the dictionary
            KVPair<K, V> toReturn = new KVPair<>(null, null);
            if (hasNext()) {
                toReturn = new KVPair<>(arrayPointer[index].getKey(), arrayPointer[index].getValue());
            } else {
                throw new NoSuchElementException("element does not exist");
            }
            index++;
            return toReturn;
        }
    }

    @Override
    public int size() {
        return size;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}

