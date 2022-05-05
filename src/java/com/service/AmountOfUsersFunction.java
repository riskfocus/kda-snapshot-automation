package com.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.util.Collector;

import java.io.IOException;

@Slf4j
public class AmountOfUsersFunction extends RichFlatMapFunction<Long, Long> implements CheckpointedFunction {
    private transient ValueState<Long> amountOfUser;
    private transient boolean isRestored;

    @Override
    public void flatMap(Long user, Collector<Long> collector) throws IOException {
        if(isRestored){
            log.warn("The amount of processed users after context restore is {}", amountOfUser.value());
            isRestored = false;
        }

        Long currentValue = amountOfUser.value();
        ++currentValue;

        log.warn("Amount of users: {}", currentValue);
        amountOfUser.update(currentValue);

        collector.collect(amountOfUser.value());
    }

    @Override
    public void open(Configuration config) {
        ValueStateDescriptor<Long> descriptor =
                new ValueStateDescriptor<>("total-users", TypeInformation.of(new TypeHint<>() {
                }), 0L);
        amountOfUser = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) {
    }

    @Override
    public void initializeState(FunctionInitializationContext context) {
        this.isRestored = context.isRestored();
        if (isRestored) {
            log.warn("The application was restored from context");
        } else {
            log.warn("The application was not restored from context");
        }
    }
}
