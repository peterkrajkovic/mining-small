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

/**
 *
 * @author jrabc
 */
public final class IntegerVector implements ConsolePrintable, List<Integer> {

    private int sum = 0;
    private int[] data = new int[100];
    private int size = 0;

    public IntegerVector() {
    }

    public int[] getData() {
        return data;
    }

    public IntegerVector(IntegerVector dv) {
        sum = dv.sum;
        size = dv.size;
        data = Arrays.copyOf(dv.data, dv.data.length);
    }

    public IntegerVector getDeepCopy() {
        IntegerVector dv = new IntegerVector();
        dv.sum = sum;
        dv.size = size;
        dv.data = Arrays.copyOf(data, data.length);
        return dv;
    }

    public void destroyData() {
        data = null;
        data = new int[0];
        trimToSize(0);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public void muli(int scalar) {
        for (int i = 0; i < data.length; i++) {
            int x = getNum(i) * scalar;
            setNum(i, x);
        }
    }

    public IntegerVector(int[] values) {
        data = values;
        size = data.length;
        sum = Arrays.stream(values).sum();
    }

    public int removeNum(int index) {
        int oldValue = getNum(size - 1);
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index,
                    numMoved);
        }
        data[--size] = 0;
        return oldValue;
    }

    public IntegerVector(List<Integer> values) {
        data = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            int x = values.get(i);
            addUnsafe(x);
        }
        size = values.size();
    }

    public IntegerVector(int capacity) {
        data = new int[capacity];
    }

    public int sum() {
        return sum;
    }

    public void addUnsafe(int n) {
        data[size++] = n;
        sum += n;
    }

    public void addUnsafeNoSum(int n) {
        data[size++] = n;
    }

    public void addNum(int... ints) {
        for (int x : ints) {
            this.addNum(x);
        }
    }

    public void addNum(int n) {
        if (data.length == size) {
            grow();
        }
        data[size++] = n;
        sum += n;
    }

    public int getNum(int i) {
        return data[i];
    }

    public IntegerVector setNum(int index, int value) {
        sum -= data[index];
        data[index] = value;
        sum += value;
        return this;
    }

    public IntegerVector dup() {
        IntegerVector out = new IntegerVector(data.length);
        System.arraycopy(data, 0, out.data, 0, data.length);
        out.sum = sum;
        out.size = size;
        return out;
    }

    /**
     * excel sumproduct
     *
     * @param other
     * @return
     */
    public int mulsum(IntegerVector other) {
        int sumproduct = 0;
        int a, b, d;
        for (int i = 0; i < size; i++) {
            a = other.getNum(i);
            b = getNum(i);
            d = b * a;
            sumproduct += d;
        }
        return sumproduct;
    }

    public IntegerVector sum(IntegerVector other) {
        int a, b;
        IntegerVector result = new IntegerVector(size);
        for (int i = 0; i < size; i++) {
            a = other.getNum(i);
            b = getNum(i);
            result.addUnsafe(a + b);
        }
        return result;
    }

    public IntegerVector sumi(IntegerVector other) {
        int a, b;
        for (int i = 0; i < size; i++) {
            a = other.getNum(i);
            b = getNum(i);
            this.setNum(i, a * b);
        }
        return this;
    }

    public IntegerVector mul(IntegerVector other) {
        IntegerVector result = new IntegerVector(size);
        int a, b, d;
        for (int i = 0; i < size; i++) {
            a = other.getNum(i);
            b = getNum(i);
            d = b * a;
            result.addUnsafe(d);
        }
        return result;
    }

    public int mulmulsum(IntegerVector other, IntegerVector other2) {
        int sumproduct = 0, a, b, c, d;
        for (int i = 0; i < size; i++) {
            a = other.getNum(i);
            b = getNum(i);
            c = other2.getNum(i);
            d = b * a * c;
            sumproduct += d;
        }
        return sumproduct;
    }

    public IntegerVector muli(IntegerVector other) {
        int d;
        for (int i = 0; i < size; i++) {
            d = getNum(i) * other.getNum(i);
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
     * @return number of inserted ints
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

    public static IntegerVector ones(int size) {
        IntegerVector m = new IntegerVector(size);
        for (int i = 0; i < size; i++) {
            m.addUnsafe(1);
        }
        return m;
    }

    public static IntegerVector vector(int... nums) {
        IntegerVector m = new IntegerVector(nums.length);
        for (int i = 0; i < nums.length; i++) {
            m.add(nums[i]);
        }
        return m;
    }

    public static IntegerVector zeros(int size) {
        IntegerVector dv = new IntegerVector(size);
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
        final IntegerVector other = (IntegerVector) obj;
        return Arrays.equals(this.data, other.data);
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            int d = data[i];
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
            if (data[i] == (Integer) d) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Itr();
    }

    @Override
    public <T extends Object> T[] toArray(T[] a) {
        Object[] arr = new Object[size];
        for (int i = 0; i < arr.length; i++) {
            a[i] = (T) new Integer(data[i]);
        }
        return a;
    }

    public int[] toArray(int[] a) {
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
    public boolean add(Integer e) {
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
            if (contains((Integer) object)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        for (Integer int1 : c) {
            addNum(int1);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Integer> c) {
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
    public Integer get(int index) {
        return data[index];
    }

    /**
     * the same result as get(index)
     *
     * @param index
     * @return
     */
    public int getPoint(int index) {
        return data[index];
    }

    @Override
    public Integer set(int index, Integer element) {
        int e = get(index);
        this.setNum(index, element);
        return e; //TODO check
    }

    @Override
    public void add(int index, Integer element) {
        data[index] = element;
    }

    @Override
    public Integer remove(int index) {
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
    public ListIterator<Integer> listIterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator<Integer> listIterator(int index) {
        ArrayList a = new ArrayList();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class Itr implements Iterator<Integer> {

        private int cursor;       // index of next element to return

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public Integer next() {
            return data[cursor++];
        }

        @Override
        public void remove() {
            removeNum(cursor - 1);
        }

    }

    public static int sumproduct(IntegerVector... dv) {
        int ret = 0, sum;
        int size = dv[0].size;
        for (int j = 0; j < size; j++) {
            sum = 0;
            for (IntegerVector dv1 : dv) {
                sum *= dv1.data[j];
            }
            ret += sum;
        }
        return ret;
    }

    public static int sumproduct(List<IntegerVector> dv) {
        int ret = 0, sum;
        int size = dv.get(0).size;
        for (int j = 0; j < size; j++) {
            sum = 1;
            for (IntegerVector dv1 : dv) {
                sum *= dv1.data[j];
            }
            ret += sum;
        }
        return ret;
    }

    public static int sumproduct(List<IntegerVector> dv, IntegerVector... aditional) {
        int ret = 0, sum;
        int size = dv.get(0).size;
        for (int j = 0; j < size; j++) {
            sum = 1;
            for (IntegerVector dv1 : dv) {
                sum *= dv1.data[j];
            }
            for (IntegerVector dv2 : aditional) {
                sum *= dv2.data[j];
            }
            ret += sum;
        }
        return ret;
    }

    public static IntegerVector getProductVector(List<IntegerVector> dv) {
        int a;
        int size = dv.get(0).size;
        IntegerVector ndv = new IntegerVector(size);
        for (int j = 0; j < size; j++) {
            a = 1;
            for (IntegerVector dv1 : dv) {
                a *= dv1.data[j];
            }
            ndv.addNum(a);
        }
        return ndv;
    }

    public static void main(String[] args) {
        IntegerVector dvSum = IntegerVector.vector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        // dvSum.remove(5);
        dvSum.print();
//        List a = Arrays.asList(10d, 2d, 3d);
//           System.out.println(dvSum);
//        dvSum.addAll(a);
//        dvSum.retainAll(a);
//        System.out.println(dvSum);
//        int[] ooo = new int[dvSum.size];
//        System.out.println(Arrays.toString(dvSum.toArray(ooo)));
//
//        System.out.println(getProductVector(Arrays.asList(dvSum, dvSum)));
//        System.out.println(sumproduct(Arrays.asList(dvSum, dvSum)));
//
//        System.out.println(sumproduct(Arrays.asList(dvSum, dvSum), dvSum));
        Iterator<Integer> d = dvSum.iterator();
        while (d.hasNext()) {
            Integer next = d.next();
            if (next == 9) {
                d.remove();
            }

        }
        dvSum.print();
    }

}
