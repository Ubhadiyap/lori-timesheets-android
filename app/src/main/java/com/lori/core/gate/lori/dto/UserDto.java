package com.lori.core.gate.lori.dto;

import com.lori.core.entity.User;

/**
 * @author artemik
 */
public class UserDto extends BaseEntityDto {
    private String login;

    public UserDto() {
    }

    public UserDto(User user) {
        super(user);
        login = user.getLogin();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    protected String getEntityClassName() {
        return "ts$ExtUser";
    }
}
