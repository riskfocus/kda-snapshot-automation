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
