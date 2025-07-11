package com.example.demo.service;

import com.example.demo.model.Expense;
import com.example.demo.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }
    
    // CRUD Operations
    @Transactional(readOnly = true)
    public List<Expense> getAllExpensesByUserId(Long userId) {
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
    }
    
    @Transactional(readOnly = true)
    public Optional<Expense> getExpenseByIdAndUserId(Long expenseId, Long userId) {
        return expenseRepository.findById(expenseId)
                .filter(expense -> expense.getUserId().equals(userId));
    }
    
    @Transactional
    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }
    
    @Transactional
    public Expense updateExpense(Long expenseId, Long userId, Expense updatedExpense) {
        return expenseRepository.findById(expenseId)
                .filter(expense -> expense.getUserId().equals(userId))
                .map(existingExpense -> {
                    existingExpense.setItem(updatedExpense.getItem());
                    existingExpense.setCost(updatedExpense.getCost());
                    existingExpense.setExpenseDate(updatedExpense.getExpenseDate());
                    existingExpense.setCategory(updatedExpense.getCategory());
                    existingExpense.setDescription(updatedExpense.getDescription());
                    existingExpense.setPaymentMethod(updatedExpense.getPaymentMethod());
                    existingExpense.setLocation(updatedExpense.getLocation());
                    return expenseRepository.save(existingExpense);
                })
                .orElseThrow(() -> new RuntimeException("Expense not found or access denied"));
    }
    
    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        expenseRepository.findById(expenseId)
                .filter(expense -> expense.getUserId().equals(userId))
                .ifPresentOrElse(
                    expense -> expenseRepository.delete(expense),
                    () -> { throw new RuntimeException("Expense not found or access denied"); }
                );
    }
    
    // Filtering Operations
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByCategory(Long userId, String category) {
        return expenseRepository.findByUserIdAndCategoryOrderByExpenseDateDesc(userId, category);
    }
    
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(userId, startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByCostRange(Long userId, BigDecimal minCost, BigDecimal maxCost) {
        return expenseRepository.findByUserIdAndCostBetweenOrderByExpenseDateDesc(userId, minCost, maxCost);
    }
    
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByPaymentMethod(Long userId, String paymentMethod) {
        return expenseRepository.findByUserIdAndPaymentMethodOrderByExpenseDateDesc(userId, paymentMethod);
    }
    
    @Transactional(readOnly = true)
    public List<Expense> searchExpensesByItem(Long userId, String itemName) {
        return expenseRepository.findByUserIdAndItemContainingIgnoreCaseOrderByExpenseDateDesc(userId, itemName);
    }
    
    // Analytics Operations
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpensesByUserId(Long userId) {
        BigDecimal total = expenseRepository.getTotalCostByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpensesByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = expenseRepository.getTotalCostByUserIdAndDateRange(userId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpensesByUserIdAndCategory(Long userId, String category) {
        BigDecimal total = expenseRepository.getTotalCostByUserIdAndCategory(userId, category);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public List<String> getCategoriesByUserId(Long userId) {
        return expenseRepository.findDistinctCategoriesByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public List<String> getPaymentMethodsByUserId(Long userId) {
        return expenseRepository.findDistinctPaymentMethodsByUserId(userId);
    }
    
    // Monthly/Yearly summaries
    @Transactional(readOnly = true)
    public BigDecimal getMonthlyTotal(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return getTotalExpensesByUserIdAndDateRange(userId, startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getYearlyTotal(Long userId, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return getTotalExpensesByUserIdAndDateRange(userId, startDate, endDate);
    }
} 