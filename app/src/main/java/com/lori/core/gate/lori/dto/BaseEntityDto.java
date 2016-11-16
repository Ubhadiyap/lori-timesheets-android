package com.lori.core.gate.lori.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lori.core.entity.BaseEntity;
import com.lori.core.gate.lori.LoriGate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author artemik
 */
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LoriGate.DATE_TIME_FORMAT)
public abstract class BaseEntityDto {
    private static final String NEW_PREFIX = "NEW";

    @Getter(onMethod=@__({@JsonIgnore}))
    @Setter
    protected UUID id;

    @Getter(onMethod=@__({@JsonIgnore}))
    @Setter
    protected boolean isNew;

    public BaseEntityDto() {
    }

    public BaseEntityDto(BaseEntity entity) {
        id = entity.getId();
    }

    protected abstract String getEntityClassName();

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
