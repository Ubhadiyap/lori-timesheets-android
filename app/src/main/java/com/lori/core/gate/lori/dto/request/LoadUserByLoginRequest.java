package com.lori.core.gate.lori.dto.request;

import com.lori.core.gate.lori.dto.request.base.BaseJpqlRequest;

/**
 * @author artemik
 */
public class LoadUserByLoginRequest extends BaseJpqlRequest {
    public LoadUserByLoginRequest(String login) {
        super(
                "ts$ExtUser",
                "select u from ts$ExtUser u where u.login = :login",
                "login", login
        );
    }
}
