package com;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.service.UserAgeService;
import com.utils.ParameterToolUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class StreamService {
    public static void main(String[] args) throws IOException {
        ParameterTool parameter;
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        if (env instanceof LocalStreamEnvironment) {
            parameter = ParameterTool.fromArgs(args);
        } else {
            Map<String, Properties> applicationProperties = KinesisAnalyticsRuntime.getApplicationProperties();
            Properties flinkProperties = applicationProperties.get("FlinkApplicationProperties");
//            if (flinkProperties == null) {
//                throw new RuntimeException("Unable to load FlinkApplicationProperties properties from the runtime.");
//            }
            parameter = ParameterToolUtils.fromApplicationProperties(flinkProperties);
        }

        new UserAgeService(parameter, env).start();
    }
}
