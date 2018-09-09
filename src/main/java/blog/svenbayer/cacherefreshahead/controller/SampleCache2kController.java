package blog.svenbayer.cacherefreshahead.controller;

import blog.svenbayer.cacherefreshahead.services.SampleCache2kService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleCache2kController {

    private static final Logger logger = LoggerFactory.getLogger(SampleCache2kController.class);

    private SampleCache2kService sampleCache2kService;

    @Autowired
    public SampleCache2kController(SampleCache2kService sampleCache2kService) {
        this.sampleCache2kService = sampleCache2kService;
    }

    @GetMapping("/longrunCaffeine/{value}")
    public ResponseEntity<String> longrun(@PathVariable String value) throws InterruptedException {
        logger.info("Executing Controller method with long running action for value '{}'", value);
        // simulating a long running action
        String result = sampleCache2kService.longRunningSimulation(value);
        return ResponseEntity.ok(result);
    }

    // TODO Reactive Endpoint
}
