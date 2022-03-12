package com.octanium91.j11.springboot.benchmark.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.*;

@Slf4j
@Service
public class BenchmarkService {

    final private static double version = 0.01;
    final private long measurementDelay = 10L;
    final public Collection<Map<String, Object>> responses = new ArrayList<>();
    private String tempFile = "./temp.temp";

    public Map<String, Object> run() {
        Map<String, Object> response = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> metaInfo = new HashMap<>();
        Map<String, Object> runtimeMap = new HashMap<>();
        runtimeMap.put("benchmark_version", version);
        runtimeMap.put("java_version", System.getProperty("java.version"));
        runtimeMap.put("java_vm_name", System.getProperty("java.vm.name"));
        runtimeMap.put("java_vm_vendor", System.getProperty("java.vm.vendor"));
        runtimeMap.put("java_vm_version", System.getProperty("java.vm.version"));
        runtimeMap.put("availableProcessors", runtime.availableProcessors());
        runtimeMap.put("totalMemory_bytes", runtime.totalMemory());
        runtimeMap.put("maxMemory_bytes", runtime.maxMemory());
        runtimeMap.put("freeMemory_bytes", runtime.freeMemory());
        runtimeMap.put("totalMemory_megabytes", runtime.totalMemory()/1048576);
        runtimeMap.put("maxMemory_megabytes", runtime.maxMemory()/1048576);
        runtimeMap.put("freeMemory_megabytes", runtime.freeMemory()/1048576);
        response.put("runtime", runtimeMap);
        final Date dateStart = new Date();
        response.put("run_1", bench());
        response.put("run_2", bench());
        response.put("run_3", bench());
        final Date dateEnd = new Date();
        metaInfo.put("date_start", dateStart);
        metaInfo.put("date_end", dateEnd);
        metaInfo.put("elapsed_time_milliseconds", dateEnd.getTime() - dateStart.getTime());
        response.put("meta_info", metaInfo);

        responses.add(response);

        return response;
    }

    private Map<String, Object> bench() {
        tempFile = "./tmp_"+System.currentTimeMillis()+".tmp";
        Map<String, Object> response = new HashMap<>();
        response.putAll(benchCreateString());
        response.putAll(benchMathSum());
        response.putAll(benchMathDivision());
        response.putAll(fileWrite());
        response.putAll(fileRead());
        File file = new File(tempFile);
        if (file.exists()) {
            if (!file.delete()) {
                log.error("File " + tempFile + " is not deleted!");
            }
        } else {
            log.error("File " + tempFile + " is not exist!");
        }
        return response;
    }

    private Map<String, Object> benchCreateString() {
        Map<String, Object> response = new HashMap<>();
        final Collection<Long> v = new ArrayList<>();
        final boolean[] calc = {true};
        new Thread(() -> {
            final String string = "String";
            String genString = "";
            while (calc[0]) {
                genString = genString+string;
                v.add(1L);
            }
        }).start();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        calc[0] = false;
                    }
                },
                measurementDelay
        );
        while (calc[0]) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        response.put("create_string", v.size());
        return response;
    }

    private Map<String, Object> benchMathSum() {
        Map<String, Object> response = new HashMap<>();
        final Collection<Long> v = new ArrayList<>();
        final boolean[] calc = {true};
        new Thread(() -> {
            while (calc[0]) {
                v.add(256L+256L);
            }
        }).start();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        calc[0] = false;
                    }
                },
                measurementDelay
        );
        while (calc[0]) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        response.put("math_sum_256", v.size());
        return response;
    }

    private Map<String, Object> benchMathDivision() {
        Map<String, Object> response = new HashMap<>();
        final Collection<Long> v = new ArrayList<>();
        final boolean[] calc = {true};
        new Thread(() -> {
            while (calc[0]) {
                v.add(256L/256L);
            }
        }).start();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        calc[0] = false;
                    }
                },
                measurementDelay
        );
        while (calc[0]) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        response.put("math_division_256", v.size());
        return response;
    }

    private Map<String, Object> fileWrite() {
        Map<String, Object> response = new HashMap<>();
        final long start = System.currentTimeMillis();
        try {
            RandomAccessFile f = new RandomAccessFile(tempFile, "rw");
            f.setLength(1024 * 1024 * 1024);
            f.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        final long end = System.currentTimeMillis();
        response.put("file_write_ms", end - start);
        return response;
    }

    private Map<String, Object> fileRead() {
        Map<String, Object> response = new HashMap<>();
        final long start = System.currentTimeMillis();
        File file = new File(tempFile);
        if (file.exists()) {
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                if (bytes.length == 0) {
                    log.error("Test read file, file "+tempFile+" is not read!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.error("Test read file, file "+tempFile+" is not exist!");
        }
        final long end = System.currentTimeMillis();
        response.put("file_read_ms", end - start);
        return response;
    }

}
