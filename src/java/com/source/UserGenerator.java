package com.source;

import com.domain.User;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class UserGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String Kate = "Kate";
    private final String Jon = "Jon";
    private final String Bob = "Bob";
    private final String Peter = "Peter";
    private final String Rob = "Rob";
    private final Random rand = new Random();

    private final List<String> names = new ArrayList<>();

    {
        names.add(Kate);
        names.add(Jon);
        names.add(Bob);
        names.add(Peter);
        names.add(Rob);
    }

    public User generateUser() {
        int age = rand.nextInt((10 - 1) + 1) + 1;
        String name = names.get(age % names.size());
        User user = new User(name, age);
        log.warn("Created a new user with name {} and age {}", user.getName(), user.getAge());
        return user;
    }
}
