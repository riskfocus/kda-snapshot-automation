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
under the License. */

package com.source;

import com.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import static java.lang.Long.parseLong;

@Slf4j
public class UserSourceFunction implements SourceFunction<User> {
    private final long endTime;
    private final int pause;
    public final UserGenerator userGenerator;

    public UserSourceFunction(String endTime, String pause) {
        log.warn("The generateUsersTimer parameter is {} seconds", endTime);
        log.warn("The pauseBetweenUserGeneration parameter is {} ms", pause);
        this.endTime = parseLong(endTime) * 1000 + System.currentTimeMillis();
        this.pause = Integer.parseInt(pause);
        this.userGenerator = new UserGenerator();
    }

    @Override
    public void run(SourceContext<User> ctx) throws Exception {
        while (true) {
            while (System.currentTimeMillis() < endTime) {
                ctx.collect(userGenerator.generateUser());
                Thread.sleep(pause);
            }
        }
    }

    @Override
    public void cancel() {
    }
}
