package edu.utexas.jdumper.writer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map program constructs to their indices
 */
class IndexMap<Elem>
{
    private Map<Elem, Integer> indexMap = new HashMap<>();
    private int nextIndex = 0;

    int getIndex(Elem elem)
    {
        Integer ret = indexMap.get(elem);
        if (ret == null)
        {
            ret = nextIndex;
            indexMap.put(elem, nextIndex);
            ++nextIndex;
        }
        return ret;
    }

    int getIndexOrFail(Elem elem)
    {
        Integer ret = indexMap.get(elem);
        if (ret == null)
            throw new RuntimeException("Element lookup failed in IndexMap");
        return ret;
    }

    boolean exist(Elem elem)
    {
        return indexMap.get(elem) != null;
    }

    int peekNextIndex()
    {
        return nextIndex;
    }

    Set<Elem> keySet() {
        return indexMap.keySet();
    }

    // Grab a fresh index without assigning it to any element
    int getNextIndex()
    {
        int ret = nextIndex;
        ++nextIndex;
        return ret;
    }
}
