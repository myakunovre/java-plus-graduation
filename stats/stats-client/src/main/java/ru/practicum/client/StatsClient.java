//package ru.practicum.client;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.retry.backoff.FixedBackOffPolicy;
//import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
//import org.springframework.retry.support.RetryTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//import ru.practicum.client.exception.StatisticClientException;
//import ru.practicum.dto.in.StatisticDto;
//import ru.practicum.dto.output.GetStatisticDto;
//
//import java.net.URI;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Collections;
//import java.util.List;
//
////@Component
//@Slf4j
//@Service
//public class StatsClient {
//    private final DiscoveryClient discoveryClient;
//    private final RetryTemplate retryTemplate;
//    private final String application;
//    private final String statsServiceId;
//    private final ObjectMapper json;
//    private final RestClient restClient;
//
//    @Autowired
//    public StatsClient(DiscoveryClient discoveryClient,
//                       @Value("main-service") String application,
////            @Value("${STATS_SERVER_URL}") String statsUrl),
//                       @Value("${discovery.services.stats-server-id}") String getStatsServiceId,
//                       ObjectMapper json) {
//
//        this.discoveryClient = discoveryClient;
//        this.application = application;
//        this.statsServiceId = getStatsServiceId;
//        this.json = json;
//        this.restClient = RestClient.builder()
////                .baseUrl(statsUrl)
//                .defaultStatusHandler(
//                        HttpStatusCode::isError,
//                        (request, response) -> {
//                            throw new StatisticClientException("Statistics service error: " + response.getStatusText());
//                        })
//                .build();
//
//        this.retryTemplate = new RetryTemplate();
//
//        RetryTemplate retryTemplate = new RetryTemplate();
//
//        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
//        fixedBackOffPolicy.setBackOffPeriod(3000L);
//        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
//
//        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
//        retryPolicy.setMaxAttempts(3);
//        retryTemplate.setRetryPolicy(retryPolicy);
//    }
//
//    public void hit(StatisticDto statisticDto) {
//        try {
//            log.info("Sending statistics hit request to client");
//            restClient.post()
//                    .uri("/hit")
//                    .body(statisticDto)
//                    .retrieve()
//                    .toBodilessEntity();
//        } catch (Exception e) {
//            log.error("Error saving statistics in client: {}, {}", statisticDto, e.getMessage());
//            throw new StatisticClientException("Error sending statistics", e);
//        }
//    }
//
//    public void hit(HttpServletRequest userRequest) {
//        EndpointHit hit = E
//        try {
//            log.info("Sending statistics hit request to client");
//            restClient.post()
//                    .uri("/hit")
//                    .body(statisticDto)
//                    .retrieve()
//                    .toBodilessEntity();
//        } catch (Exception e) {
//            log.error("Error saving statistics in client: {}, {}", statisticDto, e.getMessage());
//            throw new StatisticClientException("Error sending statistics", e);
//        }
//    }
//
//
//    public List<GetStatisticDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
//        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        try {
//            log.info("Requesting statistics from client");
//            return restClient
//                    .get()
//                    .uri(uriBuilder -> uriBuilder.path("/stats")
//                            .queryParam("start", start.format(dateTimeFormat))
//                            .queryParam("end", end.format(dateTimeFormat))
//                            .queryParam("uris", uris != null ? uris : Collections.emptyList())
//                            .queryParam("unique", unique)
//                            .build())
//                    .retrieve()
//                    .body(new ParameterizedTypeReference<>() {
//                    });
//        } catch (Exception e) {
//            log.error("Error retrieving statistics from client: {}", e.getMessage());
//            throw new StatisticClientException("Error getting statistics", e);
//        }
//    }
//
//    private String encode(String value) {
//        return URLEncoder.encode(value, StandardCharsets.UTF_8);
//    }
//
//    private URI makeUri(String path) {
//        ServiceInstance instance = retryTemplate.execute(cxt -> getInstance());
//        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
//    }
//
//    private ServiceInstance getInstance() {
//        try {
//            return discoveryClient
//                    .getInstances(statsServiceId)
//                    .getFirst();
//        } catch (Exception exception) {
//            throw new RuntimeException(
//                    "Ошибка обнаружения адреса сервиса статистики с id: " + statsServiceId,
//                    exception
//            );
//        }
//    }
//}