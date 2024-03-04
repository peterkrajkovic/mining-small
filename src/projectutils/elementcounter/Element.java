/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.elementcounter;

/**
 *
 * @author rabcan
 */
public class Element<O> implements Comparable<Element<O>> {

    private O object;
    private int pocetVyskytov = 1;

    public Element(O object) {
        this.object = object;
    }

    public void pridajVyskyt() {
        pocetVyskytov++;
    }

    public int getPocetVyskytov() {
        return pocetVyskytov;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return this.object.equals(obj);
    }

    public String toString() {
        return pocetVyskytov + " " + object.toString();
    }

    public O getObject() {
        return object;
    }
    
    @Override
    public int compareTo(Element<O> o) {
        if (pocetVyskytov == o.getPocetVyskytov()) {
            return 0;
        }
        return pocetVyskytov > o.getPocetVyskytov() ? 1 : -1;

    }

}
