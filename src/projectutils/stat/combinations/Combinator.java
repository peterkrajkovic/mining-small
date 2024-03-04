/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat.combinations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author jrabc
 * @param <T>
 */
public class Combinator<T> {

    List<TempContainer<T>> containers = new LinkedList<>();
    private Consumer<List<T>> onCombinationCreate;

    /**
     * @param val jeden vector, z ktoreho sa robi kombinacia
     */
    public void addContainer(List<T> val) {
        containers.add(new TempContainer(val));
    }

    public void addObjContainer(Object val) {
        containers.add(new TempContainer((List) val));
    }

    public void addObjContainer(Object... val) {
        containers.add(new TempContainer(Arrays.asList(val)));
    }

    private List<List<T>> getCombination(int currentIndex, List<TempContainer<T>> containers) {
        if (currentIndex == containers.size()) {
            // Skip the items for the last container
            List<List<T>> combinations = new ArrayList<>();
            combinations.add(new ArrayList<>());
            return combinations;
        }
        List<List<T>> combinations = new ArrayList<>();
        TempContainer<T> container = containers.get(currentIndex);
        List<T> containerItemList = container.getItems();
        // Get combination from next index
        List<List<T>> suffixList = Combinator.this.getCombination(currentIndex + 1, containers);
        int size = containerItemList.size();
        for (int ii = 0; ii < size; ii++) {
            T containerItem = containerItemList.get(ii);
            if (suffixList != null) {
                for (List<T> suffix : suffixList) {
                    List<T> nextCombination = new LinkedList<>();
                    nextCombination.add(containerItem);
                    nextCombination.addAll(suffix);
                    combinations.add(nextCombination);
                }
            }
        }
        return combinations;
    }

    private void getCombinationConsumer(int currentIndex, List<TempContainer<T>> containers) {
        if (currentIndex == containers.size()) {
            // Skip the items for the last container
            List<List<T>> combinations = new LinkedList<>();
            combinations.add(new LinkedList<>());
            return;
        }
        TempContainer<T> container = containers.get(currentIndex);
        List<T> containerItemList = container.getItems();
        // Get combination from next index
        List<List<T>> suffixList = Combinator.this.getCombination(currentIndex + 1, containers);
        int size = containerItemList.size();
        for (int ii = 0; ii < size; ii++) {
            T containerItem = containerItemList.get(ii);
            if (suffixList != null) {
                for (List<T> suffix : suffixList) {
                    List<T> nextCombination = new LinkedList<>();
                    nextCombination.add(containerItem);
                    nextCombination.addAll(suffix);
                    onCombinationCreate.accept(nextCombination);
                }
            }
        }
    }

    private void onCombinationCreate(Consumer<List<T>> onCombinationCreate) {
        this.onCombinationCreate = onCombinationCreate;
    }

    public List<List<T>> getCombintations() {
        List<List<T>> combinations = Combinator.this.getCombination(0, containers);
        return combinations;
    }

    public void getCombinations(Consumer onCombinationCreate) {
        onCombinationCreate(onCombinationCreate);
        getCombinationConsumer(0, containers);
    }

    public static <T> List<List<T>> getCombintations(List<T>... vals) {
        Combinator<T> cmb = new Combinator<>();
        for (int i = 0; i < vals.length; i++) {
            List<T> val = vals[i];
            cmb.addContainer(val);
        }
        return cmb.getCombintations();
    }

    public static void main(String[] sa) {
//        Combinator<String> cmb = new Combinator<>();
//        int n = 8;
//        int m = 4;
//        for (int i = 0; i < n; i++) {
//            List<String> states = new ArrayList<>();
//            for (int j = 0; j < m; j++) {
//                states.add(Integer.toString(j));
//            }
//            cmb.addContainer(states);
//        }
//        List<List<String>> combinations = cmb.getCombintations();
//        int maxSum = m * n;
//        //System.out.println(combinations);
//        for (List<String> combination : combinations) {
//            int sum = combination.stream().mapToInt(Integer::new).sum();
//            // int level = getState(sum, maxSum, m);
//            int level = ProjectUtils.randBetween(0, m - 1);
//            combination.add(level + "");
//            System.out.println(combination.toString().replace("[", "").replace("]", ""));
//        }
        Combinator<Object> cmb = new Combinator<>();
          cmb.addContainer(List.of("a","n"));
        cmb.addContainer(List.of(1d,2d,3d,4d));
      
        List<List<Object>> combinations = cmb.getCombintations();
        //System.out.println(combinations);
        for (List<Object> combination : combinations) {
            System.out.println(combination.toString().replace("[", "").replace("]", ""));
        }
    }

    class TempContainer<T> {

        private List<T> items;

        TempContainer(List<T> items) {
            this.items = items;
        }

        public void setItems(List<T> items) {
            this.items = items;
        }

        public List<T> getItems() {
            return items;
        }
    }

}
