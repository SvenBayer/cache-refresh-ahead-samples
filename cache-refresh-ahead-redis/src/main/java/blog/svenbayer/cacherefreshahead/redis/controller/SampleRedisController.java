package blog.svenbayer.cacherefreshahead.redis.controller;

import blog.svenbayer.cacherefreshahead.redis.services.SampleRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleRedisController {

    private static final Logger logger = LoggerFactory.getLogger(SampleRedisController.class);

    private SampleRedisService sampleRedisService;

    @Autowired
    public SampleRedisController(SampleRedisService sampleRedisService) {
        this.sampleRedisService = sampleRedisService;
    }

    @GetMapping("/longrun/{value}")
    public ResponseEntity<String> longrun(@PathVariable String value) throws InterruptedException {
        logger.info("Executing Controller method with long running action for value '{}'", value);
        // simulating a long running action
        String result = sampleRedisService.longRunningSimulation(value);
        return ResponseEntity.ok(result);
    }
}
