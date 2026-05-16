package com.member.storage.controller;

import com.member.storage.model.RefundRecord;
import com.member.storage.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/refund")
public class RefundController {

    @Autowired
    private RefundService refundService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> refund(
            @RequestParam String orderId,
            @RequestParam(required = false, defaultValue = "用户申请退款") String reason) {
        try {
            RefundRecord record = refundService.refund(orderId, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "退款成功");
            response.put("data", record);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{refundId}")
    public ResponseEntity<Map<String, Object>> getRefundRecord(@PathVariable String refundId) {
        RefundRecord record = refundService.getRefundRecord(refundId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", record);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/list")
    public ResponseEntity<Map<String, Object>> getRefundList(@PathVariable String userId) {
        List<RefundRecord> records = refundService.getRefundRecordsByUserId(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records);
        response.put("total", records.size());
        
        return ResponseEntity.ok(response);
    }
}
