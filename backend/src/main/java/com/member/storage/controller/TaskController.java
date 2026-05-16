package com.member.storage.controller;

import com.member.storage.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/expire-gift")
    public ResponseEntity<Map<String, Object>> expireGift() {
        int count = taskService.processExpiredGifts();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "过期赠送金处理完成");
        response.put("processed", count);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/risk-check")
    public ResponseEntity<Map<String, Object>> riskCheck() {
        int count = taskService.checkRisks();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "风险检查完成");
        response.put("checked", count);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/level-update")
    public ResponseEntity<Map<String, Object>> levelUpdate() {
        int count = taskService.updateMemberLevels();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "会员等级更新完成");
        response.put("updated", count);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTaskStats() {
        Map<String, Object> stats = taskService.getTaskStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }
}
