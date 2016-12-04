package com.lori.core.entity;

/**
 * @author artemik
 */
public class ActivityType extends BaseEntity {
    private static final long serialVersionUID = -6759954596337387675L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
