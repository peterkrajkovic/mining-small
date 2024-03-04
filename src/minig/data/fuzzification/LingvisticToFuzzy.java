/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification;

import projectutils.structures.DoubleVector;
import java.util.Collection;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.classification.fdt.FDTu;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.data.fuzzification.functionsset.Fcmf;
import minig.data.fuzzification.toFuzzzy.ToFuzzy;
import projectutils.ProjectUtils;

/**
 *
 * @author jrabc
 */
public class LingvisticToFuzzy implements ToFuzzy {

    private LinguisticAttr attr;

    public LingvisticToFuzzy(LinguisticAttr attr) {
        this.attr = attr;
    }

    @Override
    public FuzzyAttr toFuzzy() {
        final Collection<String> values = attr.getDomainNames();
        final int valSize = values.size();
        final FuzzyAttr fuzzyattr = new FuzzyAttr(attr.getName(), values, AttrValue.Type.BINARY);
        for (Integer value : attr.getValues()) {
            DoubleVector row = DoubleVector.zeros(valSize);
            row.setNum(value, 1);
            fuzzyattr.addFuzzyRow(row);
        }
        return fuzzyattr;
    }

    public static FuzzyAttr get(LinguisticAttr attr) {
        LingvisticToFuzzy lf = new LingvisticToFuzzy(attr);
        return lf.toFuzzy();
    }

    public static DataSet lingvisticToFuzzy(DataSet dt) {
        for (Attribute attribute : dt.getAttributes()) {
            if (Attribute.isLingvistic(attribute)) {
                boolean isOutput = attribute.isOutputAttr();
                LingvisticToFuzzy ltf = new LingvisticToFuzzy((LinguisticAttr) attribute);
                FuzzyAttr attr = ltf.toFuzzy();
                int index = attribute.getAttributeIndex();
                dt.replaceAttribute(attr, index, false);
                if (isOutput) {
                    dt.setOutputAttrIndex(index);
                }
            }
        }
        return dt;
    }

    public static void main(String[] args) {
//        LinguisticAttr attr = new LinguisticAttr("ATTR", "A", "B", "C");
//        attr.addRows(0,1,1,0,0,1,0,2,0,2,2,2,2);
//        LingvisticToFuzzy aa = new LingvisticToFuzzy(attr);
//        FuzzyAttr ff = aa.toFuzzy();
//        System.out.println(ff.getDomainNames());
//        for (int i = 0; i < ff.getDataCount(); i++) {
//            System.out.println(ff.getRow(i));
//        }
        DataSet dt = DatasetFactory.getDataset(DatasetFactory.DataSetCode.IRIS);
        dt.print();

        dt = Fuzzification.byKMeansWH(dt, new Fcmf());
        dt.print();

        FDTu dta = new FDTu();
        dta.setDataset(dt);
        dta.buildModel();
        dta.print();

        ProjectUtils.toClipboard(dta.getGraphvizCode());
    }

}
