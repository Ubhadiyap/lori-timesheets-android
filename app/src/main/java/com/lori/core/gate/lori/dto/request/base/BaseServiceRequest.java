package com.lori.core.gate.lori.dto.request.base;

import java.util.HashMap;
import java.util.Map;

/**
 * @author artemik
 */
public class BaseServiceRequest {
    private static final String PARAM_PREFIX = "param";

    private final String service;
    private final String method;
    private final String view;
    private final Map<String, Object> params;

    public BaseServiceRequest(String service, String method, String view, Object... paramsList) {
        this.service = service;
        this.method = method;
        this.view = view;

        if (paramsList != null) {
            params = new HashMap<>();
            for (int i = 0; i < paramsList.length; i++) {
                params.put(PARAM_PREFIX + i, paramsList[i]);
            }
        } else {
            params = null;
        }
    }

    public String getService() {
        return service;
    }

    public String getMethod() {
        return method;
    }

    public String getView() {
        return view;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}