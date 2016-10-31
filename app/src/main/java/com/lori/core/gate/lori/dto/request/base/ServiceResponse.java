package com.lori.core.gate.lori.dto.request.base;

/**
 * @author artemik
 */
public class ServiceResponse<T> {
    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
