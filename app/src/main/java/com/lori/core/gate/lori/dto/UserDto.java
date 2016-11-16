package com.lori.core.gate.lori.dto;

import com.lori.core.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author artemik
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDto extends BaseEntityDto {
    private String login;

    public UserDto(User user) {
        super(user);
        login = user.getLogin();
    }

    @Override
    protected String getEntityClassName() {
        return "ts$ExtUser";
    }
}
