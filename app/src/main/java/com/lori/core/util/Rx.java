package com.lori.core.util;

import java.util.List;

/**
 * @author artemik
 */
public class Rx {
    public static <T> T getFirst(List<T> collection) {
        return collection == null || collection.size() == 0 ?
                null :
                collection.iterator().next();
    }
}
