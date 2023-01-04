package org.fightjc.xybot.model.dto.user;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class LoginOutput {

    private String username;

    private String token;
}
