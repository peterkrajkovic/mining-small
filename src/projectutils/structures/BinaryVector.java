/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.structures;

import projectutils.ConsolePrintable;
import projectutils.MemoryUtils;
import projectutils.ProjectUtils;
import static projectutils.ProjectUtils.booleanToDouble;
import static projectutils.ProjectUtils.booleanToInt;
import static projectutils.ProjectUtils.doubleToBoolean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public final class BinaryVector extends Vector implements ConsolePrintable {

    private double sum = 0;
    private boolean[] data = new boolean[100];
    private int size = 0;

    public BinaryVector() {
    }

    public boolean[] getData() {
        return data;
    }

    @Override
    public long getMemorySize() {
        return 64 + MemoryUtils.sizeOf(data);
    }

    public BinaryVector(BinaryVector dv) {
        sum = dv.sum;
        size = dv.size;
        data = Arrays.copyOf(dv.data, dv.data.length);
    }

    @Override
    public BinaryVector getDeepCopy() {
        BinaryVector dv = new BinaryVector();
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

    public BinaryVector(boolean... values) {
        data = values;
        size = data.length;
        sum = ProjectUtils.sum(this);
    }

    public double removeNum(int index) {
        double oldValue = getNum(size - 1);
        if (data[index] == true) {
            sum -= 1;
        }
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index,
                    numMoved);
        }
        data[--size] = false;
        return oldValue;
    }

    public BinaryVector(List<Boolean> values) {
        data = new boolean[values.size()];
        for (Boolean value : values) {
            addUnsafe(value);
        }
        size = values.size();
    }

    public BinaryVector(int capacity) {
        data = new boolean[capacity];
    }

    public final double sum() {
        return sum;
    }

    public final void addUnsafe(boolean n) {
        data[size++] = n;
        if (n) {
            sum += 1;
        }
    }

    public final void addUnsafeNoSum(boolean n) {
        data[size++] = n;
    }

    @Override
    public final void addNum(double... doubles) {
        for (double x : doubles) {
            this.addNum(x);
        }
    }

    @Override
    public final void addNum(double n) {
        if (data.length == size) {
            grow();
        }
        final boolean doubleToBoolean = doubleToBoolean(n);
        data[size++] = doubleToBoolean;
        if (doubleToBoolean) {
            sum += 1;
        }
    }

    public final void addNum(boolean n) {
        if (data.length == size) {
            grow();
        }
        final boolean doubleToBoolean = n;
        data[size++] = doubleToBoolean;
        if (doubleToBoolean) {
            sum += 1;
        }
    }

    @Override
    public final double getNum(int i) {
        return data[i] ? 1 : 0;
    }

    @Override
    public final Number getNumDefault(int i) {
        return data[i] ? 1 : 0;
    }

    @Override
    public BinaryVector setNum(int i, double v) {
        return this.setNum(i, doubleToBoolean(v));
    }

    public BinaryVector setNum(int i, boolean v) {
        if (data[i]) {
            sum -= 1;
        }
        data[i] = v;
        if (v) {
            sum += 1;
        }
        return this;
    }

    public final BinaryVector dup() {
        BinaryVector out = new BinaryVector(data.length);
        System.arraycopy(data, 0, out.data, 0, data.length);
        out.sum = sum;
        out.size = size;
        return out;
    }

    /**
     *
     * @return allocated getCapacity of data array
     */
    public final int getCapacity() {
        return data.length;
    }

    /**
     *
     * @return number of inserted doubles
     */
    @Override
    public final int size() {
        return size;
    }

    @Override
    public final void clear() {
        size = 0;
        sum = 0;
    }

    public final static BinaryVector ones(int size) {
        BinaryVector m = new BinaryVector(size);
        for (int i = 0; i < size; i++) {
            m.addUnsafe(true);
        }
        return m;
    }

    public final static BinaryVector vector(double... nums) {
        BinaryVector m = new BinaryVector(nums.length);
        for (int i = 0; i < nums.length; i++) {
            m.add(nums[i]);
        }
        return m;
    }

    public final static BinaryVector vector(boolean... nums) {
        BinaryVector m = new BinaryVector(nums.length);
        for (int i = 0; i < nums.length; i++) {
            m.addUnsafe(nums[i]);
        }
        return m;
    }

    public final static BinaryVector zeros(int size) {
        BinaryVector dv = new BinaryVector(size);
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
        final BinaryVector other = (BinaryVector) obj;
        return Arrays.equals(this.data, other.data);
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            sb.append(booleanToInt(data[i])).append(", ");
        }
        sb.append(booleanToInt(data[size - 1]));
        return sb.toString();
    }

    @Override
    public final void trimToSize(int newSize) {
        size = newSize;
    }

    @Override
    public final boolean contains(Object d) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == (Boolean) d) {
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
            a[i] = (T) new Boolean(data[i]);
        }
        return a;
    }

    public boolean[] toArray(double[] a) {
        return Arrays.copyOf(data, size);
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        for (int i = 0; i < arr.length; i++) {
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
    /**
     * Collection c must contains boolean values. NOT DOUBLES
     */
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, false);
    }

    @Override
    /**
     * Collection c must contains boolean values. NOT DOUBLES
     */
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
                    if (data[r]) {
                        sum--;
                    }
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

    private static final Double ONE = 1d;
    private static final Double ZERO = 0d;

    @Override
    public Double get(int index) {
        if (data[index]) {
            return ONE;
        } else if (!data[index]) {
            return ZERO;
        } else {
            return Double.NaN;
        }
    }

    public Double getLast() {
        return booleanToDouble(data[size - 1]);
    }

    public Double getFirst() {
        return booleanToDouble(data[0]);
    }

    @Override
    public Double set(int index, Double element) {
        double e = get(index);
        this.setNum(index, element);
        return e; //TODO check
    }

    @Override
    public void add(int index, final Double element) {
        if (data.length == size) {
            grow();
        }
        final boolean boolelement = doubleToBoolean(element);
        if (boolelement) {
            sum += 1;
        }
        System.arraycopy(data, index, data, index + 1,
                size - index);
        size++;
        data[index] = boolelement;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator<Double> listIterator(int index) {
        ArrayList a = new ArrayList();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Double> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class Itr implements Iterator<Double> {

        private int cursor;       // index of next element to return

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public Double next() {
            return booleanToDouble(data[cursor++]);
        }

        @Override
        public void remove() {
            removeNum(cursor - 1);
        }

    }

    public void destroyData() {
        data = new boolean[0];
        trimToSize(0);
    }

    public static void main(String[] args) {
        DoubleVector dv = DoubleVector.vector(0, 1, 0, 0, 1, 0, 1, 0, 0, 1);
        BinaryVector dvSum = BinaryVector.vector(0, 1, 0, 0, 1, 0, 1, 0, 0, 1);
        DoubleVector d = dvSum.sum(dv);
        //   d.print();
        //   dv.remove(1);
        dvSum.print();
        dvSum.add(0, 0.5d);
        dvSum.print();
        System.out.println(dv.sum());

        BinaryVector v = new BinaryVector();
        v.add(Double.NaN);
        v.print();
//        List a = Arrays.asList(10d, 2d, 3d);
//           System.out.println(dvSum);
//        dvSum.addAll(a);
//        dvSum.retainAll(a);
//        System.out.println(dvSum);
//        double[] ooo = new double[dvSum.size];
//        System.out.println(Arrays.toString(dvSum.toArray(ooo)));
//
//        System.out.println(getProductVector(Arrays.asList(dvSum, dvSum)));
//        System.out.println(sumproduct(Arrays.asList(dvSum, dvSum)));
//
//        System.out.println(sumproduct(Arrays.asList(dvSum, dvSum), dvSum));
//        Iterator<Double> d = dvSum.iterator();
//        while (d.hasNext()) {
//            Double next = d.next();
//            if (next == 0) {
//                d.remove();
//            }
//        }
//        dvSum.print();
    }
}
