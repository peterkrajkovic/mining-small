/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.generator;

import java.util.SplittableRandom;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.dataset.DataSet;
import projectutils.ProjectUtils;

/**
 *
 * @author rabcan
 */
public class SimpleDatasetGenerator {

    private long seed = 1;
   

    public SimpleDatasetGenerator() {
    }

    public SimpleDatasetGenerator(long seed) {
        this.seed = seed;
    }

    private void addInstance(DataSet dataset, SplittableRandom sp) {

        dataset.forEachAttr((attr) -> {
            switch (attr.getType()) {
                case Attribute.LINGUISTIC:
                    attr.linguistic().getValues().addNum(ProjectUtils.randBetween((int) 0, (int) attr.getDomainSize() - 1, sp));
                    break;
                default:
                    throw new Error("Attribute: not implemented yet");
            }
        });
    }

    public DataSet generate(int attrCount, int valuesCount, int instanceCount) {
        DataSet dt = new DataSet();
         SplittableRandom sp = new SplittableRandom(seed);
        for (int i = 0; i < attrCount; i++) {
            LinguisticAttr attr = new LinguisticAttr("Attr " + i + 1);
            for (int j = 0; j < valuesCount; j++) {
                attr.addAttrValue("class " + j);
            }
            dt.addAttribute(attr);
        }
        for (int i = 0; i < instanceCount; i++) {
            addInstance(dt, sp);
        }
        dt.initDatasetInstances();
        return dt;
    }

    public static void main(String[] args) {
        SimpleDatasetGenerator sg = new SimpleDatasetGenerator();
        sg.generate(5, 2, 100).print();

    }

}
