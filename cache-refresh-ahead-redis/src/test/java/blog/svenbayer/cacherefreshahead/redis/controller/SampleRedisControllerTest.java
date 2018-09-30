package blog.svenbayer.cacherefreshahead.redis.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class SampleRedisControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(SampleRedisControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void longrun() throws Exception {
        Instant testStart = Instant.now();
        int max = 50;
        for (int i = 0; i < max; i++) {
            logger.info("Iteration '{}'", i);
            Instant start = Instant.now();
            long testTimePassed = Duration.between(testStart, start).getSeconds();

            if (testTimePassed < 8) {
                logger.info("Calling long running endpoints for 'World'");
                mockMvc.perform(MockMvcRequestBuilders.get("/longrun/World"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Hello World"));
            }

            if (testTimePassed < 16) {
                logger.info("Calling long running endpoints for 'SpringBoot'");
                mockMvc.perform(MockMvcRequestBuilders.get("/longrun/SpringBoot"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Hello SpringBoot"));
            }

            if (testTimePassed < 17) {
                logger.info("Calling long running endpoints for 'Java'");
                mockMvc.perform(MockMvcRequestBuilders.get("/longrun/Java"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Hello Java"));
            }

            if (i > 0) {
                Instant finish = Instant.now();
                long durationSeconds = Duration.between(start, finish).getSeconds();
                assertTrue("Passed seconds was: " + durationSeconds, durationSeconds <= 1);
            }
            if (testTimePassed >= 17) {
                break;
            }
            TimeUnit.SECONDS.sleep(1L);
        }
    }
}