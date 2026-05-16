package com.member.storage.controller;

import com.member.storage.model.ConsumeRecord;
import com.member.storage.model.OrderItem;
import com.member.storage.service.ConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consume")
public class ConsumeController {

    @Autowired
    private ConsumeService consumeService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> consume(
            @RequestParam String userId,
            @RequestBody List<OrderItem> items,
            @RequestParam(required = false) List<String> couponIds) {
        try {
            ConsumeRecord record = consumeService.consume(userId, items, couponIds);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "消费成功");
            response.put("data", record);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/combine")
    public ResponseEntity<Map<String, Object>> combinePay(
            @RequestParam String userId,
            @RequestBody List<OrderItem> items,
            @RequestParam BigDecimal balanceAmount,
            @RequestParam BigDecimal cashAmount) {
        try {
            ConsumeRecord record = consumeService.combinePay(userId, items, balanceAmount, cashAmount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "组合支付成功");
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
    public ResponseEntity<Map<String, Object>> getConsumeRecord(@PathVariable String orderId) {
        ConsumeRecord record = consumeService.getConsumeRecord(orderId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", record);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/list")
    public ResponseEntity<Map<String, Object>> getConsumeList(@PathVariable String userId) {
        List<ConsumeRecord> records = consumeService.getConsumeRecordsByUserId(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records);
        response.put("total", records.size());
        
        return ResponseEntity.ok(response);
    }
}
