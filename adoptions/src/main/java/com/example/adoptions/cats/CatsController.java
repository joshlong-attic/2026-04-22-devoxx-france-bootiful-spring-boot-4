package com.example.adoptions.cats;

import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.registry.ImportHttpServices;

import java.util.concurrent.atomic.AtomicInteger;

@ImportHttpServices(CatsClient.class)
@Controller
@ResponseBody
class CatsController {

    private final CatsClient catsClient;

    private final AtomicInteger counter = new AtomicInteger(0);

    CatsController(CatsClient catsClient) {
        this.catsClient = catsClient;
    }

    @ConcurrencyLimit(10)
    @Retryable(maxRetries = 5, includes = IllegalStateException.class)
    @GetMapping("/cats")
    CatFacts facts() {
        if (this.counter.getAndIncrement() < 4) {
            IO.println("oops!");
            throw new IllegalStateException("oops!");
        }
        IO.println("yay!");
        return this.catsClient.cats();
    }
}
