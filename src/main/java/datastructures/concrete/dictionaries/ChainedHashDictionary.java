package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see IDictionary and the assignment page for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private int sizeOfChain;
    private int keyHash;
    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashDictionary() {
        sizeOfChain = 0;
        chains = makeArrayOfChains(10);
        keyHash = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
        if (key != null) {
            keyHash = getHashCode(key.hashCode(), chains);
        }
        if (chains[keyHash] == null || !chains[keyHash].containsKey(key)) {
            throw new NoSuchKeyException();
        } else {
            return chains[keyHash].get(key);
        }
    }

    @Override
    public void put(K key, V value) {
        if (key != null) {
            keyHash = getHashCode(key.hashCode(), chains);
        }
        if (chains[keyHash]== null) {
            chains[keyHash] = new ArrayDictionary<K, V>();
        }
        if (!chains[keyHash].containsKey(key)) {
            sizeOfChain++;
        }
        chains[keyHash].put(key, value);
        double loadFactor = (double) (sizeOfChain / chains.length);
        if (loadFactor > 0.9) {
            IDictionary<K, V>[] newChains = makeArrayOfChains((chains.length * 2));
            for (int i = 0; i < chains.length; i++) {
                if (chains[i]!= null) {
                    for (KVPair<K, V> shiftPairs: chains[i]) {
                        newChainCreate(shiftPairs, newChains);
                    }
                }
            }
            chains = newChains;
        }
    }

    private void newChainCreate(KVPair<K, V> newKey, IDictionary<K, V>[] newChains) {
        int reHash = (getHashCode(newKey.getKey().hashCode(), newChains));
        if (newChains[reHash] == null) {
            newChains[reHash] = new ArrayDictionary<>();
        }
        newChains[reHash].put(newKey.getKey(), newKey.getValue());
    }

    @Override
    public V remove(K key) {
        if (key != null) {
            keyHash = getHashCode(key.hashCode(), chains);
        }
        if (chains[keyHash] == null) {
            throw new NoSuchKeyException();
        }
        sizeOfChain--;
        return chains[keyHash].remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        if (key != null) {
            keyHash = getHashCode(key.hashCode(), chains);
        }
        if (!(chains[keyHash] == null)) {
            return chains[keyHash].containsKey(key);
        }
        return false;
    }

    @Override
    public int size() {
        return sizeOfChain;
    }

    private int getHashCode(int hashCode, IDictionary<K, V>[] dictChain) {
        if (hashCode < 0) {
            hashCode *= -1;
        }
        return hashCode % dictChain.length;
    }

    public V getOrDefault(K key, V defaultValue) {
        int position = getHashCode(key.hashCode(), chains);
        return chains[position].getOrDefault(key, defaultValue);
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be
     *    true once the constructor is done setting up the class AND
     *    must *always* be true both before and after you call any
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int currentIndex;
        private Iterator<KVPair<K, V>> kvPairIterator;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            currentIndex = 0;
            checkNext(currentIndex);
        }

        @Override
        public boolean hasNext() {
            for (int i = 0; i < chains.length; i++) {
                if (kvPairIterator != null && kvPairIterator.hasNext()) {
                    return true;
                }
                else if (currentIndex == chains.length - 1) {
                    return false;
                }
                checkNext(++currentIndex);
            }
            return false;
        }

        private void checkNext(int nextNum) {
            if (chains[nextNum] == null) {
                kvPairIterator = null;
            } else {
                kvPairIterator = chains[nextNum].iterator();
            }
        }

        @Override
        public KVPair<K, V> next() {
            if (hasNext()) {
                return kvPairIterator.next();
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
