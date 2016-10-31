package com.lori.core.gate.lori.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lori.core.entity.BaseEntity;
import com.lori.core.gate.lori.LoriGate;

import java.util.UUID;

/**
 * @author artemik
 */
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LoriGate.DATE_TIME_FORMAT)
public abstract class BaseEntityDto {
    private static final String NEW_PREFIX = "NEW";

    @JsonIgnore
    protected UUID id;

    @JsonIgnore
    protected boolean isNew;

    public BaseEntityDto() {
    }

    public BaseEntityDto(BaseEntity entity) {
        id = entity.getId();
    }

    protected abstract String getEntityClassName();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @JsonProperty("id")
    public String getJsonId() {
        if (id == null) {
            return null;
        }

        String jsonId = getEntityClassName() + "-" + id.toString();

        if (isNew) {
            jsonId = NEW_PREFIX + "-" + jsonId;
        }

        return jsonId;
    }

    @JsonProperty("id")
    public void setJsonId(String jsonId) {
        if (jsonId != null) {
            String uuid = jsonId.substring(jsonId.indexOf("-") + 1, jsonId.length());
            id = UUID.fromString(uuid);
        }
    }
}
