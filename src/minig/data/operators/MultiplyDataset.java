/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.operators;

import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class MultiplyDataset extends DataOperator {

    private boolean hardCopy = false;

    public MultiplyDataset() {
        super();
    }

    public MultiplyDataset(DataSet dt) {
        super(dt);
    }

    public void setHardCopy(boolean hardCopy) {
        this.hardCopy = hardCopy;
    }

    public DataSet getDatasetCopy() {
        if (hardCopy) {
            DataSet origData = getDataset();
            DataSet cp = getDataset().getEmptyCopy();
            for (int i = 0; i < origData.getDataCount(); i++) {
                cp.addInstance(origData.getInstance(i));
            }
            return cp;
        } else {
            DataSet origData = getDataset();
            DataSet cp = new DataSet();
            for (int i = 0; i < origData.getAtributteCount(); i++) {
                Attribute a = origData.getAttribute(i).getRawCopy();
                cp.addAttribute(a);
            }
            if (origData.isOutputAttributeSet()) {
                cp.setOutputAttrIndex(origData.getOutputAttrIndex());
            }
            cp.initDatasetInstances();
            cp.setName(origData.getName());
            return cp;
        }
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(DatasetFactory.DataSetCode.LIESKOVSKY_SEM);
        MultiplyDataset md = new MultiplyDataset(dt);
        md.setHardCopy(true);

        DataSet cp = md.getDatasetCopy();
        cp.<FuzzyAttr>getAttribute(0).getAttrValue(0).getValues().set(0, 1d);
        cp.addAttribute(cp.getAttribute(0));
        dt.print();
        cp.print();
    }

}
