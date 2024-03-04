/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.operators;

import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.ProjectUtils;

/**
 *
 * @author rabcan
 */
public class InstanceSplitter extends DataOperator {

    public static DataSet split(DataSet set) {
        return split(set, 2);
    }

    public static DataSet split(DataSet set, int count) {
        InstanceSplitter sp = new InstanceSplitter();
        sp.splitInstances(count);
        return sp.getDataset();
    }

    public DataSet splitInstances() {
        return splitInstances(2);
    }

    public DataSet splitInstances(int count) {
        DataSet toSplit = getDataset();
        DataSet splited = new DataSet();
        splited.setName(getDataset().getName());
        if (toSplit.getInputAttrCount() % count == 0) {
            int attrType = getType(toSplit, splited);
            addAttributesToSplittedDataset(toSplit, attrType, splited, count);
            addInstancesToSplittedDataset(toSplit, splited, count);
        } else {
            throw new Error("Number of input attributes must be devisible by " + count);
        }
        return splited;
    }

    private void addAttributesToSplittedDataset(DataSet toSplit, int attrType, DataSet splited, int count) {
        for (int i = 0; i < toSplit.getAtributteCount() / count; i++) {
            switch (attrType) {
                case Attribute.NUMERIC:
                    splited.addAttribute(new NumericAttr("Attr" + i));
                    break;
                case Attribute.LINGUISTIC:
                    splited.addAttribute(new LinguisticAttr("Attr" + i));
                    break;
                case Attribute.FUZZY:
                    throw new Error("Not supported for fuzzy attributes");
            }
        }
    }

    private void addInstancesToSplittedDataset(DataSet toSplit, DataSet splited, int count) {

        Attribute outputAttr = getDataset().getOutbputAttribute();
        Attribute newOA = outputAttr.getEmptyCopy();

        int instanceSize = toSplit.getInputAttrCount() / count;

        toSplit.forEach(instance -> {
            Object[] data = instance.getInputData();
            Object[][] splittedData = ProjectUtils.splitArray(data, instanceSize);

            for (int i = 0; i < count; i++) {

                splited.addInstance(splittedData[i]);
            }

            for (int i = 0; i < count; i++) {
                newOA.addRow(outputAttr.getRow(instance.getIndex()));
            }

        });

        splited.addAttribute(newOA);
        splited.setOutputAttr();
    }

    public int getType(DataSet toSplit, DataSet splited) {
        int intputNumeric = toSplit.getNumAttrCount();
        int intputFuzzy = toSplit.getFuzzyAttrCount();
        int intputLinguistic = toSplit.getLngAttrCount();
        if (toSplit.hasOutputAttr()) {
            if (toSplit.getOutbputAttribute().isFuzzy()) {
                intputFuzzy--;
            } else if (toSplit.getOutbputAttribute().isLinguistic()) {
                intputLinguistic--;
            } else if (toSplit.getOutbputAttribute().isNumeric()) {
                intputNumeric--;
            }
        }
        if (intputNumeric == 0 && intputFuzzy == 0 && intputLinguistic != 0) {
            return Attribute.LINGUISTIC;
        } else if (intputNumeric != 0 && intputFuzzy == 0 && intputLinguistic == 0) {
            return Attribute.NUMERIC;
        } else if (intputNumeric == 0 && intputFuzzy != 0 && intputLinguistic == 0) {
            return Attribute.FUZZY;
        } else {
            throw new Error("All input attrubtes must have the same type.For example: Numeric attributes only");
        }
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(DatasetFactory.DataSetCode.TAE);
        dt.printInfo();
        System.out.println(dt.getDataCount());

        InstanceSplitter is = new InstanceSplitter();
        is.setDataset(dt);
        System.out.println(is.splitInstances(5).getDataCount());
    }
}
