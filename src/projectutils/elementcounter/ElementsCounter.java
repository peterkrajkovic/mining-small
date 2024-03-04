/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.elementcounter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rabcan
 * @param <O>
 */
public class ElementsCounter<O> {

    private HashMap<O, Element<O>> list = new HashMap<>();

    public void addElement(O element) {

        if (list.containsKey(element)) {
            list.get(element).pridajVyskyt();
        } else {
            list.put(element, new Element(element));
        }
    }

    public List<Element<O>> getSortedList() {
        List<Element<O>> a = new LinkedList();
        for (Element<O> c : list.values()) {
            a.add(c);
        }
        Collections.sort(a);
        return a;
    }

    public O getMax() {
        List<Element<O>> a = getSortedList();
        if (a.isEmpty()) {
            return null;
        }
        return a.get(a.size() - 1).getObject();
    }

    public O getMin() {
        List<Element<O>> a = getSortedList();
        if (a.isEmpty()) {
            return null;
        }
        return a.get(0).getObject();
    }
    
    public int getOccurences(O element){
        return list.get(element).getPocetVyskytov();
    }

    @Override
    public String toString() {
        String pom = "";
        for (Element<O> s : list.values()) {
            pom += s + "\n";
        }
        return pom;
    }

    public static void main(String[] args) {
        ElementsCounter<Integer> s = new ElementsCounter<>();
        s.addElement(1);
        s.addElement(2);
        s.addElement(3);
        s.addElement(1);
        s.addElement(1);
        s.addElement(1);
        System.out.println(s.getSortedList());
    }

    public double getCount() {
        return list.size();
    }

}
