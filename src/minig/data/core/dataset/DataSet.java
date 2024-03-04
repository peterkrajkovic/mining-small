/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.dataset;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SplittableRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.io.load.DataLoader;
import minig.data.core.io.load.MyLoader;
import minig.data.core.io.save.DatasetWriter;
import minig.data.fuzzification.LingvisticToFuzzy;
import minig.data.fuzzification.LingvisticToNumericParser;
import minig.data.fuzzification.functionsset.FuzzyMapper;
import minig.data.fuzzification.fuzzification_old.DatasetFuzzification.DatasetFuzzificator;
import minig.data.fuzzification.fuzzification_old.DatasetFuzzification.FC_Means_cH;
import minig.data.fuzzification.fuzzification_old.DatasetFuzzification.KMeans_withoutOA_wH;
import minig.data.fuzzification.fuzzification_old.DatasetFuzzification.K_Means_wH;
import minig.data.operators.MultiplyDataset;
import projectutils.ConsolePrintable;
import projectutils.MemoryUtils;
import projectutils.ProjectUtils;

/**
 *
 * @author jrabc
 */
public class DataSet implements ConsolePrintable, Iterable<DatasetInstance> {

    private ArrayList<Attribute> attributes = new ArrayList<>(15);
    private ArrayList<NumericAttr> numAttrs = new ArrayList<>();
    private ArrayList<LinguisticAttr> liguisticAttrs = new ArrayList<>();
    private ArrayList<FuzzyAttr> fuzzyAttrs = new ArrayList<>();

    private ArrayList<DatasetInstance> instances = new ArrayList<>();

    private Attribute outputAttribute;
    private int outputAttrIndex = -1;
    private String name;

    private int rowLength = 0;

    DataSet(int numAttrCount, int lingAttrCount, int fuzzyAttrCount, int dataCount, List<Attribute> attrs) {
        attributes = new ArrayList<>(attrs.size());
        numAttrs = new ArrayList<>(numAttrCount);
        liguisticAttrs = new ArrayList<>(lingAttrCount);
        fuzzyAttrs = new ArrayList<>(fuzzyAttrCount);
        instances = new ArrayList<>(dataCount);
        for (Attribute attr : attrs) {
            addAttribute(attr);
        }
        for (int i = 0; i < dataCount; i++) {
            instances.add(new DatasetInstance(this, i));
        }
    }

    public DataSet() {
        name = "";
    }

    public DataSet(String path, String separator) throws IOException {
        MyLoader my = new MyLoader(this, path, separator);
        my.load();
    }

    public DataSet(DataSet dt) {
        for (Attribute attribute : dt.getAttributesInList()) {
            this.addAttribute(attribute);
        }
        setOutputAttrIndex(dt.getOutputAttrIndex());
        name = dt.getName();
    }

    public DataSet(Attribute... attributes) {
        for (Attribute attribute : attributes) {
            addAttribute(attribute);
        }
        initDatasetInstances();
        name = "";
    }

    public DataSet(String name, Attribute... attributes) {
        this.name = name;
        for (Attribute attribute : attributes) {
            addAttribute(attribute);
        }
    }

    public DataSet(DataLoader loader) {
        loader.setDataset(this);
        loader.load();
        name = "";
    }

    public void sort(Comparator<DatasetInstance> cmp) {
        int n = instances.size();
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (cmp.compare(instances.get(j - 1), instances.get(j)) > 0) {
                    swapInstances(j - 1, j);
                }
            }
        }
    }

    /**
     * This constructor load DataSet from file. This constructor uses MyLoader
     * class, therefore the format of the load file must agrees with MyLoader
     * format definition.
     *
     * @param path
     * @throws IOException
     */
    public DataSet(String path) throws IOException {
        MyLoader my = new MyLoader(this, path, ",");
        my.load();
    }

    /**
     * Method adds attributes to the dataset. Each attribute stores its data of
     * instances. The data must have the same size as the number of instances in
     * this dataset.
     *
     * @param attrs inserted attributes
     */
    public void addAttributes(Attribute... attrs) {
        for (Attribute attr : attrs) {
            addAttribute(attr);
        }
    }

    /**
     * Method adds attributes to the dataset. Each attribute stores its data of
     * instances. The data must have the same size as the number of instances in
     * this dataset.
     *
     * @param attrs inserted attributes
     */
    public final void addAttributes(Collection<Attribute> attrs) {
        attrs.forEach((attr) -> {
            addAttribute(attr);
            if (attr.isOutputAttr()) {
                setOutputAttr();
            }
        });
    }

    /**
     * Method adds attributes to the dataset. Each attribute stores its data of
     * instances. The data must have the same size as the number of instances in
     * this dataset.
     *
     * @param attrs inserted attributes
     * @param mapAttrInfo if true then collection attrs can change or set the
     * output attribute
     */
    public final void addAttributes(Collection<Attribute> attrs, boolean mapAttrInfo) {
        if (!mapAttrInfo) {
            addAttributes(attrs);
        } else {
            attrs.forEach((attr) -> {
                addAttribute(attr);
                if (attr.isOutputAttr()) {
                    setOutputAttr();
                }
            });
        }
    }

    /**
     * Method adds attribute to the dataset. Each attribute stores its data of
     * instances. The data must have the same size as the number of instances in
     * this dataset.
     *
     * @param attr inserted attribute
     */
    public final void addAttribute(Attribute attr) {
        Attribute a = attr.getRawCopy();
        a.setOutputAttr(false);
        a.setAttributeIndex(attributes.size());
        a.setDataset(this);
        attributes.add(a);
        rowLength += attr.getRowLength();
        addToSublist(a);
        initDatasetInstances();
    }

    /**
     * Method adds attribute to the dataset. Each attribute stores its data of
     * instances. The data must have the same size as the number of instances in
     * this dataset.
     *
     * @param attr inserted attribute
     */
    public final void addAttribute(int index, Attribute attr) {
        Attribute a = attr.getRawCopy();
        a.setOutputAttr(false);
        a.setDataset(this);
        attributes.add(index, a);
        rowLength += attr.getRowLength();
        repairAttributeIndices(index);
        addToSublist(a);
        initDatasetInstances();

    }

    /**
     * Method adds attribute to the dataset.Each attribute stores its data of
     * instances. The data must have the same size as the number of instances in
     * this dataset.
     *
     * @param attr inserted attribute
     * @param output if true, added attribute will be set as the output
     * attribute
     */
    public final void addAttribute(Attribute attr, boolean output) {
        addAttribute(attr);
        if (output) {
            setOutputAttrIndex(getLastAttrIndex());
        }
    }

    /**
     * Method returns all attributes which pass the test condition with true
     * value.
     *
     * @param <T> type of the returned attribute
     * @param test test condition for selecting of attributes
     * @return List of attributes which agrees with the test.
     */
    public <T extends Attribute> List<T> getAttributesIf(Predicate<Attribute> test) {
        ArrayList<T> list = new ArrayList(attributes.size());
        Attribute attribute;
        for (int i = 0; i < attributes.size(); i++) {
            attribute = attributes.get(i);
            if (test.test(attribute)) {
                list.add((T) attribute);
            }
        }
        return list;
    }

    /**
     * Method returns true if dataset contains zero instances (is empty).
     *
     * @return true if data count is equal to zero.
     */
    public boolean isEmpty() {
        return getDataCount() == 0;
    }

    /**
     * Method removes all instances (rows) which pass the test condition with
     * true value. BUG BUG
     *
     * @param test test condition
     */
    public void removeInstanceIf(Predicate<DatasetInstance> test) {
        int index = 0;
        while (getDataCount() > index) {
            DatasetInstance dataInstance = getDatasetInstance(index);
            if (test.test(dataInstance)) {
                removeInstance(index);
            } else {
                index++;
            }
        }
    }

    /**
     * Method instance (row) which at specified index.
     *
     * @param index index of the removed instance
     */
    public void removeInstance(int index) {
        for (Attribute attribute : attributes) {
            attribute.removeRow(index);
        }
        instances.remove(index);
        repairInstancesIndices(index);
    }

    /**
     * Method returns index of the last attribute. This index can be obtained as
     * getAttributes.size() - 1
     *
     * @return index of the last attribute.
     */
    public int getLastAttrIndex() {
        return attributes.size() - 1;
    }

    public List<String> getAttributeNames() {
        ArrayList list = new ArrayList(attributes.size());
        for (Attribute attribute : attributes) {
            list.add(attribute.getName());
        }
        return list;
    }

    /**
     * to MB: getDataMemoryUsage()/8/1000000;
     *
     * @return size of occupied memory by data in bits
     */
    public long getDataMemoryUsage() {
        long usage = 0;
        for (Attribute attribute : attributes) {
            if (attribute.isNumeric()) {
                usage += attribute.numeric().getAttrValue().getValues().getMemorySize();
            } else if (attribute.isLinguistic()) {
                int[] d = attribute.linguistic().getValues().getData();
                usage += MemoryUtils.sizeOf(d);
            } else if (attribute.isFuzzy()) {
                for (AttrValue attrValue : attribute.fuzzy().getDomain()) {
                    usage += attrValue.getValues().getMemorySize();
                }
            }
        }
        return usage;
    }

    /**
     * This method returns number of classes of the output attribute. If output
     * attribute is not set or output attribute is numerical then 0 is returned.
     *
     * @return Number of classes of the output attribute.
     */
    public int getNumberOfClasses() {
        if (!isOutputAttributeSet() || Attribute.isNumeric(outputAttribute)) {
            return 0;
        }
        return this.getOutbputAttribute().getDomain().size();
    }

    /**
     * Method swap instances at specified indices.
     *
     * @param i index of the first instance
     * @param j index of the second instance
     */
    public void swapInstances(int i, int j) {
        instances.get(i).setIndex(j);
        instances.get(j).setIndex(i);
        Collections.swap(instances, i, j);
        for (int p = 0; p < getAtributteCount(); p++) {
            Attribute a = getAttribute(p);
            if (Attribute.isFuzzy(a)) {
                FuzzyAttr attr = (FuzzyAttr) a;
                for (AttrValue attrValue : attr.getDomain()) {
                    Collections.swap(attrValue.getValues(), i, j);
                }
            } else {
                Collections.swap(a.getValues(), i, j);
            }
        }
    }

    /**
     * Method shuffle data randomly
     */
    public void shuffleData() {
        SplittableRandom random = new SplittableRandom(1245888);
        for (int i = 0; i < this.getDataCount(); i++) {
            int randomIndex = i + random.nextInt(this.getDataCount() - i);
            swapInstances(randomIndex, i);
        }
    }

    /**
     * Method shuffle data randomly
     *
     * @param seed for random generator. Setting of this parameter leads to
     * reproducible results
     */
    public void shuffleData(int seed) {
        SplittableRandom random = new SplittableRandom(1245888);
        for (int i = 0; i < this.getDataCount(); i++) {
            int randomIndex = i + random.nextInt(this.getDataCount() - i);
            swapInstances(randomIndex, i);
        }
    }

    /**
     * Method shuffle attributes randomly
     *
     */
    public void shuffleAttrs() {
        SplittableRandom random = new SplittableRandom();
        for (int i = 0; i < attributes.size(); i++) {
            int randomValue = i + random.nextInt(attributes.size() - i);
            swapAttributes(i, randomValue);
        }
    }

    /**
     * Method shuffle attributes randomly
     *
     * @param seed for random generator. Setting of this parameter leads to
     * reproducible results
     */
    public void shuffleAttrs(int seed) {
        SplittableRandom random = new SplittableRandom();
        for (int i = 0; i < attributes.size(); i++) {
            int randomValue = i + random.nextInt(attributes.size() - i);
            swapAttributes(i, randomValue);
        }
    }

    /**
     * This method fills the input array by values at specifics row/instance of
     * this dataset. In case of linguistic attributes, each possible class is
     * represented by zero or one. So, filled array for attribute speed {slow,
     * fast} can look as: [1,0], which means that speed is slow.
     *
     * @param array the array filled by values of row/instance at specified
     * index
     * @param index index of row/instance
     * @param output if true, output attribute will be included. Otherwise, the
     * output attribute is skipped
     * @param clearArray if true, array is filled by zeros before the instance
     * is assigned into this array.
     */
    public void fillArrayByInstances(double[] array, int index, boolean output, boolean clearArray) {
        if (clearArray) {
            ProjectUtils.clearArray(array);
        }
        int i = 0;
        for (Attribute attribute : attributes) {
            if (output == false && attribute.isOutputAttr()) {
                continue;
            } else if (attribute.isLinguistic()) {
                array[i + attribute.linguistic().get(index)] = 1;
                i += attribute.getDomainSize();
            } else if (attribute.isNumeric()) {
                array[i++] = attribute.numeric().get(index);
            } else {
                for (AttrValue attrValue : attribute.fuzzy().getDomain()) {
                    array[i++] = attrValue.get(index);
                }
            }
        }
    }

    private void attrRangeCheck(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > attributes.size() - 1) {
            throw new IndexOutOfBoundsException("\n   Attr Count: " + getAtributteCount() + "\n   Index     : " + index);
        }
    }

    /**
     *
     * @return number of input attributes
     */
    public int getInputAttrCount() {
        if (isOutputAttributeSet()) {
            return attributes.size() - 1;
        } else {
            return attributes.size();
        }
    }

    private void addToSublist(Attribute a) {
        switch (a.getType()) {
            case Attribute.NUMERIC:
                a.setSubListIndex(numAttrs.size());
                numAttrs.add(a.numeric());
                break;
            case Attribute.FUZZY:
                a.setSubListIndex(fuzzyAttrs.size());
                fuzzyAttrs.add(a.fuzzy());
                break;
            case Attribute.LINGUISTIC:
                a.setSubListIndex(liguisticAttrs.size());
                liguisticAttrs.add(a.linguistic());
                break;
        }
    }

    private void repairInstancesIndices(int index) {
        for (int i = index; i < instances.size(); i++) {
            DatasetInstance instance = instances.get(i);
            instance.setIndex(instance.getIndex() - 1);
        }
    }

    private void repairAttributeIndices(int index) {
        for (int i = index; i < attributes.size(); i++) {
            Attribute attr = attributes.get(i);
            attr.setAttributeIndex(i);
            if (attr.isOutputAttr()) {
                outputAttrIndex = i;
            }
        }
    }

    /**
     * Method removes attribute at specified index.
     *
     * @param index of removed attribute.
     * @return removed attribute.
     */
    public Attribute removeAttribue(int index) {
        attrRangeCheck(index);
        Attribute a = attributes.get(index);
        removeFromSublist(a);
        if (a.isOutputAttr()) {
            outputAttrIndex = -1;
            outputAttribute = null;
        } else if (index < outputAttrIndex) {
            --outputAttrIndex;
        }
        rowLength -= a.getRowLength();
        attributes.remove(index);
        for (int i = index; i < getAtributteCount(); i++) {
            getAttribute(i).setAttributeIndex(i);
        }
        a.setAttributeIndex(-1);
        return a;
    }

    /**
     * Method removes specified attribute.
     *
     * @param attr Attribute to be removed.
     * @return removed attribute.
     */
    public Attribute removeAttribue(Attribute attr) {
        return removeAttribue(attr.getAttributeIndex());
    }

    /**
     * Remove all attributes for which the test is true.
     *
     * @param test test whatever a attribute will be removed or not.
     */
    public void removeAttribueIf(Predicate<Attribute> test) {
        ArrayDeque<Integer> indices = new ArrayDeque(attributes.size());
        for (Attribute attribute : attributes) {
            if (test.test(attribute)) {
                indices.add(attribute.getAttributeIndex());
            }
        }
        Iterator<Integer> it = indices.descendingIterator();
        while (it.hasNext()) {
            removeAttribue(it.next());
            it.remove();
        }
    }

    /**
     * Remove attributes at specified indices.
     *
     */
    public void removeAttribue(int... indices) {
        ProjectUtils.quickSort(indices);
        for (int i = indices.length - 1; i >= 0; i--) {
            int index = indices[i];
            attrRangeCheck(index);
            removeAttribue(index);
        }
    }

    /**
     * NO CASE SENSITIVE
     *
     * @param name
     * @return
     */
    public boolean removeAttribue(String name) {
        boolean deleted = false;
        Attribute attribute;
        for (int index = 0; index < attributes.size(); index++) {
            attribute = attributes.get(index);
            if (attribute.getName().trim().equalsIgnoreCase(name.trim())) {
                deleted = true;
                this.removeAttribue(index);
            }
        }
        return deleted;
    }

    /**
     * NO CASE SENSITIVE
     *
     * @param names
     * @return the number of removed attributes
     */
    public int removeAttribue(String... names) {
        int deleted = 0;
        for (String attrName : names) {
            if (removeAttribue(attrName)) {
                deleted++;
            } else {
                System.out.println(attrName + " not exist");
            }
        }
        return deleted;
    }

    /**
     *
     * @param caseSensitive
     * @param names
     * @return the number of removed attributes
     */
    public int removeAttribue(boolean caseSensitive, String... names) {
        int deleted = 0;
        for (String attrName : names) {
            if (removeAttribue(attrName, caseSensitive)) {
                deleted++;
            } else {
                System.out.println(attrName + " not exist");
            }
        }
        return deleted;
    }

    public boolean removeAttribue(String regex, boolean caseSensitive) {
        boolean deleted = false;

        Pattern pattern;
        if (caseSensitive) {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } else {
            pattern = Pattern.compile(regex);
        }

        removeAttribueIf((attr) -> pattern.matcher(attr.getName()).find());
        return deleted;
    }

    private void removeFromSublist(Attribute a) {
        switch (a.getType()) {
            case Attribute.NUMERIC:
                restoreSubListIndices(a.getSubListIndex(), numAttrs);
                numAttrs.remove(a.getSubListIndex());
                break;
            case Attribute.FUZZY:
                restoreSubListIndices(a.getSubListIndex(), fuzzyAttrs);
                fuzzyAttrs.remove(a.getSubListIndex());
                break;
            case Attribute.LINGUISTIC:
                restoreSubListIndices(a.getSubListIndex(), liguisticAttrs);
                liguisticAttrs.remove(a.getSubListIndex());
                break;
        }
        a.setSubListIndex(-1);
    }

    /**
     * must be call before removal
     *
     * @param removedIndex
     */
    private void restoreSubListIndices(int removedIndex, List<? extends Attribute> attrs) {
        for (int i = removedIndex + 1; i < attrs.size(); i++) {
            attrs.get(i).setSubListIndex(i - 1);
        }
    }

    /**
     *
     * @return Numeric attributes of this dataset. The order of returned
     * attributes can be different form order in dataset
     */
    public ArrayList<NumericAttr> getNumericAttrs() {
        return numAttrs;
    }

    /**
     *
     * @return Linguistic attributes of this dataset. The order of returned
     * attributes can be different form order in dataset
     */
    public ArrayList<LinguisticAttr> getLiguisticAttrs() {
        return liguisticAttrs;
    }

    /**
     *
     * @return Fuzzy attributes of this dataset. The order of returned
     * attributes can be different form order in dataset
     */
    public ArrayList<FuzzyAttr> getFuzzyAttrs() {
        return fuzzyAttrs;
    }

    /**
     * Method returns number of columns of the dataset. Each linguistic and
     * numeric attribute has one columns in dataset. In case of fuzzy attribute,
     * the number of its columns corresponds with number its values
     *
     * @return number of columns
     */
    public int getRowLength() {
        return rowLength;
    }

    /**
     *
     * @return index of the output attribute
     */
    public int getOutputAttrIndex() {
        return outputAttrIndex;
    }

    /**
     * Method find and return attribute of the dataset according to attribute
     * name.
     *
     * @param name name of attribute
     * @return Attribute of dataset, or null if attribute with specified name is
     * not present in the dataset.
     */
    public Attribute getAttribute(String name) {
        return getAttribute(name, true);
    }

    /**
     * Method find and return attribute of the dataset according to attribute
     * name.
     *
     * @param name name of attribute
     * @param caseSensitive flag for case sensitive search
     * @return Attribute of dataset, or null if attribute with specified name is
     * not present in the dataset.
     */
    public Attribute getAttribute(String name, boolean caseSensitive) {
        for (int index = 0; index < attributes.size(); index++) {
            Attribute attribute = attributes.get(index);
            if (!caseSensitive) {
                if (attribute.getName().equalsIgnoreCase(name)) {
                    return getAttribute(index);
                }
            } else {
                if (attribute.getName().equals(name)) {
                    return getAttribute(index);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param <T> Type of the returned attribute
     * @param index index of attribute
     * @return Attribute at specified index
     */
    public <T extends Attribute> T getAttribute(int index) {
        return (T) attributes.get(index);
    }

    /**
     * Method creates new ArrayList and all attributes are inserted into them.
     *
     * @param <A>
     * @return all attributes of dataset wrapped into ArrayList
     */
    public <A extends Attribute> List<A> getAttributesInList() {
        List<A> a = new ArrayList<>(attributes.size());
        for (int i = 0; i < attributes.size(); i++) {
            A attr = this.<A>getAttribute(i);
            a.add(attr);
        }
        return a;
    }

    /**
     *
     * @return Unmodifiable List of attributes
     */
    public List<Attribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    /**
     *
     * @param <A> If all attributes are of the same class, then we can use
     * Generic Type
     * @param indices specifies indices of attributes
     * @return attributes on specified indices
     */
    public <A extends Attribute> List<A> getAttributes(int... indices) {
        List a = new ArrayList<>(indices.length);
        for (int i = 0; i < indices.length; i++) {
            a.add(attributes.get(indices[i]));
        }
        return a;
    }

    /**
     *
     * @param attribute
     * @return Index of first attribute with the same name as param attr. If
     * attribute is not present in list, then -1 is returned.
     */
    public int getAttributeIndex(Attribute attribute) {
        return attribute.getAttributeIndex();
    }

    /**
     *
     * @return number of instances (number of rows)
     */
    public int getDataCount() {
        return instances.size();
    }

    //TODO test
    /**
     * Method is case sensitive.
     *
     * @param attrName Name of the attribute.
     * @return index of the attribute with specified name.
     */
    public int getAttributeIndex(String attrName) {
        int i = 0;
        for (; i < attributes.size(); i++) {
            Attribute get = attributes.get(i);
            if (get.getName().equals(attrName)) {
                break;
            }
        }
        return i;
    }

    /**
     *
     * @return number of attributes.
     */
    public int getAtributteCount() {
        return attributes.size();
    }

    /**
     * Method transforms all linguistic attributes for fuzzy attributes.
     * @return this data set
     */
    public DataSet lingvisticToFuzzy() {
        return LingvisticToFuzzy.lingvisticToFuzzy(this);
    }

    /**
     * Method replace attribute by another attribute. Method copy meta data of
     * attribute but instance values are referenced.
     *
     * @param added Attribute which will be added to this dataset
     * @param indexOfReplaced index of the replaced attribute from this dataset
     */
    public void replaceAttribute(Attribute added, int indexOfReplaced) {
        replaceAttribute(added, indexOfReplaced, true);
    }

    /**
     * Method replace attribute by another attribute.
     *
     * @param added Attribute which will be added to this dataset
     * @param indexOfReplaced index of the replaced attribute from this dataset
     * @param makeCopy if true then meta data of the attribute are copied. If
     * the value is false, then is necessary to keep in mind that attribute
     * should not be in one dataset twice, and not be inserted in more than one
     * dataset.
     */
    public void replaceAttribute(Attribute added, int indexOfReplaced, boolean makeCopy) {
        attrRangeCheck(indexOfReplaced);
        Attribute inserted = makeCopy ? added.getRawCopy() : added;
        final Attribute replaced = getAttribute(indexOfReplaced);
        addToSublist(inserted);
        rowLength -= attributes.get(indexOfReplaced).getRowLength();
        inserted.setAttributeIndex(indexOfReplaced);
        inserted.setDataset(this);
        removeFromSublist(replaced);

        replaced.setAttributeIndex(-1);
        attributes.set(indexOfReplaced, inserted);
        rowLength += added.getRowLength();
        if (getOutputAttrIndex() == indexOfReplaced) {
            outputAttrIndex = -1;
            outputAttribute = null;
            inserted.setOutputAttr(false);
        }
    }

    /**
     * Method replaces attribute by another attribute. Remind that attributes
     * represent column of dataset. Method makes copy of meta data of inserted
     * attribute but values of instances (column values) are referenced.
     *
     * @param inserted Attribute which will be added to this dataset
     * @param replaced replaced attribute from this dataset
     */
    public void replaceAttribute(Attribute inserted, Attribute replaced) {
        int index = replaced.getAttributeIndex();
        replaceAttribute(inserted, index);
    }
    
        /**
     * Method replaces attribute by another attribute. Remind that attributes
     * represent column of dataset. Method makes copy of meta data of inserted
     * attribute but values of instances (column values) are referenced.
     *
     * @param inserted Attribute which will be added to this dataset
     * @param replaced replaced attribute from this dataset
     */
    public void replaceAttribute(List<? extends Attribute> inserted, Attribute replaced) {
        
        int index = replaced.getAttributeIndex();
        removeAttribue(replaced);
        for (Attribute attribute : inserted) {
            addAttribute(index++, attribute);
        }
    }

    /**
     * Attribute at specified index will be set as the output attribute. If the
     * output attribute is already set, the old output attribute will be
     * transformed to the input attribute.
     *
     * @param outputAttIndex
     */
    public final void setOutputAttrIndex(int outputAttIndex) {
        if (isOutputAttributeSet()) {
            getOutbputAttribute().setOutputAttr(false);
        }
        attrRangeCheck(outputAttIndex);
        outputAttribute = attributes.get(outputAttIndex);
        outputAttribute.setOutputAttr(true);
        this.outputAttrIndex = outputAttIndex;
    }

    /**
     * The last attribute of the dataset is set as the output attribute.
     */
    public final void setOutputAttr() {
        outputAttribute = attributes.get(getLastAttrIndex());
        outputAttribute.setOutputAttr(true);
        this.outputAttrIndex = getLastAttrIndex();
    }

    /**
     * Method return values of numeric attributes of specified row. Non-numeric
     * attributes are skipped.
     *
     * @param index of row
     * @return values of numeric attributes of specified row
     */
    public List<Double> getRowVector(int index) {
        List<Double> attrs = new ArrayList(getAtributteCount());
        for (int i = 0; i < getAtributteCount(); i++) {
            Attribute attr = getAttribute(i);
            if (Attribute.isNumeric(attr)) {
                attrs.add(((NumericAttr) attr).getAttrValue().get(index));
            } else if (Attribute.isFuzzy(attr)) {
                for (AttrValue attrValue : ((FuzzyAttr) attr).getDomain()) {
                    attrs.add(attrValue.get(index));
                }
            }
        }
        return attrs;
    }

    /**
     * Method swap positions of two attributes at specified indices
     *
     * @param indexAttr1 index of the first attribute
     * @param indexAttr2 index of the second attribute
     */
    public void swapAttributes(final int indexAttr1, final int indexAttr2) {
        final Attribute a1 = attributes.get(indexAttr1);
        final Attribute a2 = attributes.get(indexAttr2);
        a1.setAttributeIndex(indexAttr2);
        a2.setAttributeIndex(indexAttr1);
        if (a1.isOutputAttr()) {
            outputAttrIndex = a1.getAttributeIndex();
        } else if (a2.isOutputAttr()) {
            outputAttrIndex = a2.getAttributeIndex();
        }
        attributes.set(indexAttr1, a2);
        attributes.set(indexAttr2, a1);
    }

    /**
     * Method creates a List of the input attributes.
     *
     * @param <T> type of the input attributes can be specified
     * @return list of the input attributes
     */
    public <T extends Attribute> List<T> getInputAttrs() {
//        if (outputAttrIndex == attributes.size() - 1) {
//            return (List<T>) attributes.subList(0, attributes.size() - 1);
//        }
        List<T> attrs = new ArrayList<>(getInputAttrCount());
        for (int i = 0; i < attributes.size(); i++) {
            T attr = (T) attributes.get(i);
            if (!attr.isOutputAttr()) {
                attrs.add(attr);
            }
        }
        return attrs;
    }

    /**
     * Method return output attribute
     *
     * @param <A> type of the output attribute
     * @return return output attribute
     */
    public <A extends Attribute> A getOutbputAttribute() {
        if (outputAttribute == null) {
            throw new Error("The output attribute is not set");
        }
        return (A) outputAttribute;
    }

    /**
     *
     * @return name of the dataset
     */
    public String getName() {
        return name;
    }

    /**
     * Method set the name of the dataset.
     *
     * @param name new name of the dataset
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(System.lineSeparator());
        sb.append(String.format("%-16s|", "NO. "));
        for (Attribute attribute : attributes) {
            sb.append(String.format("%-16s|", attribute.getName().toUpperCase()));
        }
        sb.append(System.lineSeparator());
        for (int i = 0; i < getDataCount(); i++) {
            sb.append(String.format("%-16s|", Integer.toString(i + 1) + "."));
            for (Attribute attribute : attributes) {
                sb.append(String.format("%-16s|", attribute.getRowString(i)));
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Method prints specified number of lines to the console.
     *
     * @param lineCount number of printed lines.
     */
    public void print(int lineCount) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-16s|", "NO. "));
        for (Attribute attribute : attributes) {
            sb.append(String.format("%-16s|", attribute.getName().toUpperCase()));
        }
        sb.append(System.lineSeparator());
        for (int i = 0; i < lineCount; i++) {
            sb.append(String.format("%-16s|", Integer.toString(i + 1) + "."));
            for (Attribute attribute : attributes) {
                sb.append(String.format("%-16s|", attribute.getRowString(i)));
            }
            sb.append(System.lineSeparator());
        }
        ConsolePrintable.print(sb.toString());
    }

    /**
     * Method prints specified number of lines to the console.
     *
     *
     */
//    @Override
//    public void print() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format("%-16s|", "NO. "));
//        for (Attribute attribute : attributes) {
//            sb.append(String.format("%-16s|", attribute.getName().toUpperCase()));
//        }
//        sb.append(System.lineSeparator());
//        for (int i = 0; i < getDataCount(); i++) {
//            sb.append(String.format("%-16s|", Integer.toString(i + 1) + "."));
//            for (Attribute attribute : attributes) {
//                sb.append(String.format("%-16s|", attribute.getRowString(i)));
//            }
//            sb.append(System.lineSeparator());
//            ProjectUtils.printAndClear(sb, 100000);
//        }
//        ConsolePrintable.print(sb.toString());
//    }

    /**
     * Return string which represent the data of the dataset. Data are separated
     * by specified separator.
     *
     * @param separator of data
     * @return String which represent the data of the dataset
     */
    public String dataToString(String separator) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (int i = 0; i < getDataCount(); i++) {
            for (int j = 0; j < getAtributteCount(); j++) {
                Attribute attribute = getAttribute(j);
                sb.append(attribute.getUnformatedRowString(i));
                if (j < (getAtributteCount() - 1)) {
                    sb.append(separator);
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Method prints data only. Meta data are not printed. This method prints
     * non-formated numbers.
     */
    public void printData() {
        ConsolePrintable.println(dataToString(","));
    }

    /**
     * Method prints basic information about dataset.
     *
     */
    public void printInfo() {
        System.out.printf("%-20s %s %n", "Name:", getName());
        System.out.printf("%-20s %s %n", "Instances:", getDataCount());
        System.out.printf("%-20s %s %n", "Attributes:", getAtributteCount());
        System.out.printf("%-20s %s %n", "   -Numeric:", getNumAttrCount());
        System.out.printf("%-20s %s %n", "   -Lingvistic:", getLngAttrCount());
        System.out.printf("%-20s %s %n", "   -Fuzzy:", getFuzzyAttrCount());
        if (isOutputAttributeSet()) {
            System.out.printf("%-20s %s %n", "Output Attribute", getOutbputAttribute().getName());
            System.out.printf("%-20s %s %n", "   -index:", getOutbputAttribute().getAttributeIndex());
            System.out.printf("%-20s %s %n", "   -type:", getOutbputAttribute().getTypeString());
            if (getOutbputAttribute().isCategorical()) {
                System.out.printf("%-20s %s %n", "   -number of classes:", getOutbputAttribute().getDomainSize());
            }
        }

    }

    /**
     *
     * @return fuzzified dataset
     */
    @Deprecated
    public DataSet getFuzzyDataset() {
        DatasetFuzzificator dtf = new FC_Means_cH(this);
        dtf.setMinTermCount(2);
        return dtf.getFuzzyDataset();
    }

    /**
     *
     * @param termCount
     * @return fuzzified dataset
     */
    @Deprecated
    public DataSet getFuzzyDataset(int termCount) {
        DatasetFuzzificator dtf = new KMeans_withoutOA_wH(this);
        dtf.setMinTermCount(termCount);
        return dtf.getFuzzyDataset();
    }

    /**
     *
     * @param minTermsCount
     * @param maxTermsCount
     * @return fuzzified dataset
     */
    @Deprecated
    public DataSet getFuzzyDataset(int minTermsCount, int maxTermsCount) {
        DatasetFuzzificator dtf = new K_Means_wH(this);
        dtf.setMinTermCount(minTermsCount);
        dtf.setMaxTermCount(maxTermsCount);
        return dtf.getFuzzyDataset();
    }

    /**
     *
     * @return fuzzified dataset
     */
    @Deprecated
    public DataSet getFuzzyDataset(int minTermsCount, int maxTermsCount, int OAterms) {
        DatasetFuzzificator dtf = new FC_Means_cH(this);
        dtf.setMinTermCount(minTermsCount);
        dtf.setMaxTermCount(maxTermsCount);
        dtf.setOutputAttributeTerms(OAterms);
        return dtf.getFuzzyDataset();
    }

    /**
     * Performs the given action for each attribute.
     *
     * @param consumer The action to be performed
     */
    public void forEachAttr(Consumer<Attribute> consumer) {
        attributes.forEach(consumer);
    }

    /**
     * Performs the given action for each attribute which agrees with the test
     *
     * @param test if the action will be performed
     * @param consumer The action to be performed
     */
    public void forEachAttrIf(Predicate<Attribute> test, Consumer<Attribute> consumer) {
        for (Attribute attribute : attributes) {
            if (test.test(attribute)) {
                consumer.accept(attribute);
            }
        }
    }

    /**
     * Performs the given action for each input attribute.
     *
     * @param consumer The action to be performed
     */
    public void forEachInputAttr(Consumer<Attribute> consumer) {
        for (Attribute attribute : attributes) {
            if (!attribute.isOutputAttr()) {
                consumer.accept(attribute);
            }
        }
    }

    /**
     * Performs the given action for each numeric attribute.
     *
     * @param consumer The action to be performed
     */
    public void forEachNumericAttr(Consumer<NumericAttr> consumer) {
        for (Attribute attribute : attributes) {
            if (attribute.isNumeric()) {
                consumer.accept(attribute.numeric());
            }
        }
    }

    /**
     * Performs the given action for each fuzzy attribute.
     *
     * @param consumer The action to be performed
     */
    public void forEachFuzzyAttr(Consumer<FuzzyAttr> consumer) {
        for (Attribute attribute : attributes) {
            if (attribute.isFuzzy()) {
                consumer.accept(attribute.fuzzy());
            }
        }
    }

    /**
     * Performs the given action for each linguistic attribute.
     *
     * @param consumer The action to be performed
     */
    public void forEachLingvisticAttr(Consumer<LinguisticAttr> consumer) {
        for (Attribute attribute : attributes) {
            if (attribute.isLinguistic()) {
                consumer.accept(attribute.linguistic());
            }
        }
    }

    /**
     * This method swap last attribute with the output attribute. If the output
     * attribute is on the last position then nothing happens. Such reordering
     * of attributes can lead to different results of some algorithms. For
     * example, in the case of Decision Tree, if two attributes have the same
     * value of splitting criterion then the first evaluated attribute will be
     * preferred.
     */
    public void reorderAttributes() {
        if (isOutputAttributeSet() && outputAttrIndex != getLastAttrIndex()) {
            this.swapAttributes(outputAttrIndex, getLastAttrIndex());
        }
    }

    /**
     * Method copy of meta data of the dataset without data. This mean that
     * attributes are the same, index of the output attribute is the same, but
     * the data count of the new attribute is equal to zero.
     *
     * @return empty copy of dataset.
     */
    public DataSet getEmptyCopy() {
        DataSet fd = new DataSet();
        for (Attribute attribute : attributes) {
            Attribute a = attribute.getEmptyCopy();
            fd.addAttribute(a);
        }
        if (isOutputAttributeSet()) {
            fd.setOutputAttrIndex(getOutputAttrIndex());
        }
        return fd;
    }

    public DataSet getEmptyCopy(boolean outputAttr) {
        DataSet fd = new DataSet();
        for (Attribute attribute : attributes) {
            if (!outputAttr && attribute.isOutputAttr()) {
                continue;
            }
            Attribute a = attribute.getEmptyCopy();
            fd.addAttribute(a);
        }
        if (outputAttr && isOutputAttributeSet()) {
            fd.setOutputAttrIndex(getOutputAttrIndex());
        }
        return fd;
    }

    public DataSet getCopy(boolean hard) {
        MultiplyDataset dt = new MultiplyDataset(this);
        dt.setHardCopy(hard);
        return dt.getDatasetCopy();
    }

    /**
     * USE DATASETFUZZYFICATOR
     *
     * @param outputAttrTerms -> len pre numericky atribut, ak je lingvisticky,
     * tak si to vypocita samo, podla poctu jeho hodnot
     * @return
     */
    public DataSet toFuzzyDataset(int outputAttrTerms) {
        DatasetFuzzificator dtf = new K_Means_wH(this);
        return dtf.getFuzzyDataset();
    }

    /**
     * Method returns values at specified row (row represents instance of
     * dataset) of dataset in List.
     *
     * @param index of row
     * @return values at specified row of dataset in List.
     */
    public List<Object> getRow(int index) {
        List<Object> values = new ArrayList<>(attributes.size());
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attr = attributes.get(i);
            if (attr.getDataCount() > index) {
                values.add(attr.getRow(index));
            }
        }
        return values;
    }

    /**
     * Method returns instance at specified index. Returned instance is not
     * linked to the dataset. If any value is changed in dataset, returned
     * instance will be not affected. This type of instances is not memory
     * friendly.
     *
     * @param index of returned instance
     * @return instance at specified index
     */
    public NewInstance getInstance(int index) {
        NewInstance i = new NewInstance(this, getRow(index));
        return i;
    }

    public NewInstance getRandomInstance() {
        if (getDataCount() == 0) {
            return null;
        }
        NewInstance i = getInstance(ProjectUtils.randBetween(0, getDataCount() - 1));
        return i;
    }

    public DatasetInstance getRandomInstanceDT() {
        if (getDataCount() == 0) {
            return null;
        }
        DatasetInstance i = getDatasetInstance(ProjectUtils.randBetween(0, getDataCount() - 1));
        return i;
    }

    /**
     * return instance at the specified index
     *
     * @param index of returned instance
     * @return instance at the specified index
     */
    public DatasetInstance getDatasetInstance(int index) {
        return instances.get(index);
    }

    /**
     *
     * @return return the list of instances (not referenced to the dataset)
     */
    public List<Instance> getInstances() {
        ArrayList<Instance> instancesList = new ArrayList<>(getDataCount());
        for (int i = 0; i < getDataCount(); i++) {
            instancesList.add(getDatasetInstance(i));
        }
        return instancesList;
    }

    /**
     *
     * @return return the unmodifiable list of instances
     */
    public List<DatasetInstance> getDatasetInstances() {
        return Collections.unmodifiableList(instances);
    }

    /**
     * Adds one instance to the end of the dataset.
     *
     * @param row specifies values of attributes for inserted instance.
     */
    public void addInstance(List<Object> row) {
        int i = 0;
        for (Attribute attr : attributes) {
            switch (attr.getType()) {
                case Attribute.NUMERIC:
                    ((NumericAttr) attr).addValue((Double) row.get(i++));
                    break;
                case Attribute.LINGUISTIC:
                    ((LinguisticAttr) attr).addValue((String) row.get(i++));
                    break;
                case Attribute.FUZZY:
                    FuzzyAttr a = ((FuzzyAttr) attr);
                    a.addFuzzyRow((List<Double>) row.get(i++));
                    break;
            }
        }
        addDatasetInstance();
    }

    /**
     * Method initialize dataset instance array, id not initialized. Do not use
     * it, it will be removed in the feature. TODO: used in add attribute method
     *
     * @deprecated
     */
    @Deprecated
    public final void initDatasetInstances() {
        if (attributes.size() > 0) {
            final int dataCount = getAttribute(0).getDataCount();
            if (instances.isEmpty() && dataCount > 0) {
                instances = new ArrayList<>(getDataCount());
                for (int i = 0; i < getAttribute(0).getDataCount(); i++) {
                    addDatasetInstance();
                }
            }
        }
    }

    /**
     * Adds one instance to the end of the dataset.
     *
     * @param values specifies values of attributes for inserted instance.
     */
    public void addInstance(Object... values) {
        for (int j = 0; j < attributes.size(); j++) {
            Attribute attr = attributes.get(j);
            switch (attr.getType()) {
                case Attribute.NUMERIC:
                    ((NumericAttr) attr).addValue((double) values[j]);
                    break;
                case Attribute.LINGUISTIC:
                    ((LinguisticAttr) attr).addValue((String) values[j]);
                    break;
                case Attribute.FUZZY:
                    FuzzyAttr a = ((FuzzyAttr) attr);
                    a.addFuzzyRow((List<Double>) values[j]);
                    break;
            }
        }
        addDatasetInstance();
    }

    /**
     * Adds one instance to the end of the dataset. If all attributes is not
     * numerics, the exception is thrown.
     *
     * @param values specifies values of attributes for inserted instance.
     */
    public void addInstanceNumeric(double... values) {
        for (int j = 0; j < attributes.size(); j++) {
            Attribute attr = attributes.get(j);
            switch (attr.getType()) {
                case Attribute.NUMERIC:
                    ((NumericAttr) attr).addValue(values[j]);
                    break;
                default:
                    //TODO remove inserted values
                    throw new Error("All attributes must be numeric");
            }
        }
        addDatasetInstance();
    }

    /**
     * Adds one instance to the end of the dataset. This method try to parse
     * values of attributes from String array. Method is dangerous, because the
     * exception during parsing can lead to the errors in data.
     *
     * @param row specifies values of attributes for inserted instance.
     */
    public void addInstance(String... row) {
        int c = getDataCount();
        if (row.length != rowLength) {
            throw new Error("bad number of values at row index: " + c);

        }
        int attrIndex = 0;
        for (int i = 0; i < row.length;) {
            Attribute attr = attributes.get(attrIndex++);
            switch (attr.getType()) {
                case Attribute.NUMERIC:
                    numAttrs.get(attr.getSubListIndex()).addValue(row[i++]);
                    break;
                case Attribute.LINGUISTIC:
                    liguisticAttrs.get(attr.getSubListIndex()).addValue(row[i++]);
                    break;
                case Attribute.FUZZY:
                    FuzzyAttr a = fuzzyAttrs.get(attr.getSubListIndex());
                    int count = a.getDomainSize();
                    for (int j = 0; j < count; j++) {
                        a.getAttrValue(j).addVaule(Double.parseDouble(row[i++]));
                    }
            }
        }
        addDatasetInstance();
    }

    /**
     * Adds one instance to the end of the dataset.
     *
     * @param instance which are added to the end of the dataset.
     */
    public void addInstance(Instance instance) {
        this.addInstance(instance.getValues());
    }

    private void addDatasetInstance() {
        DatasetInstance di = new DatasetInstance(this, instances.size());
        instances.add(di);
    }

    /**
     * Method creates 2D array in which the values of numerical attributes are
     * stored.
     *
     * @param withOutputAttr if true, the output attribute is ignored
     * @return data in double matrix
     */
    public double[][] getDataInDoubleArray(boolean withOutputAttr) {
        double[][] data;
        int attrc;
        if (!withOutputAttr) {
            if (isOutputAttributeSet()) {
                attrc = getAtributteCount() - 1;
            } else {
                attrc = getAtributteCount();
            }
        } else {
            if (isOutputAttributeSet()) {
                attrc = getAtributteCount();
            } else {
                attrc = getAtributteCount();
            }
        }

        data = new double[getDataCount()][attrc];
        for (int i = 0; i < getDataCount(); i++) { // riadok
            int pom = 0;
            for (int j = 0; j < attrc + pom; j++) { //stlpce
                if (isOutputAttributeSet() && withOutputAttr != true && getOutputAttrIndex() == j) {
                    pom = 1;
                    continue;
                }
                NumericAttr num = getAttribute(j);
                double value = num.getValues().get(i);
                data[i][j - pom] = value;
            }
        }
        return data;
    }

    public void save(String path, DatasetWriter dw) {
        dw.setDataset(this);
        dw.save(path);
    }

    /**
     * Method maps one instance (row) of the dataset at the specified index into
     * double array.
     *
     * @param withOutputAttr
     * @param row index of row (instance) which is mapped into array
     * @return
     */
    public double[] getDataInDoubleArray(boolean withOutputAttr, int row) {
        double[] data;
        int attrc;
        if (!withOutputAttr) {
            if (isOutputAttributeSet()) {
                attrc = getNumAttrCount() - 1;
            } else {
                attrc = getNumAttrCount();
            }
        } else {
            if (isOutputAttributeSet()) {
                attrc = getNumAttrCount();
            } else {
                attrc = getNumAttrCount();
            }
        }

        data = new double[attrc];
        int pom = 0;
        for (int j = 0; j < attrc + pom; j++) { //stlpce
            if (isOutputAttributeSet() && withOutputAttr != true && getOutputAttrIndex() == j) {
                pom = 1;
                continue;
            }
            if (getAttribute(j).isNumeric()) {
                NumericAttr num = getAttribute(j);
                double value = num.getValues().get(row);
                data[j - pom] = value;
            }
        }
        return data;
    }

    /**
     * ONLY NUMERIC ATTRIBUTES.
     *
     * @param row
     * @param data
     */
    public void getDataInDoubleArray(int row, double[] data) {
        NumericAttr num;
        for (int j = 0; j < getAtributteCount(); j++) { //stlpce
            if (Attribute.isNumeric(attributes.get(j))) {
                num = attributes.get(j).numeric();
                double value = num.get(row);
                data[j] = value;
            }
        }
    }

    /**
     * Method creates copy of all attributes without values.
     *
     * @return List of copied attributes without values.
     */
    public List<Attribute> getEmptyAttributes() {
        List<Attribute> attrs = new ArrayList<>();
        attributes.forEach((attr) -> {
            attrs.add(attr.getEmptyCopy());
        });
        return attrs;
    }

    /**
     *
     * @return the number of numerical attributes.
     */
    public int getNumAttrCount() {
        return numAttrs.size();
    }

    /**
     *
     * @return the number of fuzzy attributes.
     */
    public int getFuzzyAttrCount() {
        return fuzzyAttrs.size();
    }

    /**
     *
     * @return the number of linguistic attributes.
     */
    public int getLngAttrCount() {
        return liguisticAttrs.size();
    }

    /**
     *
     * @return iterator over dataset instances.
     */
    @Override
    public Iterator<DatasetInstance> iterator() {
        return instances.iterator();
    }

    /**
     *
     * @param test is used for select instances which agrees with the condition
     * only.
     * @return copy of dataset, where only instances which hold the test
     * condition are added.
     */
    public DataSet where(Predicate<DatasetInstance> test) {
        DataSet dt = getEmptyCopy();
        for (int i = 0; i < getDataCount(); i++) {
            DatasetInstance instance = getDatasetInstance(i);
            if (test.test(instance)) {
                dt.addInstance(instance);
            }
        }
        return dt;
    }

    /**
     *
     * @return true if the output attribute is set, otherwise false
     */
    public boolean isOutputAttributeSet() {
        return outputAttrIndex != -1;
    }

    /**
     *
     * @return true if the output attribute is set, otherwise false
     */
    public boolean hasOutputAttr() {
        return outputAttrIndex != -1;
    }

    public static void main(String[] args) throws IOException {
//        DataSet dt = DatasetFactory.getANDDataSet(100000);
//
//        dt.print();
//        double[] arr = new double[9];
//        for (DatasetInstance datasetInstance : dt) {
//            dt.fillArrayByInstances(arr, datasetInstance.getIndex(), true, true);
//            System.out.println(Arrays.toString(arr));
//        }
        DataSet dt = new DataSet(new LinguisticAttr("1"), new LinguisticAttr("2"), new LinguisticAttr("3"));
        dt.addAttribute(2, new LinguisticAttr("aa"));

        dt.forEachAttr((t) -> {
            System.out.println("attr:" + t.getName() + "   " + t.getAttributeIndex());
        });
    }

    //-------------------------------------STATIC METHODS------------------------------
    /**
     * Methods transforms linguistics attributes of dataset to numeric
     * attributes, where value is numerical word (for example 2.5). If word is
     * not numeric (for example "January"), than NaN value is used in
     * transformed attribute.
     *
     * @param dt
     */
    public static void lingvisticToNumeric(DataSet dt) {
        for (int i = 0; i < dt.getAtributteCount() - 1; i++) {
            Attribute attr = dt.getAttribute(i);
            LingvisticToNumericParser ltn = new LingvisticToNumericParser(attr);
            dt.replaceAttribute(ltn.toNumeric(), i);
        }
    }

}
