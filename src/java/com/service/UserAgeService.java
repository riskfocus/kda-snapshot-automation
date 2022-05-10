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

package com.service;

import com.domain.AgeStatistic;
import com.domain.User;
import com.source.UserSourceFunction;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;

@Slf4j
public class UserAgeService {
    private final ParameterTool params;
    private final StreamExecutionEnvironment env;

    public UserAgeService(ParameterTool params, StreamExecutionEnvironment env) {
        this.params = params;
        this.env = env;
    }

    @SneakyThrows
    public void start() {
        this.env.execute(process());
    }

    private StreamGraph process() {
        env.getConfig().setGlobalJobParameters(params);
        env.setParallelism(1);

        DataStreamSource<User> userStream = env.addSource(
                new UserSourceFunction(params.get("generateUsersTimer", "15"),
                        params.get("pauseBetweenUserGeneration", "1000")));

        SingleOutputStreamOperator<Long> amountOfUser = userStream
                .uid("user-source2")
                .map(x -> 1L)
                .keyBy(x -> 1L)
                .flatMap(new AmountOfUsersFunction())
                .uid("amount-of-user");

        SingleOutputStreamOperator<AgeStatistic> ageStatistics = userStream
                .uid("user-source1")
                .keyBy(User::getAge)
                .flatMap(new AgeStatisticFunction())
                .uid("age-statistic");

        StreamGraph streamGraph = env.getStreamGraph();
        streamGraph.setJobName("user-service");
        return streamGraph;
    }
}
