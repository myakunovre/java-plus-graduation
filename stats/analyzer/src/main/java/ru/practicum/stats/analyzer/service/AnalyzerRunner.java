package ru.practicum.stats.analyzer.service;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {
    final UserActionProcessor userActionProcessor;
    final EventSimilarityProcessor eventSimilarityProcessor;

    @Override
    public void run(String... args) throws Exception {
        Thread userActionThread = new Thread(userActionProcessor);
        Thread eventSimilarityThread = new Thread(eventSimilarityProcessor);

        userActionThread.setName("UserActionHandlerThread");
        eventSimilarityThread.setName("EventSimilarityThread");

        userActionThread.start();
        eventSimilarityThread.start();
    }
}
