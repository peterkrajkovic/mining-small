package projectutils;

import projectutils.structures.DoubleVector;
import projectutils.structures.Vector;
import java.util.LinkedList;
import java.util.List;
import minig.data.core.attribute.AttrValue;

/**
 * V excel prikladoch sumproduct, alebo M(B) Sem to reprezentuje postupnost hran
 * od vrcholu az k danemu uzlu. Uchovava sa len jeden vektor hodnot, po pridani
 * dalsieho sa hodnoty prenasobia. Funguje to ako sumproduct, ale iterativne
 *
 * @author jrabc
 */
public class ProductUnionSlow extends Union {

    private LinkedList<Vector> vecors = new LinkedList();

    public ProductUnionSlow() {
    }

    public DoubleVector getValues() {
        return Vector.getProductVector(vecors);
    }

    public ProductUnionSlow getCopy() {
        return new ProductUnionSlow(this);
    }

    public int getSize() {
        if (vecors.isEmpty()) {
            return 0;
        }
        return vecors.getFirst().size();
    }

    public ProductUnionSlow(AttrValue val) {
        vecors.add(val.getValues());
    }

    public ProductUnionSlow(ProductUnionSlow sp) {
        vecors = new LinkedList<>(sp.vecors);
    }

    public ProductUnionSlow(List<Double> values) {
        this.vecors.add((DoubleVector) values);
    }

    public void addAttrVal(AttrValue val) {
        vecors.add(val.getValues());
    }

    public double getValueAtProductVector(int i) {
        double ret = 1;
        for (int j = vecors.size(); j > 0; j--) {
            ret *= vecors.get(j).get(i);
        }
        return ret;
    }

    public String toString() {
        return vecors.toString();
    }

    public void print(int limit) {
        System.out.println(toString());
    }

    /**
     *
     * @return count of added attrValues
     */
    public int getCount() {
        return vecors.size();
    }

    public boolean isEmpty() {
        return vecors.isEmpty();
    }

    public double getSumProduct() {
        return Vector.sumproduct(vecors);
    }

    public LinkedList<Vector> getAllValues() {
        return vecors;
    }

    public double getSumproductOf(AttrValue val) {
        Vector m = val.getValues();
        if (vecors.isEmpty()) {
            return m.sum();
        }
        return Vector.sumproduct(vecors, m);
        // return m.mul(this.values).mulsum();
    }

    public double getSumproductOf(AttrValue val, AttrValue val1) {
        Vector m = val.getValues();
        Vector n = val1.getValues();
        return Vector.sumproduct(vecors, m, n);
    }

    public double getProduct(Vector vaules) {
        return this.getProduct((List<Double>) vaules);
    }

    public double getProduct(List<Double> vaules) {
        if (vecors.isEmpty()) {
            return ((DoubleVector) vaules).sum();
        } else {
            return Vector.sumproduct(vecors, (Vector) vaules);
        }
    }

    public void destroy() {
        vecors.clear();
    }

    public double getStaticUnion(AttrValue val, AttrValue val1) {
        Vector m = val.getValues();
        Vector n = val1.getValues();
        return m.sumproductIn(n);
    }

}
