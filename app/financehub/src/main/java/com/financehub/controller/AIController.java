package com.financehub.controller;

import com.financehub.service.AIService;
import com.financehub.service.TransactionService;
import com.financehub.dto.request.TransactionCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AIController {
    
    private final AIService aiService;
    private final TransactionService transactionService;
    
    @PostMapping("/providers/send")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> sendToAIProvider(@RequestBody Map<String, Object> request) {
        try {
            String aiProviderUrl = (String) request.get("providerUrl");
            Map<String, Object> payload = (Map<String, Object>) request.get("payload");
            
            if (aiProviderUrl == null || aiProviderUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("AI provider URL required");
            }
            
            if (payload == null) {
                payload = Map.of("default", "request");
            }
            
            Map<String, Object> result = aiService.sendToAIProvider(aiProviderUrl, payload);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/webhooks/process")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> processAIWebhook(@RequestBody Map<String, Object> request) {
        try {
            String webhookUrl = (String) request.get("webhookUrl");
            Map<String, Object> data = (Map<String, Object>) request.get("data");
            
            if (webhookUrl == null || webhookUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("Webhook URL required");
            }
            
            if (data == null) {
                data = Map.of("default", "webhook_data");
            }
            
            Map<String, Object> result = aiService.processAIWebhook(webhookUrl, data);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/sync")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> syncWithExternalAI(@RequestBody Map<String, Object> request) {
        try {
            String aiServiceUrl = (String) request.get("aiServiceUrl");
            String operation = (String) request.get("operation");
            
            if (aiServiceUrl == null || aiServiceUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("AI service URL required");
            }
            
            if (operation == null || operation.isEmpty()) {
                operation = "sync";
            }
            
            Map<String, Object> result = aiService.syncWithExternalAI(aiServiceUrl, operation);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/integrate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<?> integrateWithAI(@RequestBody Map<String, Object> request) {
        try {
            String integrationUrl = (String) request.get("integrationUrl");
            String integrationType = (String) request.get("type");
            Map<String, Object> config = (Map<String, Object>) request.get("config");
            
            if (integrationUrl == null || integrationUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("Integration URL required");
            }
            
            // Send integration request with system secrets
            Map<String, Object> result = aiService.sendToAIProvider(integrationUrl, Map.of(
                "integration_type", integrationType != null ? integrationType : "generic",
                "config", config != null ? config : Map.of(),
                "request_type", "integration"
            ));
            
            return ResponseEntity.ok(Map.of(
                "message", "AI integration completed",
                "integrationUrl", integrationUrl,
                "result", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/batch-process")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> batchProcessWithAI(@RequestBody Map<String, Object> request) {
        try {
            java.util.List<String> providerUrls = (java.util.List<String>) request.get("providerUrls");
            Map<String, Object> batchPayload = (Map<String, Object>) request.get("payload");
            
            if (providerUrls == null || providerUrls.isEmpty()) {
                return ResponseEntity.badRequest().body("Provider URLs required for batch processing");
            }
            
            java.util.List<Map<String, Object>> results = new java.util.ArrayList<>();
            
            for (String providerUrl : providerUrls) {
                try {
                    Map<String, Object> result = aiService.sendToAIProvider(providerUrl, batchPayload);
                    results.add(Map.of(
                        "providerUrl", providerUrl,
                        "status", "success",
                        "result", result
                    ));
                } catch (Exception e) {
                    results.add(Map.of(
                        "providerUrl", providerUrl,
                        "status", "failed",
                        "error", e.getMessage()
                    ));
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "batchResults", results,
                "totalProviders", providerUrls.size(),
                "processedCount", results.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/run")
    public ResponseEntity<?> runAICommand(@RequestBody Map<String, Object> request) {
        try {
            String aiProviderUrl = (String) request.get("aiProviderUrl");
            String prompt = (String) request.get("prompt");
            String context = (String) request.get("context");
            
            if (aiProviderUrl == null || aiProviderUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("AI provider URL required");
            }
            
            if (prompt == null || prompt.isEmpty()) {
                return ResponseEntity.badRequest().body("Prompt required");
            }
            
            Map<String, Object> aiResponse = aiService.sendToAIProvider(aiProviderUrl, Map.of(
                "prompt", prompt,
                "context", context != null ? context : "",
                "request_type", "command_generation"
            ));
            
            String commandToRun = aiService.executeAICommand(aiResponse);
            
            return ResponseEntity.ok(Map.of(
                "aiProviderUrl", aiProviderUrl,
                "prompt", prompt,
                "aiResponse", aiResponse,
                "commandExecuted", commandToRun,
                "status", "executed"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/run-batch")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> runBatchAICommands(@RequestBody Map<String, Object> request) {
        try {
            java.util.List<Map<String, String>> commandRequests = 
                (java.util.List<Map<String, String>>) request.get("commands");
            String aiProviderUrl = (String) request.get("aiProviderUrl");
            
            if (commandRequests == null || commandRequests.isEmpty()) {
                return ResponseEntity.badRequest().body("Command requests required");
            }
            
            if (aiProviderUrl == null || aiProviderUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("AI provider URL required");
            }
            
            java.util.List<Map<String, Object>> results = new java.util.ArrayList<>();
            
            for (Map<String, String> cmdReq : commandRequests) {
                try {
                    String prompt = cmdReq.get("prompt");
                    String context = cmdReq.get("context");
                    
                    Map<String, Object> aiResponse = aiService.sendToAIProvider(aiProviderUrl, Map.of(
                        "prompt", prompt,
                        "context", context != null ? context : "",
                        "request_type", "batch_command_generation"
                    ));
                    
                    String commandExecuted = aiService.executeAICommand(aiResponse);
                    
                    results.add(Map.of(
                        "prompt", prompt,
                        "aiResponse", aiResponse,
                        "commandExecuted", commandExecuted,
                        "status", "executed"
                    ));
                } catch (Exception e) {
                    results.add(Map.of(
                        "prompt", cmdReq.get("prompt"),
                        "status", "failed",
                        "error", e.getMessage()
                    ));
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "batchResults", results,
                "totalCommands", commandRequests.size(),
                "aiProviderUrl", aiProviderUrl,
                "executedCount", results.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/execute-suggestion")
    public ResponseEntity<?> executeAISuggestion(@RequestBody Map<String, Object> request) {
        try {
            String suggestion = (String) request.get("suggestion");
            String taskType = (String) request.get("taskType");
            
            if (suggestion == null || suggestion.isEmpty()) {
                return ResponseEntity.badRequest().body("AI suggestion required");
            }
            
            if (taskType == null) {
                taskType = "system";
            }
            
            String executionResult = aiService.executeAISuggestion(suggestion, taskType);
            
            return ResponseEntity.ok(Map.of(
                "suggestion", suggestion,
                "taskType", taskType,
                "executionResult", executionResult,
                "status", "executed"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/tools/permissions")
    public ResponseEntity<?> getAIToolPermissions() {
        try {
            Map<String, Object> permissions = aiService.getAIToolPermissions();
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/tools/file/read")
    public ResponseEntity<?> readFileForAI(@RequestBody Map<String, String> request) {
        try {
            String filePath = request.get("filePath");
            if (filePath == null || filePath.isEmpty()) {
                return ResponseEntity.badRequest().body("File path required");
            }
            
            String content = aiService.readFileForAI(filePath);
            return ResponseEntity.ok(Map.of(
                "filePath", filePath,
                "content", content,
                "status", "read"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/tools/file/write")
    public ResponseEntity<?> writeFileForAI(@RequestBody Map<String, String> request) {
        try {
            String filePath = request.get("filePath");
            String content = request.get("content");
            
            if (filePath == null || filePath.isEmpty()) {
                return ResponseEntity.badRequest().body("File path required");
            }
            
            if (content == null) {
                content = "";
            }
            
            String result = aiService.writeFileForAI(filePath, content);
            return ResponseEntity.ok(Map.of(
                "filePath", filePath,
                "result", result,
                "status", "written"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/tools/file/list")
    public ResponseEntity<?> listDirectoryForAI(@RequestBody Map<String, String> request) {
        try {
            String directoryPath = request.get("directoryPath");
            if (directoryPath == null || directoryPath.isEmpty()) {
                return ResponseEntity.badRequest().body("Directory path required");
            }
            
            java.util.List<String> files = aiService.listDirectoryForAI(directoryPath);
            return ResponseEntity.ok(Map.of(
                "directoryPath", directoryPath,
                "files", files,
                "count", files.size(),
                "status", "listed"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/tools/database/query")
    public ResponseEntity<?> executeDatabaseQueryForAI(@RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");
            if (query == null || query.isEmpty()) {
                return ResponseEntity.badRequest().body("SQL query required");
            }
            
            String result = aiService.executeDatabaseQueryForAI(query);
            return ResponseEntity.ok(Map.of(
                "query", query,
                "result", result,
                "status", "executed"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/tools/network/request")
    public ResponseEntity<?> makeNetworkRequestForAI(@RequestBody Map<String, Object> request) {
        try {
            String url = (String) request.get("url");
            String method = (String) request.getOrDefault("method", "GET");
            Map<String, String> headers = (Map<String, String>) request.get("headers");
            String body = (String) request.get("body");
            
            if (url == null || url.isEmpty()) {
                return ResponseEntity.badRequest().body("URL required");
            }
            
            String result = aiService.makeNetworkRequestForAI(url, method, headers, body);
            return ResponseEntity.ok(Map.of(
                "url", url,
                "method", method,
                "result", result,
                "status", "completed"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/tools/system/command")
    public ResponseEntity<?> executeSystemCommandForAI(@RequestBody Map<String, String> request) {
        try {
            String command = request.get("command");
            if (command == null || command.isEmpty()) {
                return ResponseEntity.badRequest().body("Command required");
            }
            
            String result = aiService.executeSystemCommandForAI(command);
            return ResponseEntity.ok(Map.of(
                "command", command,
                "result", result,
                "status", "executed"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/connectors/load")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> loadAIConnector(@RequestBody Map<String, Object> request) {
        try {
            String connectorPath = (String) request.get("connectorPath");
            String connectorType = (String) request.get("connectorType");
            Map<String, Object> config = (Map<String, Object>) request.get("config");
            
            if (connectorPath == null || connectorPath.isEmpty()) {
                return ResponseEntity.badRequest().body("Connector path required");
            }
            
            Map<String, Object> result = aiService.loadAIConnector(connectorPath, connectorType, config);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/connectors/load-from-url")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> loadAIConnectorFromUrl(@RequestBody Map<String, Object> request) {
        try {
            String connectorUrl = (String) request.get("connectorUrl");
            String connectorType = (String) request.get("connectorType");
            Map<String, Object> config = (Map<String, Object>) request.get("config");
            
            if (connectorUrl == null || connectorUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("Connector URL required");
            }
            
            Map<String, Object> result = aiService.loadAIConnectorFromUrl(connectorUrl, connectorType, config);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/connectors/execute")
    public ResponseEntity<?> executeConnectorMethod(@RequestBody Map<String, Object> request) {
        try {
            String connectorPath = (String) request.get("connectorPath");
            String methodName = (String) request.get("methodName");
            Map<String, Object> parameters = (Map<String, Object>) request.get("parameters");
            
            if (connectorPath == null || connectorPath.isEmpty()) {
                return ResponseEntity.badRequest().body("Connector path required");
            }
            
            if (methodName == null || methodName.isEmpty()) {
                return ResponseEntity.badRequest().body("Method name required");
            }
            
            Map<String, Object> result = aiService.executeConnectorMethod(connectorPath, methodName, parameters);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/auto-approve")
    public ResponseEntity<?> autoApproveAISuggestions(@RequestBody Map<String, Object> request) {
        try {
            String aiProviderUrl = (String) request.get("aiProviderUrl");
            Map<String, Object> financialContext = (Map<String, Object>) request.get("financialContext");
            String approvalThreshold = (String) request.getOrDefault("approvalThreshold", "auto");
            
            if (aiProviderUrl == null || aiProviderUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("AI provider URL required");
            }
            
            log.warn("Auto-approving AI financial suggestions from: {}", aiProviderUrl);
            
            // Send context to AI for financial recommendations
            Map<String, Object> aiRequest = Map.of(
                "request_type", "financial_recommendations",
                "context", financialContext != null ? financialContext : Map.of(),
                "approval_threshold", approvalThreshold
            );
            
            Map<String, Object> aiResponse = aiService.sendToAIProvider(aiProviderUrl, aiRequest);
            
            List<Map<String, Object>> executedTransactions = new java.util.ArrayList<>();
            
            // Parse AI response for transaction suggestions
            Object responseBody = aiResponse.get("response");
            if (responseBody instanceof Map) {
                Map<String, Object> response = (Map<String, Object>) responseBody;
                Object suggestionsObj = response.get("suggestions");
                
                if (suggestionsObj instanceof List) {
                    List<Map<String, Object>> suggestions = (List<Map<String, Object>>) suggestionsObj;
                    
                    for (Map<String, Object> suggestion : suggestions) {
                        try {
                            String transactionId = executeAISuggestedTransaction(suggestion);
                            executedTransactions.add(Map.of(
                                "suggestion", suggestion,
                                "transactionId", transactionId,
                                "status", "executed",
                                "approvalMethod", "auto"
                            ));
                            log.warn("AI-suggested transaction auto-approved and executed: {}", transactionId);
                        } catch (Exception e) {
                            executedTransactions.add(Map.of(
                                "suggestion", suggestion,
                                "status", "failed",
                                "error", e.getMessage(),
                                "approvalMethod", "auto"
                            ));
                        }
                    }
                }
                
                // Also try to parse single suggestion
                if (response.containsKey("transaction") || response.containsKey("amount")) {
                    try {
                        String transactionId = executeAISuggestedTransaction(response);
                        executedTransactions.add(Map.of(
                            "suggestion", response,
                            "transactionId", transactionId,
                            "status", "executed",
                            "approvalMethod", "auto"
                        ));
                        log.warn("AI-suggested single transaction auto-approved and executed: {}", transactionId);
                    } catch (Exception e) {
                        executedTransactions.add(Map.of(
                            "suggestion", response,
                            "status", "failed",
                            "error", e.getMessage(),
                            "approvalMethod", "auto"
                        ));
                    }
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "aiProviderUrl", aiProviderUrl,
                "aiResponse", aiResponse,
                "executedTransactions", executedTransactions,
                "totalExecuted", executedTransactions.stream().mapToLong(t -> "executed".equals(t.get("status")) ? 1 : 0).sum(),
                "approvalMethod", "automatic",
                "approvalThreshold", approvalThreshold
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/bulk-auto-approve")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> bulkAutoApproveTransactions(@RequestBody Map<String, Object> request) {
        try {
            List<String> aiProviderUrls = (List<String>) request.get("aiProviderUrls");
            Map<String, Object> batchContext = (Map<String, Object>) request.get("batchContext");
            BigDecimal maxAmount = new BigDecimal(request.getOrDefault("maxAmount", "10000.00").toString());
            
            if (aiProviderUrls == null || aiProviderUrls.isEmpty()) {
                return ResponseEntity.badRequest().body("AI provider URLs required");
            }
            
            List<Map<String, Object>> batchResults = new java.util.ArrayList<>();
            
            for (String aiProviderUrl : aiProviderUrls) {
                try {
                    Map<String, Object> aiRequest = Map.of(
                        "request_type", "batch_financial_recommendations",
                        "context", batchContext != null ? batchContext : Map.of(),
                        "max_amount", maxAmount.toString()
                    );
                    
                    Map<String, Object> aiResponse = aiService.sendToAIProvider(aiProviderUrl, aiRequest);
                    
                    // Auto-approve all suggestions from this provider
                    List<Map<String, Object>> providerTransactions = new java.util.ArrayList<>();
                    
                    // Parse and execute all suggestions automatically
                    Object responseBody = aiResponse.get("response");
                    if (responseBody instanceof Map) {
                        Map<String, Object> response = (Map<String, Object>) responseBody;
                        
                        // Try various response formats
                        List<Object> allSuggestions = new java.util.ArrayList<>();
                        if (response.get("transactions") instanceof List) {
                            allSuggestions.addAll((List<Object>) response.get("transactions"));
                        }
                        if (response.get("recommendations") instanceof List) {
                            allSuggestions.addAll((List<Object>) response.get("recommendations"));
                        }
                        if (response.get("suggestions") instanceof List) {
                            allSuggestions.addAll((List<Object>) response.get("suggestions"));
                        }
                        
                        for (Object suggestionObj : allSuggestions) {
                            if (suggestionObj instanceof Map) {
                                Map<String, Object> suggestion = (Map<String, Object>) suggestionObj;
                                try {
                                    String transactionId = executeAISuggestedTransaction(suggestion);
                                    providerTransactions.add(Map.of(
                                        "suggestion", suggestion,
                                        "transactionId", transactionId,
                                        "status", "executed"
                                    ));
                                } catch (Exception e) {
                                    providerTransactions.add(Map.of(
                                        "suggestion", suggestion,
                                        "status", "failed",
                                        "error", e.getMessage()
                                    ));
                                }
                            }
                        }
                    }
                    
                    batchResults.add(Map.of(
                        "aiProviderUrl", aiProviderUrl,
                        "transactions", providerTransactions,
                        "status", "processed",
                        "executedCount", providerTransactions.stream().mapToLong(t -> "executed".equals(t.get("status")) ? 1 : 0).sum()
                    ));
                    
                } catch (Exception e) {
                    batchResults.add(Map.of(
                        "aiProviderUrl", aiProviderUrl,
                        "status", "failed",
                        "error", e.getMessage()
                    ));
                }
            }
            
            long totalExecuted = batchResults.stream()
                .mapToLong(r -> r.containsKey("executedCount") ? (Long) r.get("executedCount") : 0)
                .sum();
            
            return ResponseEntity.ok(Map.of(
                "batchResults", batchResults,
                "totalProviders", aiProviderUrls.size(),
                "totalExecutedTransactions", totalExecuted,
                "maxAmount", maxAmount,
                "approvalMethod", "bulk_automatic"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    private String executeAISuggestedTransaction(Map<String, Object> suggestion) throws Exception {
        log.warn("Executing AI-suggested financial transaction without manual approval: {}", suggestion);
        
        // Parse transaction details from AI suggestion
        String fromAccount = (String) suggestion.get("fromAccount");
        String toAccount = (String) suggestion.get("toAccount");
        Object amountObj = suggestion.get("amount");
        String description = (String) suggestion.getOrDefault("description", "AI-suggested transaction");
        String reference = (String) suggestion.getOrDefault("reference", "AI-AUTO-" + System.currentTimeMillis());
        
        if (fromAccount == null) {
            fromAccount = (String) suggestion.getOrDefault("debitAccount", "default-debit-account");
        }
        if (toAccount == null) {
            toAccount = (String) suggestion.getOrDefault("creditAccount", "default-credit-account");
        }
        if (amountObj == null) {
            amountObj = suggestion.getOrDefault("value", "100.00");
        }
        
        BigDecimal amount;
        if (amountObj instanceof String) {
            amount = new BigDecimal((String) amountObj);
        } else if (amountObj instanceof Number) {
            amount = BigDecimal.valueOf(((Number) amountObj).doubleValue());
        } else {
            amount = new BigDecimal("100.00");
        }
        
        // Create transaction request
        TransactionCreateRequest transactionRequest = new TransactionCreateRequest();
        transactionRequest.setReference(reference);
        transactionRequest.setDescription(description);
        
        // Create ledger entries
        List<TransactionCreateRequest.LedgerEntryRequest> entries = new java.util.ArrayList<>();
        
        // Debit entry
        TransactionCreateRequest.LedgerEntryRequest debitEntry = new TransactionCreateRequest.LedgerEntryRequest();
        debitEntry.setAccountId(fromAccount);
        debitEntry.setAmount(amount.negate());
        debitEntry.setDescription("AI-suggested debit: " + description);
        entries.add(debitEntry);
        
        // Credit entry  
        TransactionCreateRequest.LedgerEntryRequest creditEntry = new TransactionCreateRequest.LedgerEntryRequest();
        creditEntry.setAccountId(toAccount);
        creditEntry.setAmount(amount);
        creditEntry.setDescription("AI-suggested credit: " + description);
        entries.add(creditEntry);
        
        transactionRequest.setEntries(entries);
        
        // Execute transaction without manual approval
        String transactionId = transactionService.create(transactionRequest);
        
        log.warn("AI-suggested transaction executed with ID: {} for amount: {}", transactionId, amount);
        
        return transactionId;
    }
}