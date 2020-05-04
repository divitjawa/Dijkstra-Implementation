package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * @see IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int size;

    // Feel free to add more fields and constants.

    public ArrayHeap() {
        size = 0;
        heap = makeArrayOfT(10);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[arraySize]);
    }

    @Override
    public T removeMin() {
        if (isEmpty()) {
            throw new EmptyContainerException();
        }
        T removedVal = heap[0];
        heap[0] = heap[size() -1];
        heap[size() -1] = null;
        size--;
        if (size > 1) {
            percolateDown(0);
        }
        return removedVal;
    }

    @Override
    public T peekMin() {
        if (isEmpty()) {
            throw new EmptyContainerException();
        }
        return heap[0];
    }

    //  Still need to add compareTo if condition for equal entries
    private void percolateDown(int level) {
        if (NUM_CHILDREN * level < heap.length) {
            T min = heap[level];
            int minIndex = -1;
            int i = 1;
            if (heap[(NUM_CHILDREN * level) + i] != null) {
                while (i <= NUM_CHILDREN && heap[(NUM_CHILDREN * level) + i] != null) {
                    if (min.compareTo(heap[(NUM_CHILDREN * level) + i]) > 0) {
                        min = heap[(NUM_CHILDREN * level) + i];
                        minIndex = (NUM_CHILDREN * level) + i;
                    }
                    i++;
                }
                if (minIndex != -1) {
                    T temp = heap[minIndex];
                    heap[minIndex] = heap[level];
                    heap[level] = temp;
                    percolateDown(minIndex);
                }
            }
        }
    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (size == 0) {
            heap[0] = item;
        } else {
            if (size == heap.length - 1) {
                T[] tempArray = makeArrayOfT(heap.length * 2);
                for (int i = 0; i < heap.length; i++) {
                    tempArray[i] = heap[i];
                }
                heap = tempArray;
            }
            heap[size] = item;
            percolateUp(size);
        }
        size++;

    }

    private void percolateUp(int level) {
        while (heap[level].compareTo(heap[(level -1) /NUM_CHILDREN]) < 0) {
            T tempVal = heap[(level -1) /NUM_CHILDREN];
            heap[(level -1) /NUM_CHILDREN] = heap[level];
            heap[level] = tempVal;
            percolateUp((level -1) /NUM_CHILDREN);
        }
    }

    @Override
    public int size() {
        return size;
    }

}
