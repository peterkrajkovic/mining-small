/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import projectutils.ConsolePrintable;
import projectutils.MemoryUtils;
import projectutils.ProjectUtils;

/**
 *
 * @author jrabc
 */
public final class FloatVector extends Vector implements ConsolePrintable {

    private double sum = 0;
    private float[] data = new float[100];
    private int size = 0;

    public static FloatVector ones(int size) {
        FloatVector m = new FloatVector(size);
        for (int i = 0; i < size; i++) {
            m.addUnsafe(1.0f);
        }
        return m;
    }

    public static FloatVector vector(double... nums) {
        FloatVector m = new FloatVector(nums.length);
        for (int i = 0; i < nums.length; i++) {
            m.addNum(nums[i]);
        }
        return m;
    }

    public static FloatVector vector(float... nums) {
        FloatVector m = new FloatVector(nums.length);
        for (int i = 0; i < nums.length; i++) {
            m.addNum(nums[i]);
        }
        return m;
    }

    public static FloatVector zeros(int size) {
        FloatVector dv = new FloatVector(size);
        dv.size = size;
        return dv;
    }

    public FloatVector() {
    }

    public float[] getData() {
        return data;
    }

    public FloatVector(FloatVector dv) {
        sum = dv.sum;
        size = dv.size;
        data = Arrays.copyOf(dv.data, dv.data.length);
    }

    public FloatVector getDeepCopy() {
        FloatVector dv = new FloatVector();
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

    public FloatVector(float[] values) {
        data = values;
        size = data.length;
        sum = ProjectUtils.sum(this);
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

    public FloatVector(List<Double> values) {
        data = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            double x = values.get(i);
            addUnsafe((float) x);
        }
        size = values.size();
    }

    public FloatVector(int capacity) {
        data = new float[capacity];
    }

    public double sum() {
        return sum;
    }

    public void addUnsafe(float n) {
        data[size++] = n;
        sum += n;
    }

    public void addUnsafeNoSum(float n) {
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
        data[size++] = (float) n;
        sum += n;
    }

    public void addNum(float n) {
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

    public FloatVector setNum(int i, double v) {
        sum -= data[i];
        data[i] = (float) v;
        sum += v;
        return this;
    }

    public FloatVector setNum(int i, float v) {
        sum -= data[i];
        data[i] = v;
        sum += v;
        return this;
    }

    public FloatVector dup() {
        FloatVector out = new FloatVector(data.length);
        System.arraycopy(data, 0, out.data, 0, data.length);
        out.sum = sum;
        out.size = size;
        return out;
    }

    public Vector sumi(Vector other) {
        double a, b;
        for (int i = 0; i < size; i++) {
            a = other.get(i);
            b = getNum(i);
            this.setNum(i, a * b);
        }
        return this;
    }

    public Vector muli(Vector other) {
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
        final FloatVector other = (FloatVector) obj;
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

    public void trimToSize(int newSize) {
        size = newSize;
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

    public float[] toArray(double[] a) {
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
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, false);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, true);
    }

    @Override
    public Double get(int index) {
        return (double) data[index];
    }

    public Double getLast() {
        return (double) data[size - 1];
    }

    public Double getFirst() {
        return (double) data[0];
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
        data[index] = element.floatValue();
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
            return (double) data[cursor++];
        }

        @Override
        public void remove() {
            removeNum(cursor - 1);
        }

    }

    public long getMemorySize() {
        return 64 + MemoryUtils.sizeOf(data);
    }

    public void destroyData() {
        data = null;
        data = new float[0];
        trimToSize(0);
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

    public static void main(String[] args) {
        FloatVector dvSum = FloatVector.vector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        dvSum.muli(2);
        dvSum.print();

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
