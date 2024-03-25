/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jrabc
 */
public class ArrayUtils {

    public static void printList(List list, String separator) {
        for (Object object : list) {
            System.out.print(object.toString() + separator);
        }
    }

    public static List stringToDouble(List<String> list) {
        List res = new ArrayList<>();
        for (String object : list) {
            res.add(Double.parseDouble(object));
        }
        return res;
    }

    public static List<Integer> makeIntegerSequence(int begin, int end) {
        List<Integer> ret = new ArrayList<Integer>(++end - begin);

        for (; begin < end;) {
            ret.add(begin++);
        }

        return ret;
    }

    public static int[] makeIntSequence(int begin, int end) {
        if (end < begin) {
            return null;
        }

        int[] ret = new int[++end - begin];
        for (int i = 0; begin < end;) {
            ret[i++] = begin++;
        }
        return ret;
    }

    public static String toString(List list, String separator, boolean separatorAfterLast) {
        int i = 0;
        String ret = "";
        for (Object object : list) { // use iterator, because of linkedlist
            if (i < list.size()) {
                ret += object.toString() + separator;
            } else {
                ret += object.toString();
            }
            i++;
        }
        return ret;
    }

}
