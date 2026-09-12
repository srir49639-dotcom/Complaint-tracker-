package interfaces;

import datastructures.CustomGraph;
import datastructures.CustomArrayList;

public interface GraphAlgorithm<V> {
    CustomArrayList<V> execute(CustomGraph<V> graph, V source);

    String getName();

    String getTimeComplexity();

    String getSpaceComplexity();
}
