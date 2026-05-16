package com.member.storage.controller;

import com.member.storage.model.RechargeRecord;
import com.member.storage.service.RechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recharge")
public class RechargeController {

    @Autowired
    private RechargeService rechargeService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> recharge(
            @RequestParam String userId,
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "WECHAT") String payMethod) {
        try {
            RechargeRecord record = rechargeService.recharge(userId, amount, payMethod);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "充值成功");
            response.put("data", record);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getRechargeRecord(@PathVariable String orderId) {
        RechargeRecord record = rechargeService.getRechargeRecord(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", record);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/list")
    public ResponseEntity<Map<String, Object>> getRechargeList(@PathVariable String userId) {
        List<RechargeRecord> records = rechargeService.getRechargeRecordsByUserId(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records);
        response.put("total", records.size());
        
        return ResponseEntity.ok(response);
    }
}
