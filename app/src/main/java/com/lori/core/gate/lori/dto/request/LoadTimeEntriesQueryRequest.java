package com.lori.core.gate.lori.dto.request;

import com.lori.core.gate.lori.dto.UserDto;
import com.lori.core.gate.lori.dto.request.base.BaseJpqlRequest;

import java.util.Date;

/**
 * @author artemik
 */
public class LoadTimeEntriesQueryRequest extends BaseJpqlRequest {
    public LoadTimeEntriesQueryRequest(Date start, Date end, UserDto user) {
        super(
                "ts$TimeEntry",
                "select te from ts$TimeEntry te where te.user.id = :userId and (te.date between :start and :end)",
                "userId", user.getId(),
                "start", start,
                "end", end
        );
    }
}
