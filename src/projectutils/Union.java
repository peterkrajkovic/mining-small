/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import minig.data.core.attribute.AttrValue;
import projectutils.structures.DoubleVector;
import projectutils.structures.Vector;

/**
 *
 * @author rabcan
 */
public abstract class Union {

    public Union() {
    }

    public abstract DoubleVector getValues();

    public abstract <T extends Union> T getCopy();

    public abstract int getSize();

    public abstract void addAttrVal(AttrValue val);

    public abstract double getValueAtProductVector(int i);

    /**
     *
     * @return count of added attrValues
     */
    public abstract int getCount();

    public abstract boolean isEmpty();

    public abstract double getSumProduct();

    public abstract double getSumproductOf(AttrValue val);

    public abstract double getSumproductOf(AttrValue val, AttrValue val1);

    public abstract void destroy();

    //-------------------------------------------------------------------------
    public abstract double getStaticUnion(AttrValue val, AttrValue val1);

    @Deprecated
    /**
     * replace with getStaticUnion, will be removed
     */
    public double getSumProdOf(AttrValue val, AttrValue val1) {
        return getStaticUnion(val, val1);
    }

    public abstract double getProduct(Vector vaules);

}
