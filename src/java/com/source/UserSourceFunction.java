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
