/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.utilsclasses;

import minig.data.fuzzification.fuzzification_old.FCMeanFuzzyificator;
import java.util.ArrayList;
import java.util.List;
import minig.data.fuzzification.fuzzification_old.clusteringOneDimensional.CM.KMeansOneDimension;
import minig.data.core.attribute.NumericAttr;

/**
 *
 * @author jrabc
 */
public class Tests {

    public static void main(String[] args) {
        NumericAttr attr = new NumericAttr("s") {
            {
                for (int i = 0; i < 1; i++) {
                    addValue(10);
                    addValue(11);
                    addValue(12);
                    addValue(11);
                    addValue(12);
                    addValue(13);
                    addValue(9);

                    addValue(111);
                    addValue(121);
                    addValue(132);
                    addValue(121);
                    addValue(132);
                    addValue(126);
                    
                         addValue(411);
                    addValue(421);
                    addValue(432);
                    addValue(421);
                    addValue(432);
                    addValue(426);
                    

                }
            }
        };
        attr.addValue(0);
        KMeansOneDimension cls = new KMeansOneDimension(attr, 3);
        cls.clustering();

        FCMeanFuzzyificator fc = new FCMeanFuzzyificator(attr);
        fc.getFuzzyAttr(attr).print();
    }

}
