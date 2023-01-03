package org.fightjc.xybot.service;

import org.fightjc.xybot.model.entity.User;

public interface UserService {
    void createUser(User user);

    User login(String username, String password);
}
