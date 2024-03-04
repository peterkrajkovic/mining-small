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

/**
 *
 * @author jrabc
 */
public class BoxedDoubleArray implements List<Double> {

    private double[] arr;

    public BoxedDoubleArray(int capacity) {
        this.arr = new double[capacity];
    }

    public BoxedDoubleArray(double... arr) {
        this.arr = arr;
    }

    public void setArr(double[] arr) {
        this.arr = arr;
    }

    public double[] getArr() {
        return arr;
    }

    @Override
    public int size() {
        return arr.length;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public void incrementOn(int i) {
        arr[i]++;
    }

    public void decrementOn(int i) {
        arr[i]++;
    }

    public void divAllByScalar(double scalar) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] /= scalar;
        }
    }

    public void mulAllByScalar(double scalar) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] /= scalar;
        }
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < arr.length; i++) {
            double d = arr[i];
            if (Double.compare((double) o, d) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < arr.length;
            }

            @Override
            public Double next() {
                return arr[i++];
            }
        };
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(Double e) {
        throw new UnsupportedOperationException("Not supported. Use set method"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection<? extends Double> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(int index, Collection<? extends Double> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double get(int index) {
        return arr[index];
    }

    @Override
    public Double set(int index, Double element) {
        arr[index] = element;
        return element;
    }

    @Override
    public void add(int index, Double element) {
        arr[index] = element;
    }

    @Override
    public Double remove(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator<Double> listIterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator<Double> listIterator(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Double> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return Arrays.toString(arr);
    }

}
