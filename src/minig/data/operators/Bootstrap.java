/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;

/**
 *
 * @author jrabc
 */
public class Bootstrap extends DataOperator {

    private int datasetCount = 5;
    private List<DataSet> dt;
    private long seed;

    public Bootstrap(DataSet dataset) {
        super(dataset);
    }

    public Bootstrap(int datasetCount, DataSet dataset) {
        super(dataset);
        this.datasetCount = datasetCount;
    }

    public Bootstrap(int datasetCount, DataSet dataset, long randomSeed) {
        super(dataset);
        this.datasetCount = datasetCount;
        this.seed = randomSeed;
    }

    public Bootstrap(List<DataSet> dt, long randomSeed, DataSet dataset) {
        super(dataset);
        this.dt = dt;
        this.seed = randomSeed;
    }

    /**
     * BOOTSTRAP AGGREGATING(bagging)
     *
     * @return list of dataset
     */
    public List<DataSet> bagging() {
        return bagging(false);
    }

    public List<DataSet> bagging(boolean copyAttrsId) {
        dt = new ArrayList<>(datasetCount);
        SplittableRandom rand = new SplittableRandom(seed);
        for (int i = 0; i < datasetCount; i++) {
            DataSet bdt = getDataset().getEmptyCopy(copyAttrsId);
            for (int j = 0; j < getDataset().getDataCount(); j++) {
                int idnex = rand.nextInt(getDataset().getDataCount());
                bdt.addInstance(getDataset().getRow(idnex));
            }
            dt.add(bdt);
        }
        return dt;
    }

    public List<DataSet> getDt() {
        return dt;
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getGolf().getFuzzyDataset();
        System.out.println(dt);
        Bootstrap bt = new Bootstrap(dt);
        List<DataSet> ddd = bt.bagging();
        System.out.println(ddd.get(0).getAttribute(2).getAttributeIndex());

    }

}
