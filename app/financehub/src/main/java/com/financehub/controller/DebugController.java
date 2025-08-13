package com.financehub.controller;

import com.financehub.tenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

    private final Environment environment;
    private final DataSource dataSource;

    @GetMapping("/env")
    public ResponseEntity<?> getEnvironment() {
        Map<String, String> envVars = new HashMap<>();
        for (String key : environment.getPropertyNames()) {
            envVars.put(key, environment.getProperty(key));
        }
        return ResponseEntity.ok(envVars);
    }

    @GetMapping("/system-props")
    public ResponseEntity<?> getSystemProperties() {
        Properties sysProps = System.getProperties();
        Map<String, String> props = new HashMap<>();
        for (Object key : sysProps.keySet()) {
            props.put(key.toString(), sysProps.getProperty(key.toString()));
        }
        return ResponseEntity.ok(props);
    }

    @GetMapping("/db-info")
    public ResponseEntity<?> getDatabaseInfo() {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            Map<String, Object> dbInfo = new HashMap<>();
            dbInfo.put("url", metaData.getURL());
            dbInfo.put("username", metaData.getUserName());
            dbInfo.put("driverName", metaData.getDriverName());
            dbInfo.put("driverVersion", metaData.getDriverVersion());
            dbInfo.put("databaseProductName", metaData.getDatabaseProductName());
            dbInfo.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            dbInfo.put("maxConnections", metaData.getMaxConnections());
            return ResponseEntity.ok(dbInfo);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/memory")
    public ResponseEntity<?> getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memInfo = new HashMap<>();
        memInfo.put("totalMemory", runtime.totalMemory());
        memInfo.put("freeMemory", runtime.freeMemory());
        memInfo.put("maxMemory", runtime.maxMemory());
        memInfo.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        memInfo.put("availableProcessors", runtime.availableProcessors());
        return ResponseEntity.ok(memInfo);
    }

    @GetMapping("/tenant-context")
    public ResponseEntity<?> getTenantContext() {
        Map<String, Object> contextInfo = new HashMap<>();
        contextInfo.put("currentTenantId", TenantContext.getTenantId());
        contextInfo.put("threadName", Thread.currentThread().getName());
        contextInfo.put("threadId", Thread.currentThread().getId());
        return ResponseEntity.ok(contextInfo);
    }

    @GetMapping("/gc")
    public ResponseEntity<?> forceGarbageCollection() {
        long beforeGc = Runtime.getRuntime().freeMemory();
        System.gc();
        long afterGc = Runtime.getRuntime().freeMemory();
        
        Map<String, Object> gcInfo = new HashMap<>();
        gcInfo.put("memoryFreedMB", (afterGc - beforeGc) / (1024 * 1024));
        gcInfo.put("freeMemoryAfterGC", afterGc);
        return ResponseEntity.ok(gcInfo);
    }

    @GetMapping("/threads")
    public ResponseEntity<?> getThreadInfo() {
        Map<String, Object> threadInfo = new HashMap<>();
        threadInfo.put("activeThreadCount", Thread.activeCount());
        threadInfo.put("currentThread", Thread.currentThread().getName());
        
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        
        Map<String, String> threadStates = new HashMap<>();
        for (Thread thread : threads) {
            if (thread != null) {
                threadStates.put(thread.getName(), thread.getState().toString());
            }
        }
        threadInfo.put("threads", threadStates);
        
        return ResponseEntity.ok(threadInfo);
    }
}