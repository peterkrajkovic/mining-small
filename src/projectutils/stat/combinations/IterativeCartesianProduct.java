/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectutils.stat.combinations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import projectutils.StopWatch;

/**
 *
 * @author jrabc
 */
public class IterativeCartesianProduct implements Iterable<Object[]> {

    private List<TempContainer> containers = new ArrayList<>();

    public void addContainer(Collection val) {
        containers.add(new TempContainer(val));
    }

    public void addObjContainer(Object... val) {
        containers.add(new TempContainer(val));
    }

    public List<Object[]> getCombinations() {
        reset();
        List combinations = new ArrayList<>();
        do {
            final Object[] combination = getCombination();
            combinations.add(combination);
        } while (tryShift());

        return combinations;
    }

    public long getCombinationCount() {
        long ret = 1;
        for (TempContainer container : containers) {
            ret *= container.items.length;
        }
        return ret;
    }
    public void forEachCombination(Consumer<Object[]> consumer) {
        reset();
        do {
            final Object[] combination = getCombination();
            consumer.accept(combination);
        } while (tryShift());
    }

    private Object[] getCombination() {
        Object[] combination = new Object[containers.size()];
        for (int i = 0; i < containers.size(); i++) {
            combination[i] = containers.get(i).getItem();
        }
        return combination;
    }

    private boolean tryShift() {

        boolean shifted = false;
        int i = containers.size() - 1;
        while (shifted == false && i >= 0) {
            TempContainer container = containers.get(i--);
            shifted = container.shift();
        }
        return shifted;
    }

    private void reset() {
        for (int i = 0; i < containers.size(); i++) {
            containers.get(i).state = 0;
        }
    }

    @Override
    public Iterator<Object[]> iterator() {
        reset();
        return new Iterator<Object[]>() {

            @Override
            public boolean hasNext() {
                return tryShift();
            }

            @Override
            public Object[] next() {
                return getCombination();
            }
        };
    }

    class TempContainer {

        Object[] items;

        int state = 0;

        TempContainer(Object[] items) {
            this.items = items;
        }

        TempContainer(Collection val) {
            this.items = val.toArray();
        }

        Object getItem() {
            return items[state];
        }

        /**
         *
         * @return true if state is not restarted -> if true then previous
         * container must be shifted
         */
        boolean shift() {
            state++;
            if (state > items.length - 1) {
                state = 0;
                return false;
            }
            return true;
        }

    }

    public static void main(String[] sa) {
//        Object[] newCombination = {1,2,3,4,5};
//        Object[] combination = new Integer[5];
//        System.arraycopy(newCombination, 0, combination, 0, newCombination.length - 1);
//        System.out.println(Arrays.toString(combination));

        IterativeCartesianProduct cmb = new IterativeCartesianProduct();
        StopWatch sw = new StopWatch();
        newmethod2(cmb);
        sw.printTimeAndReset();
    }

    private static void newmethod2(IterativeCartesianProduct cmb) {
        cmb.addContainer(Arrays.asList("a", "b", "c"));
        cmb.addContainer(Arrays.asList("1", "2", "3"));
        cmb.addContainer(Arrays.asList("5", "x"));
        cmb.addContainer(Arrays.asList("i", "k", "l"));
        List<Object[]> combinations = cmb.getCombinations();
        for (Object[] combination : combinations) {
            System.out.println(Arrays.toString(combination));
        }
    }

}
