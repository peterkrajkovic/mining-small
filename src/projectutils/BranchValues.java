/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import projectutils.structures.DoubleVector;
import projectutils.structures.Vector;
import java.util.List;
import minig.data.core.attribute.AttrValue;

/**
 * V excel prikladoch sumproduct, alebo M(B) Sem to reprezentuje postupnost hran
 * od vrcholu az k danemu uzlu. Uchovava sa len jeden vektor hodnot, po pridani
 * dalsieho sa hodnoty prenasobia. Funguje to ako sumproduct, ale iterativne
 *
 * @author jrabc
 */
public class BranchValues extends Union {

    protected DoubleVector values;
    protected int count;

    public BranchValues() {
    }

    @Override
    public DoubleVector getValues() {
        return values;
    }

    public BranchValues(int size) {
        this.values = DoubleVector.ones(size);
    }

    @Override
    public BranchValues getCopy() {
        return new BranchValues(this);
    }

    @Override
    public int getSize() {
        if (values == null) {
            return 0;
        }
        return values.size();
    }

    public BranchValues(AttrValue val) {
        this.values = new DoubleVector(val.getValues());
    }

    public BranchValues(BranchValues sp) {
        copy(sp);
    }

    private void copy(BranchValues sp) {
        if (sp.values != null) {
            values = new DoubleVector(sp.values);
        }
        count = sp.count;
    }

    public BranchValues(List<Double> values) {
        this.values = new DoubleVector(values);
    }

    @Override
    public void addAttrVal(AttrValue val) {
        count++;
        if (values == null) {
            values = new DoubleVector(val.getValues());
        } else {
            values.muli(val.getValues());
        }
    }

    public void initByOnes(int count) {
        this.count = count;
        values = DoubleVector.ones(count);
    }

    @Override
    public double getValueAtProductVector(int i) {
        return values.getNum(i);
    }

    @Override
    public String toString() {
        return values.toString();
    }

    public void print(int limit) {
        System.out.println(toString());
    }

    /**
     *
     * @return count of added attrValues
     */
    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return values == null || values.isEmpty();
    }

    @Override
    public double getSumProduct() {
        if (values == null) {
            return 0;
        }
        return values.sum();
    }

    @Override
    public double getSumproductOf(AttrValue val) {
        Vector m = val.getValues();
        if (values == null) {
            return m.sum();
        }
        return values.sumproductIn(m);
        //return m.mulsum(values);
        // return m.mul(this.values).mulsum();
    }

    @Override
    public double getSumproductOf(AttrValue val, AttrValue val1) {
        return this.values.sumproductIn(val.getValues(), val1.getValues());
    }

    public double getProduct(Vector vaules) {
        if (values == null) {
            return ((DoubleVector) vaules).sum();
        } else {
            return values.sumproductIn(vaules);
        }
    }

    @Override
    public void destroy() {
        values = null;
    }

    public double getStaticUnion(AttrValue val, AttrValue val1) {
        Vector m = val.getValues();
        Vector n = val1.getValues();
        return m.sumproductIn(n);
    }

 
}
