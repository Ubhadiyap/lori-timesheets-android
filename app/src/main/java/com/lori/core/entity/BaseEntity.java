package com.lori.core.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author artemik
 */
public abstract class BaseEntity implements Serializable {
    private UUID id;

    public BaseEntity() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
