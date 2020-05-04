package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;
//import misc.exceptions.NotYetImplementedException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods:
 * @see datastructures.interfaces.IList
 * (You should be able to control/command+click "IList" above to open the file from IntelliJ.)
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        if (isEmpty()) {
            front = new Node<T>(item);
            back = front;
        } else {
            back.next = new Node<T>(back, item, null);
            back = back.next;
        }
        size++;
    }

    @Override
    public T remove() {
        Node<T> current = back;
        if (isEmpty()) {
            throw new EmptyContainerException();
        } else {
            if (front == back) {
                back = back.next;
                front = null;
            } else {
                back = back.prev;
                back.next = null;
            }
        }
        size--;
        return current.data;
    }

    @Override
    public T get(int index) {
        Node<T> current = front;
        if ((index < 0 || index >= size)) {
            throw new IndexOutOfBoundsException();
        }
        if (front !=null) {
            current = traverse(current, index);
        }
        return current.data;
    }


    @Override
    public void set(int index, T item) {
        if ((index < 0 || index >= size)) {
            throw new IndexOutOfBoundsException();
        } else {
            if (!isEmpty()){
                Node<T> current = front;
                current = traverse(current, index);
                Node<T> temp = new Node<T>(current.prev, item, current.next);
                if (current.prev == null) {
                    front = temp;
                } else {
                    current.prev.next = temp;
                } if (current.next == null) {
                    back = temp;
                } else {
                    current.next.prev = temp;
                }
            }
        }
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index >= size + 1) {
            throw new IndexOutOfBoundsException();
        } else if (index == size) {
            add(item);
            return;
        } else {
            if (isEmpty()) {
                front = new Node<T>(item);
            } else {
                Node<T> current = front;
                current = traverse(current, index);
                Node<T> temp = new Node<T>(current.prev, item, current);
                if (current.prev == null) {
                    front = temp;
                    current.prev = temp;
                }  else {
                    current.prev.next = temp;
                    current.prev = temp;
                }
            }
        }
        size++;
    }

    @Override
    public T delete(int index)  {
        if (index < 0 || index > this.size()) {
            throw new IndexOutOfBoundsException();
        } else if (isEmpty()) {
            throw new EmptyContainerException();
        } else {
            Node<T> current = front;
            if (index == 0) {
                front = current.next;
                current.prev = null;
                size--;
                return current.data;
            } else if (index == size - 1) {
                return remove();
            } else {
                current = traverse(current, index);
                Node<T> temp = current;
                current.prev.next = current.next;
                current.next.prev = current.prev;
                size--;
                return temp.data;
            }
        }
    }


    @Override
    public int indexOf(T item) {
        if (!isEmpty()) {
            Node<T> current = front;
            for (int i = 0; i < size; i++) {
                if (item != null && current != null && item.equals(current.data)) {
                    return i;
                } else if (item == null && current.data == null){
                    return i;
                }
                current = current.next;
            }
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(T other) {
        return (indexOf(other) != -1);
    }

    private Node<T> traverse(Node<T> current, int index) {
        if (size - index > index) {
            current = front;
            for (int i =0; i < index; i++) {
                current = current.next;
            }
            return current;
        } else {
            current = back;
            for (int i = size -1; i > index; i--) {
                current = current.prev;
            }
            return current;
        }
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return (current != null);
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node<T> toReturn = current;
            current = current.next;
            return toReturn.data;
        }
    }
}
