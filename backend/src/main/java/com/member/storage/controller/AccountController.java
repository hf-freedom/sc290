package com.member.storage.controller;

import com.member.storage.model.Account;
import com.member.storage.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createAccount(@RequestParam String userId) {
        Account account = accountService.createAccount(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "账户创建成功");
        response.put("data", account);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getAccount(@PathVariable String userId) {
        try {
            Account account = accountService.getAccount(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", account);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", accounts);
        response.put("total", accounts.size());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable String userId) {
        try {
            Account account = accountService.getAccount(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("balance", account.getBalance());
            response.put("giftBalance", account.getGiftBalance());
            response.put("totalBalance", account.getBalance().add(account.getGiftBalance()));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
