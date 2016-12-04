package com.lori.core.entity;

/**
 * @author artemik
 */
public class User extends BaseEntity {
    private static final long serialVersionUID = -5568062043653083891L;

    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
