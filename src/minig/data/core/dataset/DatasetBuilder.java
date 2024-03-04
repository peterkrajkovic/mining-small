/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.dataset;

import java.util.ArrayList;
import minig.data.core.attribute.Attribute;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class DatasetBuilder {

    private String name = "";

    private ArrayList<Attribute> attributes = new ArrayList<>();

    private int numericCount;
    private int lingvisticCount;
    private int fuzzyCount;

    public void setName(String name) {
        this.name = name;
    }

    public DatasetBuilder addAttribute(Attribute attr) {
        attributes.add(attr);
        switch (attr.getType()) {
            case Attribute.FUZZY:
                fuzzyCount++;
                break;
            case Attribute.LINGUISTIC:
                lingvisticCount++;
                break;
            case Attribute.NUMERIC:
                numericCount++;
                break;
        }
        return this;
    }

    public DataSet build() {
        DataSet dt = new DataSet(numericCount, lingvisticCount, fuzzyCount, attributes.get(0).getDataCount(), attributes);
        dt.setName(name);
        return dt;
    }

}
