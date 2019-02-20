package serialize;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;



public class Circular<E extends Serializable> implements Serializable, BlockingQueue<E> {

    private E[] bufor;
    public int readIndex;
    public int writeIndex;
    private int constSize; //parametr trzymajacy stala wielkosc bufora
    private int currentSize; //

    //konstruktor- tworze bufor o wielkosci size
    Circular(int size) {
        bufor = (E[]) new Serializable[size];
        constSize = size;
        readIndex = 0;
        writeIndex = 0;
        currentSize = 0;
    }

    // bofor pelny- ilsc danych zapisanych = wielkosc bufora
    boolean isFull() {
        if (currentSize == constSize)
            return true;
        return false;
    }

    //bufor pusty- nie ma zapisanych danych w buforze
    @Override
    public boolean isEmpty() {
        if (currentSize == 0)
            return true;
        return false;
    }

    @Override
    public int size() {
        return currentSize;
    }

    public int capacity() {
        return constSize;
    }



    // tworzenie nowej tablicy
    @Override
    public Object[] toArray() {
        int temp;
        if (readIndex > writeIndex - 1)
            temp = writeIndex - 1 - readIndex + constSize;
        else
            temp = writeIndex - 1
                    - readIndex;
        E[] array = (E[]) java.lang.reflect.Array.newInstance(bufor.getClass().getComponentType(), temp + 1);
        int tempIndex = readIndex;
        for (int j = 0; j < currentSize; j++) {
            int modulo = (tempIndex - readIndex) % constSize;
            modulo += modulo < 0 ? constSize : 0;
            array[modulo] = bufor[tempIndex];
            tempIndex = (tempIndex + 1) % constSize;
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        T[] temporaryArray = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), currentSize);
        int index = readIndex;
        int elements = 0;
        while (elements < currentSize) {
            if (index == constSize)
                index = 0;

            temporaryArray[elements] = (T) bufor[index++];
            a[elements] = temporaryArray[elements];
            elements++;
        }
        return temporaryArray;
    }

    public E[] fromArray(Object[] a) {
        E[] temporaryArray = (E[]) (new Serializable[constSize]);
        for (int i = 0; i < constSize; i++) {
            int index = (i + readIndex) % constSize;
            if (i < a.length)
                temporaryArray[index] = (E) (a[i]);
            else
                temporaryArray[index] = null;
        }
        return temporaryArray;
    }

    @Override
    public Iterator<E> iterator() {
        return new CircularIterator(this);
    }

    @Override
    public synchronized E take() throws InterruptedException {
        E temp = null;
        if (!isEmpty()) {
            if (readIndex == constSize)
                readIndex = 0;
            temp = bufor[readIndex];
            bufor[readIndex++] = null;
            --currentSize;
        }
        return temp;
    }

    @Override
    public synchronized E poll(long timeout, TimeUnit unit) throws InterruptedException {
        if (bufor[readIndex] == null) {
            throw new NullPointerException();
        }
        long millis = unit.toMillis(timeout);
        Thread.sleep(millis);
        E headOfQueue = bufor[readIndex];
        bufor[readIndex] = null;
        readIndex = increment(readIndex);
        --currentSize;
        return headOfQueue;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public synchronized boolean remove(Object o) {
        for (E element : bufor) {
            if (element.equals(o)) {
                element = null;
                --currentSize;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }

    public synchronized E getItem(int i) {
        return bufor[i];
    }

    @Override
    public synchronized boolean add(E e) {
        return offer(e);
    }


    private int increment(int index){
        return (++index == constSize) ? 0 : index;
    }

    private int decrement(int index){
        return ((index == 0) ? bufor.length : index) - 1;
    }

    private void insert(E e){
        bufor[writeIndex] = e;
        writeIndex = increment(writeIndex);
        ++currentSize;
    }

    @Override
    public synchronized boolean offer(E e) {
        if(e == null){
            throw new NullPointerException();
        }
        if(currentSize == constSize){
            return false;
        }else{
            insert(e);
            return true;
        }
    }

    @Override
    public synchronized E remove() {
        return poll();
    }

    @Override
    public synchronized E poll() {
        if (bufor[readIndex] == null) {
            throw new NullPointerException();
        }

        if (isEmpty()) {
            return null;
        }

        E headOfQueue = bufor[readIndex];
        bufor[readIndex] = null;
        --currentSize;
        readIndex = increment(readIndex);
        return headOfQueue;
    }

    @Override
    public E element() {
        return null;
    }

    @Override
    public synchronized E peek() {
        E temp = bufor[readIndex];
        bufor[readIndex] = null;
        readIndex = decrement(readIndex);
        --currentSize;
        return temp;
    }

    @Override
    public synchronized void put(E value) throws InterruptedException {
        if (!isFull()) {
            if (writeIndex == constSize)
                writeIndex = 0;

            bufor[writeIndex] = value;
            writeIndex++;
            currentSize++;
        } else
            throw new IndexOutOfBoundsException("Array is full");
    }

    @Override
    public synchronized boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if(e == null){
            throw new NullPointerException();
        }
        long millis = unit.toMillis(timeout);
        Thread.sleep(millis);
        if(isFull()){
            return false;
        }else{
            insert(e);
            return true;
        }
    }


    public void readObject(ObjectInputStream input) throws IOException,
            ClassNotFoundException {

        constSize = (int) input.readObject();
        bufor = this.fromArray((E[]) input.readObject());

        readIndex = (int) input.readObject();
        writeIndex = (int) input.readObject();
        currentSize = (int) input.readObject();
    }

    public void writeObject(ObjectOutputStream output) throws IOException {
        output.writeObject(constSize);
        output.writeObject(toArray());

        output.writeObject(readIndex);
        output.writeObject(writeIndex);
        output.writeObject(currentSize);
    }

    //konwersja elementu bufora na string
    public String toString() {
        String element = "";
        String[] array = new String[currentSize];
        array = toArray(array);
        int index = readIndex;
        for (String s : array) {
            if (index == constSize)
                index = 0;
            int distanceToHead = Math.abs(writeIndex - index);
            element += index + "." + " head distance: " + distanceToHead + ", value: ";
            if (s == null)
                element += "NULL\n";
            else
                element += s + "\n";
            index++;
        }
        return element;
    }

    class CircularIterator implements Iterator<E> {
        private int readIterator;
        private int writeIterator;
        private int visited = 0;

        CircularIterator(Circular<E> circularObject) {
            readIterator = circularObject.readIndex;

        }


        @Override
        public boolean hasNext() {
            if (readIterator == constSize)
                readIterator = 0;
            return bufor[readIterator] != null & visited < currentSize;
        }

        //zwraca nastepny element bufora
        @Override
        public E next() {
            visited++;
            return bufor[readIterator++];
        }
    }
}
