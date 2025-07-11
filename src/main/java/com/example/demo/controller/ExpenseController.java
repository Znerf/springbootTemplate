package com.example.demo.controller;

import com.example.demo.model.Expense;
import com.example.demo.service.ExpenseService;
import com.example.demo.service.LoginService;
import com.example.demo.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    private final JwtUtil jwtUtil;
    private final LoginService loginService;
    
    public ExpenseController(ExpenseService expenseService, JwtUtil jwtUtil, LoginService loginService) {
        this.expenseService = expenseService;
        this.jwtUtil = jwtUtil;
        this.loginService = loginService;
    }
    
    // Helper method to get user ID from JWT token
    private Long getUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            return loginService.getLoginByEmail(email)
                    .map(login -> login.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("Invalid or missing JWT token");
    }
    
    // CRUD Operations
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(@RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<Expense> expenses = expenseService.getAllExpensesByUserId(userId);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id, 
                                                 @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        return expenseService.getExpenseByIdAndUserId(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense,
                                                @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        expense.setUserId(userId);
        Expense createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id,
                                                @RequestBody Expense expense,
                                                @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        try {
            Expense updatedExpense = expenseService.updateExpense(id, userId, expense);
            return ResponseEntity.ok(updatedExpense);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id,
                                             @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        try {
            expenseService.deleteExpense(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Filtering Endpoints
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Expense>> getExpensesByCategory(@PathVariable String category,
                                                              @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<Expense> expenses = expenseService.getExpensesByCategory(userId, category);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<Expense>> getExpensesByDateRange(@RequestParam LocalDate startDate,
                                                               @RequestParam LocalDate endDate,
                                                               @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<Expense> expenses = expenseService.getExpensesByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/cost-range")
    public ResponseEntity<List<Expense>> getExpensesByCostRange(@RequestParam BigDecimal minCost,
                                                               @RequestParam BigDecimal maxCost,
                                                               @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<Expense> expenses = expenseService.getExpensesByCostRange(userId, minCost, maxCost);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/payment-method/{paymentMethod}")
    public ResponseEntity<List<Expense>> getExpensesByPaymentMethod(@PathVariable String paymentMethod,
                                                                   @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<Expense> expenses = expenseService.getExpensesByPaymentMethod(userId, paymentMethod);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Expense>> searchExpensesByItem(@RequestParam String itemName,
                                                             @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<Expense> expenses = expenseService.searchExpensesByItem(userId, itemName);
        return ResponseEntity.ok(expenses);
    }
    
    // Analytics Endpoints
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalExpenses(@RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        BigDecimal total = expenseService.getTotalExpensesByUserId(userId);
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/total/category/{category}")
    public ResponseEntity<BigDecimal> getTotalExpensesByCategory(@PathVariable String category,
                                                                @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        BigDecimal total = expenseService.getTotalExpensesByUserIdAndCategory(userId, category);
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/total/date-range")
    public ResponseEntity<BigDecimal> getTotalExpensesByDateRange(@RequestParam LocalDate startDate,
                                                                 @RequestParam LocalDate endDate,
                                                                 @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        BigDecimal total = expenseService.getTotalExpensesByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/total/monthly/{year}/{month}")
    public ResponseEntity<BigDecimal> getMonthlyTotal(@PathVariable int year,
                                                     @PathVariable int month,
                                                     @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        BigDecimal total = expenseService.getMonthlyTotal(userId, year, month);
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/total/yearly/{year}")
    public ResponseEntity<BigDecimal> getYearlyTotal(@PathVariable int year,
                                                    @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        BigDecimal total = expenseService.getYearlyTotal(userId, year);
        return ResponseEntity.ok(total);
    }
    
    // Metadata Endpoints
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(@RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<String> categories = expenseService.getCategoriesByUserId(userId);
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/payment-methods")
    public ResponseEntity<List<String>> getPaymentMethods(@RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<String> paymentMethods = expenseService.getPaymentMethodsByUserId(userId);
        return ResponseEntity.ok(paymentMethods);
    }
} 