/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.dataset;

import projectutils.structures.Vector;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class NumericMatrix {

    private Vector[] vector;
    int count = 0;

    public NumericMatrix(int colNum) {
        vector = new Vector[colNum];
    }

    public NumericMatrix(DataSet dataset) {
        this(dataset.getNumAttrCount());
        dataset.forEachNumericAttr((attr) -> {
            addColumn(attr.getValues());
        });
    }

    public void addColumn(Vector v) {
        vector[count++] = v;
    }

    public double get(int rowIndex, int attrIndex) {
        return vector[attrIndex].getNum(rowIndex);
    }

    public void clear() {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = null;
        }
        vector = null;
    }

}
