/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.structures;

import projectutils.StopWatch;
import java.util.Arrays;
import java.util.List;
import projectutils.ProjectUtils;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public abstract class Vector implements List<Double> {

    public abstract <T extends Vector> T getDeepCopy();

    public abstract double sum();

    public abstract void addNum(double... doubles);

    public abstract void addNum(double n);

    public abstract double getNum(int index);

    public abstract Number getNumDefault(int i);

    public abstract int getCapacity();

    public abstract double removeNum(int index);

    public abstract <T extends Vector> T setNum(int i, double v);

    public abstract <T extends Vector> T dup();

    public abstract void trimToSize(int newSize);

    public abstract void destroyData();

    public double[] copyToArray() {
        double[] arr = new double[size()];
        for (int i = 0; i < size(); i++) {
            arr[i] = getNum(i);
        }
        return arr;
    }

    public abstract long getMemorySize();

    public DoubleVector mul(Vector other) {
        DoubleVector result = new DoubleVector(size());
        double a, b, d;
        for (int i = 0; i < size(); i++) {
            a = other.get(i);
            b = get(i);
            d = b * a;
            result.addUnsafe(d);
        }
        return result;
    }

    public DoubleVector fuzzyUnion(Vector other) {
        DoubleVector result = new DoubleVector(size());
        double a, b, d;
        for (int i = 0; i < size(); i++) {
            a = other.get(i);
            b = get(i);
            d = Math.min(a, b);
            result.addUnsafe(d);
        }
        return result;
    }

    public Vector unionIn(Vector other) {
        double d;
        for (int i = 0; i < size(); i++) {
            d = Math.min(getNum(i), other.get(i));
            this.setNum(i, d);
        }
        return this;
    }

    public double sumproductIn(Vector other, Vector other2) {
        double sumproduct = 0;
        for (int i = 0; i < size(); i++) {
            sumproduct += other.get(i) * get(i) * other2.get(i);
        }
        return sumproduct;
    }

    public double sumUnionIn(Vector other, Vector other2) {
        double sumproduct = 0;
        for (int i = 0; i < size(); i++) {
            sumproduct += ProjectUtils.min(other.get(i), get(i), other2.get(i));
        }
        return sumproduct;
    }

    public DoubleVector fuzzyUnionSum(Vector other) {
        DoubleVector result = new DoubleVector(size());
        double a, b, d;
        for (int i = 0; i < size(); i++) {
            a = other.get(i);
            b = get(i);
            d = Math.min(a, b);
            result.addUnsafe(d);
        }
        return result;
    }

    /**
     * excel sumproduct
     *
     * @param other
     * @return
     */
    public double sumproductIn(Vector other) {
        double sumproduct = 0;
        double a, b, d;
        for (int i = 0; i < size(); i++) {
            a = other.get(i);
            b = get(i);
            d = b * a;
            sumproduct += d;
        }
        return sumproduct;
    }

    public double sumUnionIn(Vector other) {
        double sumproduct = 0;
        double a, b, d;
        for (int i = 0; i < size(); i++) {
            a = other.get(i);
            b = get(i);
            d = Math.min(a, b);
            sumproduct += d;
        }
        return sumproduct;
    }

    public DoubleVector sum(Vector other) {
        double a, b;
        DoubleVector result = new DoubleVector(size());
        for (int i = 0; i < size(); i++) {
            a = other.get(i);
            b = get(i);
            result.addUnsafe((a + b));
        }
        return result;
    }

    public static void muli(DoubleVector dv, double scalar) {
        final int length = dv.size() - 1;
        for (int i = length; i >= 0; i--) {
            double x = dv.getNum(i) * scalar;
            dv.setNum(i, x);
        }
    }

    public static DoubleVector getProductVector(List<Vector> dv) {
        double a;
        int size = dv.get(0).size();
        DoubleVector ndv = new DoubleVector(size);
        for (int j = 0; j < size; j++) {
            a = 1;
            for (Vector dv1 : dv) {
                a *= dv1.getNum(j);
            }
            ndv.addNum(a);
        }
        return ndv;
    }

    public static double sumproduct(Vector... dv) {
        final int size = dv[0].size();
        double ret = 0, sum;
        int i;
        for (int j = 0; j < size; j++) {
            sum = dv[0].getNum(j);
            for (i = 1; i < dv.length; i++) {
                sum *= dv[i].getNum(j);
            }
            ret += sum;
        }
        return ret;
    }

    public static double sumproduct(List<Vector> dv) {
        double ret = 0, sum;
        int size = dv.get(0).size();
        for (int j = 0; j < size; j++) {
            sum = 1;
            for (Vector dv1 : dv) {
                sum *= dv1.getNum(j);
            }
            ret += sum;
        }
        return ret;
    }

    public static double sumproduct(List<Vector> dv, Vector... aditional) {
        double ret = 0, sum;
        int size = dv.get(0).size();
        for (int j = 0; j < size; j++) {
            sum = 1;
            for (Vector dv1 : dv) {
                sum *= dv1.get(j);
            }
            for (Vector dv2 : aditional) {
                sum *= dv2.get(j);
            }
            ret += sum;
        }
        return ret;
    }

    public static void main(String[] a) {
        BinaryVector bv = new BinaryVector();
        bv.addNum(true);
        int c = 50000000;
//        {
//            StopWatch sw = new StopWatch();
//            DoubleVector dv = new DoubleVector(c);
//            for (int i = 0; i < c; i++) {
//                dv.add(Math.random());
//            }
//            System.out.print("DoubleVector ADD:  ");
//            sw.printTimeAndReset();
//            for (int i = 0; i < c; i++) {
//                double d = dv.get(i);
//            }
//            System.out.print("DoubleVector GET:  ");
//            sw.printTime();
//        }
//        {
//            StopWatch sw = new StopWatch();
//            BinaryVector dv = new BinaryVector(c);
//            for (int i = 0; i < c; i++) {
//                dv.add(Math.random());
//            }
//            System.out.print("BinaryVector ADD:  ");
//            sw.printTimeAndReset();
//            for (int i = 0; i < c; i++) {
//                double d = dv.get(i);
//            }
//            System.out.print("BinaryVector GET:  ");
//            sw.printTime();
//        }
//        {
//            StopWatch sw = new StopWatch();
//            FloatVector dv = new FloatVector(c);
//            for (int i = 0; i < c; i++) {
//                dv.add(Math.random());
//            }
//            System.out.print("FloatVector ADD:  ");
//            sw.printTimeAndReset();
//            for (int i = 0; i < c; i++) {
//                double d = dv.get(i);
//            }
//            System.out.print("FloatVector GET:  ");
//            sw.printTime();
//        }
        {
            FloatVector fl = new FloatVector(c);
            DoubleVector db = new DoubleVector(c);
            BinaryVector bin = new BinaryVector(c);
            for (int i = 0; i < c; i++) {
                fl.add(Math.random());
                db.add(Math.random());
                bin.add(Math.random());
            }
            StopWatch sw = new StopWatch();
//            Vector.sumproduct(db, db);
//            sw.printTimeAndReset("Sumproduct DV, DV");
//            Vector.sumproduct(bin, fl);
//            sw.printTimeAndReset("Sumproduct bin, FV");
//            Vector.sumproduct(db, fl);
//            sw.printTimeAndReset("Sumproduct DV, FV");
//            Vector.sumproduct(fl, fl);
//            sw.printTimeAndReset("Sumproduct FV, FV");
//            Vector.sumproduct(bin, db);
//            sw.printTimeAndReset("Sumproduct bin, DV");
//            Vector.sumproduct(bin, bin);
//            sw.printTimeAndReset("Sumproduct bin, bin");
//            Vector.sumproduct(bin, db, fl);
            sw.printTimeAndReset("Sumproduct bin, DV, FV");
        }
        {
//            Vector db = new FloatVector(c);
//            for (int i = 0; i < c; i++) {
//                db.add(Math.random());
//            }
//            StopWatch sw = new StopWatch();
//            System.out.print("Sumproduct DV, DV:  ");
//            System.out.println(Vector.sumproduct(db, db, db, db, db));
//            sw.printTimeAndReset();
//            System.out.print("Sumproduct doublematrix:  ");
//            sw.printTimeAndReset();
        }

    }

}
