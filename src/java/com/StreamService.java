/* 
Licensed to the Apache Software Foundation (ASF) under one
Copyright 2022 Ness USA, Inc. 

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
under the License. 
*/

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
