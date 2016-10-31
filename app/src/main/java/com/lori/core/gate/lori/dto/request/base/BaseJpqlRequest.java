package com.lori.core.gate.lori.dto.request.base;

import java.util.ArrayList;
import java.util.List;

/**
 * @author artemik
 */
public class BaseJpqlRequest {
    private final String entity;
    private final String query;
    private final List<Param> params;

    public BaseJpqlRequest(String entity, String query, Object... paramPairs) {
        this.entity = entity;
        this.query = query;

        if (paramPairs != null) {
            params = new ArrayList<>();
            for (int i = 0; i < paramPairs.length; i += 2) {
                params.add(new Param((String) paramPairs[i], paramPairs[i + 1]));
            }
        } else {
            params = null;
        }
    }

    public String getEntity() {
        return entity;
    }

    public String getQuery() {
        return query;
    }

    public List<Param> getParams() {
        return params;
    }

    public static class Param {
        private String name;
        private Object value;

        public Param(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }
}
