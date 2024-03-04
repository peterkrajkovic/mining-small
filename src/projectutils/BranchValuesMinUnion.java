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
public class BranchValuesMinUnion extends Union {

    protected DoubleVector values;
    protected int count;

    public BranchValuesMinUnion() {
    }

    @Override
    public DoubleVector getValues() {
        return values;
    }

    public BranchValuesMinUnion(int size) {
        this.values = DoubleVector.ones(size);
    }

    @Override
    public BranchValuesMinUnion getCopy() {
        return new BranchValuesMinUnion(this);
    }

    @Override
    public int getSize() {
        if (values == null) {
            return 0;
        }
        return values.size();
    }

    public BranchValuesMinUnion(BranchValuesMinUnion sp) {
        copy(sp);
    }

    @Override
    public double getProduct(Vector vaules) {
        if (values == null) {
            return ((DoubleVector) vaules).sum();
        } else {
            return values.sumUnionIn(vaules);
        }
    }

    private void copy(BranchValuesMinUnion sp) {
        if (sp.values != null) {
            values = new DoubleVector(sp.values);
        }
        count = sp.count;
    }

    public BranchValuesMinUnion(List<Double> values) {
        this.values = new DoubleVector(values);
    }

    @Override
    public void addAttrVal(AttrValue val) {
        count++;
        if (values == null) {
            values = new DoubleVector(val.getValues());
        } else {
            values.unionIn(val.getValues());
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
        return values.sumUnionIn(m);
        //return m.mulsum(values);
        // return m.mul(this.values).mulsum();
    }

  @Override
    public double getSumproductOf(AttrValue val, AttrValue val1) {
        return this.values.sumUnionIn(val.getValues(), val1.getValues());
    }

    @Override
    public void destroy() {
        values = null;
    }

    public double getStaticUnion(AttrValue val, AttrValue val1) {
        Vector m = val.getValues();
        Vector n = val1.getValues();
        return m.sumUnionIn(n);
    }

}
