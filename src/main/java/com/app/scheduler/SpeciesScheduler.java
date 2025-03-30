package com.app.scheduler;

import com.app.domain.entities.Species;
import com.app.services.SpeciesService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class SpeciesScheduler {
    private final SpeciesService speciesService;

    public SpeciesScheduler(SpeciesService speciesService) {
        this.speciesService = speciesService;
    }

    @Scheduled(cron = "0 24 22 * * *")
    public void cleanUpUnapprovedSpecies() {
        List<Species> list = speciesService
                .getAllUnapproved()
                .stream()
                .filter(s -> s.getAddedOn().isBefore(LocalDate.now().minusDays(30L)))
                .toList();

        list.forEach(s -> speciesService.delete(s.getId()));
    }
}
