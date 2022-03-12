package com.octanium91.j11.springboot.benchmark.controller;

import com.octanium91.j11.springboot.benchmark.services.BenchmarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
public class BenchmarkController {

    private BenchmarkService benchmarkService;

    @PostConstruct
    public void init() {
        this.benchmarkService = new BenchmarkService();
    }

    @GetMapping("/api/octanium91/benchmark/run")
    Map<String, Object> run() {
        Map<String, Object> response = benchmarkService.run();
        log.info("Benchmark response: "+response.toString());
        return response;
    }

    @GetMapping("/api/octanium91/benchmark/history")
    Collection<Map<String, Object>> history() {
        return benchmarkService.responses;
    }

}
