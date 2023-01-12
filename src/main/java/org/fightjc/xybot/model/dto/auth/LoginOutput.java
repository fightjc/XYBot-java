package org.fightjc.xybot.model.dto.auth;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class LoginOutput {

    private String username;

    private String role;

    private String token;
}
