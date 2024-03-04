/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import minig.data.core.attribute.AttrValue;
import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public class Median {

    private List<MedianBox> boxes;
    private int medianIndex = -1;
    private double median;

    private List<Integer> indexes;
    private AttrValue value;

    public Median(List<Integer> indexes, AttrValue value) {
        
        if (indexes == null) {
            boxes = new ArrayList<>(value.size());
            for (int i = 0; i < value.size(); i++) {
                double d = value.get(i);
                boxes.add(new MedianBox(d, i));
            }
        } else {
            boxes = new ArrayList<>(indexes.size());
            for (int i : indexes) {
                double d = value.get(i);
                boxes.add(new MedianBox(d, i));
            }
        }
        Collections.sort(boxes);
        int i = boxes.size() / 2;
        medianIndex = boxes.get(i).getIndex();
        median = boxes.get(i).getValue();
        boxes = null;
    }

    public Median(List<Double> lst) {
        boxes = new ArrayList<>(lst.size());
        for (int i = 0; i < lst.size(); i++) {
            double d = lst.get(i);
            boxes.add(new MedianBox(d, i));
        }
        Collections.sort(boxes);
        int i = boxes.size() / 2;
        medianIndex = boxes.get(i).getIndex();
        median = boxes.get(i).getValue();
        boxes = null;
    }

    public int getMedianIndex() {
        return medianIndex;
    }

    public double getMedian() {
        return median;
    }

    private static class MedianBox implements Comparable<MedianBox> {

        private double d;
        private int index;

        MedianBox(double d, int index) {
            this.d = d;
            this.index = index;
        }

        @Override
        public int compareTo(MedianBox o) {
            return Double.compare(d, o.d);
        }

        public double getValue() {
            return d;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public String toString() {
            return "d=" + d + ", index=" + index;
        }

    }

    public static void main(String[] args) {
        List<Double> dd = Arrays.asList(3d, 1d, 2d, 5d, 6d, 4d, 7d);
        Median m = new Median(dd);
        System.out.println(m.getMedian());
    }
}
