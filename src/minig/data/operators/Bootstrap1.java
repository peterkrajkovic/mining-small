/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.operators;

import java.util.SplittableRandom;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;

/**
 *
 * @author jrabc
 */
public class Bootstrap1 extends DataOperator {

    private int numberOfInstances;
    private SplittableRandom rand = new SplittableRandom(1);

    public Bootstrap1(DataSet dataset) {
        super(dataset);
        init();
    }

    private void init() {
        numberOfInstances = getDataset().getDataCount();
    }

    public void setSeed(long seed) {
        rand = new SplittableRandom(seed);
    }
    
    public Bootstrap1(DataSet dataset, long randomSeed) {
        super(dataset);
        init();
    }

    public void bootstrapSize(int numberOfInstances) {
        this.numberOfInstances = numberOfInstances;
    }

    /**
     * BOOTSTRAP AGGREGATING(bagging)
     *
     * @return next dataset according to seed
     */
    public synchronized DataSet getNextDataset() {
        DataSet bdt = getDataset().getEmptyCopy(true);
        for (int j = 0; j < numberOfInstances; j++) {
            int idnex = rand.nextInt(getDataset().getDataCount());
            bdt.addInstance(getDataset().getRow(idnex));
        }
        return bdt;
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getGolf().getFuzzyDataset();
        System.out.println(dt);
        Bootstrap1 bt = new Bootstrap1(dt);
        bt.getNextDataset().print();

    }

}
