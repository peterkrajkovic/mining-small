/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification;

import projectutils.ErrorMessages;
import java.util.Arrays;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public class LingvisticToNumericParser {

    private LinguisticAttr attr;

    public LingvisticToNumericParser(LinguisticAttr attr) {
        this.attr = attr;
    }

    public LingvisticToNumericParser(Attribute attr) {
        if (Attribute.isLingvistic(attr)) {
            this.attr = (LinguisticAttr) attr;
        } else {
            throw new Error(ErrorMessages.ATTR_MUST_BE_LINGVISTIC);
        }

    }

  

    public NumericAttr toNumeric() {
        NumericAttr nattr = new NumericAttr(this.attr.getName());
        for (Integer i : attr.getValues()) {
            nattr.addValue(attr.getRow(i));
        }
        return nattr;
    }

    public static void main(String[] args) {
        LinguisticAttr attr = new LinguisticAttr("1", Arrays.asList("s", "2", "3 ", "4", "5", "6", "ssds", "ss"));
        LingvisticToNumericParser aa = new LingvisticToNumericParser(attr);
        NumericAttr ff = aa.toNumeric();
        System.out.println(ff.getDomainNames());
        for (int i = 0; i < ff.getDataCount(); i++) {
            System.out.println(ff.getRow(i));
        }
    }
}
