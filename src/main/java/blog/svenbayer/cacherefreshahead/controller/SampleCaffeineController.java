package blog.svenbayer.cacherefreshahead.controller;

import blog.svenbayer.cacherefreshahead.services.SampleCaffeineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleCaffeineController {

    private static final Logger logger = LoggerFactory.getLogger(SampleCaffeineController.class);

    private SampleCaffeineService sampleCaffeineService;

    @Autowired
    public SampleCaffeineController(SampleCaffeineService sampleCaffeineService) {
        this.sampleCaffeineService = sampleCaffeineService;
    }

    @GetMapping("/longrunCaffeine/{value}")
    public ResponseEntity<String> longrun(@PathVariable String value) throws InterruptedException {
        logger.info("Executing Controller method with long running action for value '{}'", value);
        // simulating a long running action
        String result = sampleCaffeineService.longRunningSimulation(value);
        return ResponseEntity.ok(result);
    }

    // TODO Reactive Endpoint
}
