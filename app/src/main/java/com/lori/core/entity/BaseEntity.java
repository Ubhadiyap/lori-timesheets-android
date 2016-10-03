package com.lori.core.entity;

import java.util.UUID;

/**
 * @author artemik
 */
public class BaseEntity {
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
