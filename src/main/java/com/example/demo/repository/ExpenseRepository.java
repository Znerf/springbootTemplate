package com.example.demo.repository;

import com.example.demo.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    // Find all expenses for a specific user
    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);
    
    // Find expenses by user and category
    List<Expense> findByUserIdAndCategoryOrderByExpenseDateDesc(Long userId, String category);
    
    // Find expenses by user and date range
    List<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(
        Long userId, LocalDate startDate, LocalDate endDate);
    
    // Find expenses by user and cost range
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.cost BETWEEN :minCost AND :maxCost ORDER BY e.expenseDate DESC")
    List<Expense> findByUserIdAndCostBetweenOrderByExpenseDateDesc(
        @Param("userId") Long userId, 
        @Param("minCost") BigDecimal minCost, 
        @Param("maxCost") BigDecimal maxCost);
    
    // Find expenses by user and payment method
    List<Expense> findByUserIdAndPaymentMethodOrderByExpenseDateDesc(Long userId, String paymentMethod);
    
    // Find expenses by user and item name (case insensitive)
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND LOWER(e.item) LIKE LOWER(CONCAT('%', :itemName, '%')) ORDER BY e.expenseDate DESC")
    List<Expense> findByUserIdAndItemContainingIgnoreCaseOrderByExpenseDateDesc(
        @Param("userId") Long userId, 
        @Param("itemName") String itemName);
    
    // Get total cost for a user
    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.userId = :userId")
    BigDecimal getTotalCostByUserId(@Param("userId") Long userId);
    
    // Get total cost for a user in date range
    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.userId = :userId AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalCostByUserIdAndDateRange(
        @Param("userId") Long userId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate);
    
    // Get total cost by category for a user
    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.userId = :userId AND e.category = :category")
    BigDecimal getTotalCostByUserIdAndCategory(
        @Param("userId") Long userId, 
        @Param("category") String category);
    
    // Get distinct categories for a user
    @Query("SELECT DISTINCT e.category FROM Expense e WHERE e.userId = :userId AND e.category IS NOT NULL")
    List<String> findDistinctCategoriesByUserId(@Param("userId") Long userId);
    
    // Get distinct payment methods for a user
    @Query("SELECT DISTINCT e.paymentMethod FROM Expense e WHERE e.userId = :userId AND e.paymentMethod IS NOT NULL")
    List<String> findDistinctPaymentMethodsByUserId(@Param("userId") Long userId);
} 