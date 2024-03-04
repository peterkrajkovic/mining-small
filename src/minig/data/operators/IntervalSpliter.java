/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.operators;

import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.NewInstance;

/**
 *
 * @author jrabc
 */
public class IntervalSpliter extends DataOperator {

    private DataSet training, testing;

    public IntervalSpliter(DataSet dataset, int trainigFrom, int trainingTo) {
        super(dataset);
        initDatasets(trainigFrom, trainingTo);
    }

    private void initDatasets(int trainigFrom1, int trainingTo1) {
        training = getDataset().getEmptyCopy();
        testing = getDataset().getEmptyCopy();
        for (int j = 0; j < getDataset().getDataCount(); j++) {
            if (trainigFrom1 <= j && j < trainingTo1) {
                NewInstance inst = getDataset().getInstance(j);
                testing.addInstance(inst);
            } else {
                NewInstance inst = getDataset().getInstance(j);
                training.addInstance(inst);
            }
        }
    }

    public DataSet getTrainingDataset() {
        return training;
    }

    public DataSet getTestingDataset() {
        return testing;
    }

}
