/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.structures;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import projectutils.ConsolePrintable;
import projectutils.MemoryUtils;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 *
 * @author jrabc
 */
public final class DoubleVector extends Vector implements ConsolePrintable {

    private double sum = 0;
    private double[] data = new double[100];
    private int size = 0;

    public DoubleVector() {
    }

    @Deprecated
    /**
     * usage of this method is dangerous.
     */
    public double[] getData() {
        return data;
    }

    public long getMemorySize() {
        return 64 + MemoryUtils.sizeOf(data);
    }

    public DoubleVector(DoubleVector dv) {
        sum = dv.sum;
        size = dv.size;
        data = Arrays.copyOf(dv.data, dv.data.length);
    }

    public DoubleVector getDeepCopy() {
        DoubleVector dv = new DoubleVector();
        dv.sum = sum;
        dv.size = size;
        dv.data = Arrays.copyOf(data, data.length);
        return dv;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public void muli(double scalar) {
        final int length = size - 1;
        for (int i = length; i >= 0; i--) {
            double x = getNum(i) * scalar;
            setNum(i, x);
        }
    }

    public DoubleVector(double[] values) {
        data = values;
        size = data.length;
        sum = Arrays.stream(values).sum();
    }

    public double removeNum(int index) {
        double oldValue = getNum(size - 1);
        sum -= oldValue;
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index,
                    numMoved);
        }
        data[--size] = 0;
        return oldValue;
    }

    public DoubleVector(List<Double> values) {
        data = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            double x = values.get(i);
            addUnsafe(x);
        }
        size = values.size();
    }

    public DoubleVector(int capacity) {
        data = new double[capacity];
    }

    public double sum() {
        return sum;
    }

    public void addUnsafe(double n) {
        data[size++] = n;
        sum += n;
    }

    public void addUnsafeNoSum(double n) {
        data[size++] = n;
    }

    public void addNum(double... doubles) {
        for (double x : doubles) {
            this.addNum(x);
        }
    }

    public void addNum(double n) {
        if (data.length == size) {
            grow();
        }
        data[size++] = n;
        sum += n;
    }

    public final double getNum(int i) {
        return data[i];
    }

    public final Number getNumDefault(int i) {
        return data[i];
    }

    public DoubleVector setNum(int i, double v) {
        sum -= data[i];
        data[i] = v;
        sum += v;
        return this;
    }

    public DoubleVector dup() {
        DoubleVector out = new DoubleVector(data.length);
        System.arraycopy(data, 0, out.data, 0, data.length);
        out.sum = sum;
        out.size = size;
        return out;
    }

    public DoubleVector muli(Vector other) {
        double d;
        for (int i = 0; i < size; i++) {
            d = getNum(i) * other.get(i);
            this.setNum(i, d);
        }
        return this;
    }

    /**
     *
     * @return allocated getCapacity of data array
     */
    public int getCapacity() {
        return data.length;
    }

    /**
     *
     * @return number of inserted doubles
     */
    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        size = 0;
        sum = 0;
    }

    public static DoubleVector ones(int size) {
        DoubleVector m = new DoubleVector(size);
        for (int i = 0; i < size; i++) {
            m.addUnsafe(1.0);
        }
        return m;
    }

    public static DoubleVector vector(double... nums) {
        DoubleVector m = new DoubleVector(nums.length);
        for (int i = 0; i < nums.length; i++) {
            m.add(nums[i]);
        }
        return m;
    }

    public static DoubleVector zeros(int size) {
        DoubleVector dv = new DoubleVector(size);
        dv.size = size;
        return dv;
    }

    private void grow() {
        int oldCapacity = data.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        data = Arrays.copyOf(data, newCapacity);
    }

    private void ensureGrow(int requestedCapacity) {
        if (size < requestedCapacity) {
            data = Arrays.copyOf(data, requestedCapacity);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DoubleVector other = (DoubleVector) obj;
        return Arrays.equals(this.data, other.data);
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            double d = data[i];
            sb.append(d).append(", ");
        }
        sb.append(data[size - 1]);
        return sb.toString();
    }

    @Deprecated
    @Override
    public void trimToSize(int newSize) {
        size = newSize;
    }

    public double[] getArray() {
        if (data.length != size) {
            data = Arrays.copyOf(data, size);
        }
        return data;
    }

    @Override
    public boolean contains(Object d) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == (Double) d) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Double> iterator() {
        return new Itr();
    }

    @Override
    public <T extends Object> T[] toArray(T[] a) {
        Object[] arr = new Object[size];
        for (int i = 0; i < arr.length; i++) {
            a[i] = (T) new Double(data[i]);
        }
        return a;
    }

    public double[] toArray(double[] a) {
        return Arrays.copyOf(data, size);
    }

    @Override
    public Double[] toArray() {
        Double[] arr = new Double[size];
        for (int i = 0; i < size; i++) {
            arr[i] = data[i];
        }
        return arr;
    }

    @Override
    public boolean add(Double e) {
        this.addNum(e);
        return true;
    }

    @Override
    public boolean remove(Object index) {
        this.removeNum((int) index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object object : c) {
            if (contains((Double) object)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Double> c) {
        for (Double double1 : c) {
            addNum(double1);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Double> c) {
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureGrow(size + numNew);  // Increments modCount
        System.arraycopy(a, 0, data, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, false);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, true);
    }

    private boolean batchRemove(Collection<?> c, boolean complement) {
        int r = 0, w = 0;
        boolean modified = false;
        try {
            for (; r < size; r++) {
                if (c.contains(data[r]) == complement) {
                    data[w++] = data[r];
                } else {
                    sum -= data[r];
                }
            }
        } finally {
            if (r != size) {
                System.arraycopy(data, r,
                        data, w,
                        size - r);
                w += size - r;
            }
            if (w != size) {
                size = w;
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public Double get(int index) {
        return data[index];
    }

    public Double getLast() {
        return data[size - 1];
    }

    public Double getFirst() {
        return data[0];
    }

    @Override
    public Double set(int index, Double element) {
        double e = data[index];
        this.setNum(index, element);
        return e;
    }

    @Override
    public void add(int index, Double element) {
        if (data.length == size) {
            grow();
        }
        sum += element;
        System.arraycopy(data, index, data, index + 1,
                size - index);
        size++;
        data[index] = element;
    }

    @Override
    public Double remove(int index) {
        return this.removeNum(index);
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (o.equals(data[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            if (o.equals(data[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<Double> listIterator() {
        return new ListItr(0);
    }

    private class ListItr extends Itr implements ListIterator<Double> {

        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Double previous() {
            int i = cursor - 1;
            if (i < 0) {
                throw new NoSuchElementException();
            }
            if (i >= size) {
                throw new ConcurrentModificationException();
            }
            cursor = i;
            return data[lastRet = i];
        }

        public void set(Double e) {
            DoubleVector.this.set(lastRet, e);
        }

        public void add(Double e) {

            try {
                int i = cursor;
                DoubleVector.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public ListIterator<Double> listIterator(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        return new ListItr(index);
    }

    @Override
    public List<Double> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class Itr implements Iterator<Double> {

        protected int lastRet = -1;
        protected int cursor;       // index of next element to return

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public Double next() {
            return data[lastRet = cursor++];
        }

        @Override
        public void remove() {
            lastRet = -1;
            removeNum(cursor - 1);
        }

    }

    public void destroyData() {
        data = new double[0];
        trimToSize(0);
    }

    public static void main(String[] args) {
        FloatVector dvSum = FloatVector.vector(0, Double.NaN, 2, 3, 4, 5, 6, 7, 8, 9);
        dvSum.trimToSize(2);
        dvSum.print();
    }

}
