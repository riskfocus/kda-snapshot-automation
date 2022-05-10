/* Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
*/ under the License.

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
