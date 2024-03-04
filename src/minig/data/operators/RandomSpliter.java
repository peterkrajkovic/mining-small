/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.SplittableRandom;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.ProjectUtils;

/**
 *
 * @author jrabc
 */
public class RandomSpliter extends DataOperator {

    private DataSet trainingDataset;
    private DataSet testingDataset;
    private double trainingPercentage = 0.2;
    private long seed = (long) (Math.random() * 1000000000);
    private SplittableRandom rand;

    public RandomSpliter() {
        rand = new SplittableRandom(seed);
    }

    public RandomSpliter(DataSet dataset) {
        super(dataset);
        rand = new SplittableRandom(seed);
        split();
    }

    public RandomSpliter(DataSet dataset, long seed) {
        super(dataset);
        rand = new SplittableRandom(seed);
        split();
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        rand = new SplittableRandom(seed);
    }

    /**
     *
     * @param dataset
     * @param trainingPercentage - [0,1] percentage for training
     */
    public RandomSpliter(DataSet dataset, double trainingPercentage) {
        super(dataset);
        rand = new SplittableRandom(seed);
        setTrainingRatio(trainingPercentage);
        split();
    }

    public RandomSpliter(DataSet dataset, double trainingPercentage, long seed) {
        super(dataset);
        setTrainingRatio(trainingPercentage);
        rand = new SplittableRandom(seed);
        split();
    }

    public final void split() {
        trainingDataset = getDataset().getEmptyCopy();
        testingDataset = getDataset().getEmptyCopy();
        int dataCount = getDataset().getDataCount();
        int count = (int) (((double) dataCount) * trainingPercentage);
        int[] deviders = getRandNumbers(count, seed);
        for (int i = 0; i < getDataset().getDataCount(); i++) {
            if (i < count) {
                testingDataset.addInstance(getDataset().getInstance(deviders[i]));
            } else {
                trainingDataset.addInstance(getDataset().getInstance(deviders[i]));
            }
        }
    }

    private int[] getRandNumbers(int count, long seed) {
        int[] dataIndices = new int[getDataset().getDataCount()];

        for (int i = 0; i < getDataset().getDataCount(); i++) {
            dataIndices[i] = i;
        }
        ProjectUtils.shuffle(dataIndices, rand);
        return dataIndices;
    }

    public DataSet getTrainingDataset() {
        if (trainingDataset == null) {
            throw new Error("Method split must be called");
        }
        return trainingDataset;
    }

    public DataSet getTestingDataset() {
        if (testingDataset == null) {
            throw new Error("Method split must be called");
        }
        return testingDataset;
    }

    public double getTrainingPercentage() {
        return trainingPercentage;
    }

    public final void setTrainingRatio(double trainingPercentage) {
        this.trainingPercentage = 1 - trainingPercentage;
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(DatasetFactory.LIESKOVSKY_SEM);
        System.out.println(dt.getDataCount());
        dt.print();
        RandomSpliter dts = new RandomSpliter(dt, 100);
        dts.getTestingDataset().print();
    }

}
