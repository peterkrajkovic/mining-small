/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import static projectutils.ProjectUtils.log2;
import projectutils.structures.DoubleVector;

/**
 *
 * @author jrabc
 */
public class AttrUtils {

    /**
     * http://mazsola.iit.uni-miskolc.hu/DATA/diploma/brutoczki_kornelia/fu_gz_01.html
     *
     * @param value
     * @return
     */
    public static double getFuzzyEntropy(AttrValue value) {
        double entropy = 0;
        for (int i = 0; i < value.getValues().size(); i++) {
            double u = value.getValues().get(i);
            entropy += (-u * log2(u)) - ((1 - u) * log2(1 - u));
        }
        return entropy;
    }

    /**
     * The summary entropy of an attribute attr
     *
     * @param attr
     * @return
     */
    public static double experimentEntropy(FuzzyAttr attr) {
        double entropy = 0;
        int dataCount = attr.getDataCount();
        for (AttrValue value : attr.getDomain()) {
            double weight = value.getSum() / dataCount;
            entropy += weight * getFuzzyEntropy(value);
        }
        return entropy;
    }

    public static double meanAttrEntropy(FuzzyAttr attr) {
        double e = 0;
        int dataCount = attr.getDataCount();
        for (AttrValue value : attr.getDomain()) {
            double cardinality = value.getSum();
            //  double p = cardinality/dataCount;
            double information = log2(dataCount) * log2(cardinality);
            double entropy = cardinality * information;
            e += entropy;
        }
        return e;
    }

    public static void addComplement(FuzzyAttr attr, String name) {
        AttrValue val = attr.addClass(name);
        for (int i = 0; i < attr.getDataCount(); i++) {
            try {
                double sum = 0;

                DoubleVector row = new DoubleVector(attr.getValues().size() - 1);
                for (int j = 0; j < attr.getValues().size() - 1; j++) {
                    AttrValue v = attr.getValues().get(j);
                    row.add(v.get(i));
                }
                sum = row.sum();
                val.addVaule(1 - sum);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
