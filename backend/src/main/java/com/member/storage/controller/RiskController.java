package com.member.storage.controller;

import com.member.storage.service.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/risk")
public class RiskController {

    @Autowired
    private RiskService riskService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getRiskList() {
        List<Map<String, Object>> accounts = riskService.getRiskAccounts();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", accounts);
        response.put("total", accounts.size());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getRiskDetail(@PathVariable String userId) {
        Map<String, Object> detail = riskService.getRiskDetail(userId);
        
        if (detail == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "账户不存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", detail);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/release")
    public ResponseEntity<Map<String, Object>> releaseRisk(@PathVariable String userId) {
        try {
            riskService.releaseRisk(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "风控已解除");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRiskStats() {
        Map<String, Object> stats = riskService.getRiskStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }
}
