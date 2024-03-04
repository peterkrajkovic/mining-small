/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import static projectutils.ProjectUtils.formatDouble;
import static projectutils.ProjectUtils.round;

/**
 *
 * @author rabcan
 */
public class ListUtils {

    public static <T> void removeFromIndex(List<T> list, int index, Consumer<T> c) {
        while (list.size() > index) {
            c.accept(list.get(index));
            list.remove((int) index);
        }
    }

    public static void removeFromIndex(ArrayList list, int index) {
        while (list.size() > index) {
            list.remove((int) index);
        }
    }

    public static void removeFromIndex(Deque list, int index) {
        while (list.size() > index) {
            list.removeLast();
        }
    }

    public static String listToString(List<Double> list) {
        String bi = "[";
        for (int i = 0; i < list.size(); i++) {
            final Double x = list.get(i);
            if (i < list.size() - 1) {
                bi += formatDouble(round(x, 3)) + ", ";
            } else {
                bi += formatDouble(round(x, 3));
            }
        }
        return bi + "]";
    }

    public static String listToString(List list, String separator) {
        String bi = "";
        for (int i = 0; i < list.size(); i++) {
            final Object x = list.get(i);
            if (i < list.size() - 1) {
                bi += x + separator;
            } else {
                bi += x;
            }
        }
        return bi;
    }

    public static int getMaxValueIndex(List<Double> val) {
        int i = -1;
        Double max = Double.NEGATIVE_INFINITY;
        for (int k = 0; k < val.size(); k++) {
            Double d = val.get(k);
            if (d > max) {
                i = k;
                max = d;
            }
        }
        return i;
    }

    public static void main(String[] args) {
        LinkedList list = new LinkedList();
        int i = 0;
        list.add(i++);
        list.add(i++);
        list.add(i++);
        list.add(i++);
        list.add(i++);
        list.add(i++);
        list.add(i++);
        list.add(i++);
        System.out.println(list);
        removeFromIndex(list, 4);
        System.out.println(list);
    }

}
