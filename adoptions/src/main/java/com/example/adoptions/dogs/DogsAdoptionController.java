package com.example.adoptions.dogs;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Transactional
@ResponseBody
class DogsAdoptionController {

    private final DogRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    DogsAdoptionController(DogRepository repository, ApplicationEventPublisher applicationEventPublisher) {
        this.repository = repository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @GetMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable int dogId, @RequestParam String owner) {

        this.repository.findById(dogId)
                .ifPresent(dog -> {
                    var updated = new Dog(
                            dog.id(),
                            dog.name(),
                            owner,
                            dog.description()
                    );
                    this.repository.save(updated);
                    IO.println("adopted " + updated);

                    applicationEventPublisher
                            .publishEvent(new DogAdoptedEvent(dogId));
                });
    }

}
