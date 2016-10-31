package com.lori.core.gate.lori.dto.request.base;

import com.lori.core.gate.lori.dto.BaseEntityDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author artemik
 */
public class CommitRequest<T extends BaseEntityDto> {
    private List<T> commitInstances = new ArrayList<>();
    private List<T> removeInstances = new ArrayList<>();
    private boolean softDeletion = true;

    public CommitRequest(List<T> commitInstances) {
        if (commitInstances == null) {
            return;
        }
        this.commitInstances.addAll(commitInstances);
    }

    public CommitRequest(List<T> commitInstances, List<T> removeInstances) {
        this(commitInstances);
        if (removeInstances == null) {
            return;
        }
        this.removeInstances.addAll(removeInstances);
    }

    public List<T> getCommitInstances() {
        return commitInstances;
    }

    public void setCommitInstances(List<T> commitInstances) {
        this.commitInstances = commitInstances;
    }

    public List<T> getRemoveInstances() {
        return removeInstances;
    }

    public void setRemoveInstances(List<T> removeInstances) {
        this.removeInstances = removeInstances;
    }

    public boolean isSoftDeletion() {
        return softDeletion;
    }

    public void setSoftDeletion(boolean softDeletion) {
        this.softDeletion = softDeletion;
    }
}
