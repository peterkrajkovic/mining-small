/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM;

import java.util.ArrayList;
import minig.data.core.attribute.FuzzyAttr;
import projectutils.stat.IncrementalStat;
import projectutils.ProjectUtils;
import java.util.LinkedList;
import java.util.List;
import static projectutils.ProjectUtils.getMaxValueIndex;
import projectutils.stat.Mean;

/**
 *
 * @author jrabc
 */
public class Cluster implements Comparable<Cluster>, minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.Cluster {

    private double center = 0;
    private IncrementalStat stat = new IncrementalStat();
    private List<Integer> indexes = new ArrayList<>();
    private KMeansOneDimension clusterAlg;

    private double getCommonEntrophy(double d) {
        return -d * ProjectUtils.log2(d);
    }

    private double getTermEntrophy(FuzzyAttr inputAttr, int inputAttrVal, FuzzyAttr outputAttr) {
        List<Mean> M = new ArrayList<>(); // sucet stupnov prislusnost pre instancie pre i-tu hodnotu vystupneho atributa 
        double mSum = 0;
        for (int i = 0; i < outputAttr.getDomainSize(); i++) {
            M.add(new Mean());
        }
        for (int i = 0; i < indexes.size(); i++) {
            int index = indexes.get(i);
            int outputAttrClassIndex = getMaxValueIndex(outputAttr.getRow(index));
            double m = inputAttr.getRow(index).get(inputAttrVal);

            mSum += m;
            M.get(outputAttrClassIndex).add(m);
        }
        double entrophy = 0;
        for (int i = 0; i < M.size(); i++) {
            if (mSum != 0) {
                double di = M.get(i).getSum() / mSum;
                if (di != 0) {
                    entrophy += getCommonEntrophy(di);
                }
            }
        }
        return entrophy;
    }

    public KMeansOneDimension getClusterAlg() {
        return clusterAlg;
    }

    public double getEntrophy(FuzzyAttr inputAttr, FuzzyAttr outputAttr) {
        double entrophy = 0;
        for (int i = 0; i < inputAttr.getDomainSize(); i++) {
            entrophy += getTermEntrophy(inputAttr, i, outputAttr);
        }
        return entrophy;
    }

    public double getWeightEntrophy(FuzzyAttr inputAttr, FuzzyAttr outputAttr) {
        double entrophy = 0;
        for (int i = 0; i < inputAttr.getDomainSize(); i++) {
            entrophy += getTermEntrophy(inputAttr, i, outputAttr);
        }
        return ((double) indexes.size() / inputAttr.getDataCount()) * entrophy;
    }

    public static double getWeightedEntrophy(List<Cluster> intervals, FuzzyAttr input, FuzzyAttr output) {
        double entrophy = 0;
        for (Cluster interval : intervals) {
            entrophy += interval.getWeightEntrophy(input, output);
        }
        return entrophy;
    }

    public Cluster(double center) {
        this.center = center;
    }

    public void clear() {
        stat.reset();
        indexes.clear();
        center = 0;
    }

    /**
     * initial center ((double) (q - 1)) / (Q - 1)
     *
     * @param q
     * @param Q
     * @param clusterAlg
     */
    public Cluster(int q, int Q, KMeansOneDimension clusterAlg) {
        this.clusterAlg = clusterAlg;
        this.center = ((double) (q - 1)) / (Q - 1);
    }

    public Cluster(double min, double max, int index, int Q, KMeansOneDimension clusterAlg) {
        this.clusterAlg = clusterAlg;
        if (Q == 1) {
            center = (max + min) / 2;
        } else {
            double step = ((double) (max - min)) / (Q - 1);
            this.center = min + step * index;
        }
    }

    @Override
    public double getCenter() {
        return center;
    }

    public double getCenter(int totalCount) {
        return (stat.getCount() * center) / totalCount;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public IncrementalStat getStat() {
        return stat;
    }

    public void setStat(IncrementalStat stat) {
        this.stat = stat;
    }

    public void add(double number) {
        stat.add(number);
    }

    public void add(int OAindex, double x) {
        stat.add(x);
        indexes.add(OAindex);
    }

    public double getEuclideDistance(double number) {
        return Math.abs(number - center);
    }

    public boolean updateCenter() {
        double average = stat.getMean();
        if (stat.getCount() == 0 | center == average) {
            return false;
        } else {
            center = average;
            return true;
        }
    }

    public static void addNumberToCorrectInterval(List<Cluster> intervals, double number) {
        double minEd = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < intervals.size(); i++) {
            Cluster interval = intervals.get(i);
            double distance = interval.getEuclideDistance(number);
            if (distance < minEd) {
                index = i;
                minEd = distance;
            }
        }
        intervals.get(index).add(number);
    }

    public static void addNumberToCorrectInterval(List<Cluster> intervals, double number, int numberIndex) {
        if (!Double.isNaN(number)) {
            double minEd = Double.MAX_VALUE;
            int index = 0;
            for (int i = 0; i < intervals.size(); i++) {
                Cluster interval = intervals.get(i);
                double distance = interval.getEuclideDistance(number);
                if (distance < minEd) {
                    index = i;
                    minEd = distance;
                }
            }
            intervals.get(index).add(numberIndex, number);
        }
    }

    public void clearData() {
        stat.reset();
        indexes.clear();
    }

    @Override
    public int compareTo(Cluster o) {
        if (center == o.center) {
            return 0;
        } else if (center > o.center) {
            return 1;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Cluster{" + "center=" + center + '}';
    }

    public static void main(String[] args) {
        FuzzyAttr input = new FuzzyAttr("a", "", "", "");
        input.addFuzzyRow(0.4, 0.6, 0.0);
        input.addFuzzyRow(0.2, 0.8, 0.0);
        input.addFuzzyRow(0.0, 1.0, 0.0);
        input.addFuzzyRow(0.0, 0.9, 0.1);
        input.addFuzzyRow(0.0, 0.7, 0.3);
        FuzzyAttr output = new FuzzyAttr("", "", "");
        output.addFuzzyRow(1.0, 0.0);
        output.addFuzzyRow(1.0, 0.0);
        output.addFuzzyRow(0.0, 1.0);
        output.addFuzzyRow(1.0, 0.0);
        output.addFuzzyRow(0.0, 1.0);
        Cluster a = new Cluster(0);
        a.add(0, 1);
        a.add(1, 1);
        a.add(2, 1);
        a.add(3, 1);
        a.add(4, 1);
        System.out.println(a.getWeightEntrophy(input, output));

    }

}
