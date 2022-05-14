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

package com.service;

import com.domain.AgeStatistic;
import com.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

@Slf4j
public class AgeStatisticFunction extends RichFlatMapFunction<User, AgeStatistic> {
    private transient ValueState<AgeStatistic> statisticsValueState;

    @Override
    public void flatMap(User user, Collector<AgeStatistic> out) throws Exception {
        AgeStatistic currentValue = statisticsValueState.value();

        if (currentValue.getAge() == 0) {
            currentValue.setAge(user.getAge());
        }

        currentValue.setAmount(currentValue.getAmount() + 1);
        statisticsValueState.update(currentValue);

        log.warn(currentValue.toString());
        out.collect(new AgeStatistic(currentValue.getAge(), currentValue.getAmount()));
        //sum.clear();
    }

    @Override
    public void open(Configuration config) {
        ValueStateDescriptor<AgeStatistic> descriptor =
                new ValueStateDescriptor<>("age-statistics", TypeInformation.of(new TypeHint<>() {
                }),
                        new AgeStatistic());
        statisticsValueState = getRuntimeContext().getState(descriptor);
    }
}
