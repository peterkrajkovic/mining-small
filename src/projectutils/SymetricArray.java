/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;

/**
 *
 * @author jrabc
 */
public class SymetricArray implements ConsolePrintable {

    private int dimension;
    private double[] data;
    private int pomIndex1; //PERFORMANCE IMPROVEMENT FOR INDEX CALCULATION
    private int pomIndex2; //PERFORMANCE IMPROVEMENT FOR INDEX CALCULATION

    public SymetricArray(int dimension) {
        initDataArray(dimension);
    }

    private void initDataArray(int dimension) {
        this.dimension = dimension;
        pomIndex2 = dimension - 1;
        pomIndex1 = (dimension * (pomIndex2) / 2);
        data = new double[getNumberOfCombinations(dimension, 2)];
    }

    private int index(int i, int j) {
        return pomIndex1 - ((dimension - i) * (pomIndex2 - i) / 2) + j;
    }

    public void put(int i, int j, double value) {
        int index;
        index = i < j ? index(i, j) : index(j, i);
        data[index] = value;
    }

    public boolean isZero(int i, int j) {
        return get(i, j) == 0;
    }

    public double get(int i, int j) {
        int index = i < j ? index(i, j) : index(j, i);
        return data[index];
    }

    private int getNumberOfCombinations(long n, long k) {
        n++;
        if (k > n) {
            return 0;
        }
        BigInteger r = BigInteger.valueOf(1);
        for (BigInteger d = BigInteger.valueOf(1); d.intValue() <= k; d = d.add(BigInteger.ONE)) {
            r = r.multiply(BigInteger.valueOf(n--));
            r = r.divide(d);
        }
        return r.intValue();
    }

    public int getCapacity() {
        return data.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder((dimension * dimension) * 6);
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                sb.append(ProjectUtils.formatDouble(get(i, j)));
                if (j < dimension - 1) {
                    sb.append(",");
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public void destroyData() {
        data = new double[0];
        data = null;
    }

    public static void writeToFile(SymetricArray arr, String path) throws IOException {
        StringBuffer sbb = new StringBuffer();
        for (int i = 0; i < arr.dimension; i++) {
            for (int j = 0; j < arr.dimension - 1; j++) {
                sbb.append(arr.get(i, j)).append(", ");
            }
            sbb.append(arr.get(i, arr.dimension - 1)).append(System.lineSeparator());
        }
        FileWriter fw = new FileWriter(new File(path));
        fw.write(sbb.toString());
        fw.close();
    }

    public static void main(String[] args) throws IOException {
        DataSet dt = DatasetFactory.getDataset(DatasetFactory.WEIGHTING_rapidminer);
        SymetricArray sm = new SymetricArray(200);
        sm.put(0, 0, 1);
        sm.put(1, 0, 2);
        sm.put(1, 2, 4);
        sm.put(2, 0, 8);
        sm.put(2, 2, 7);
        int a = 299999999;
        //  sm.print();
        System.out.println(sm.getCapacity());
        writeToFile(sm, "aaa");
    }
}
