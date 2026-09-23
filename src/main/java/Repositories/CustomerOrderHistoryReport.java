package Repositories;

import Domain.Order;

import java.math.BigDecimal;
import java.util.List;

public record CustomerOrderHistoryReport(List<Order>orders, BigDecimal totalSpent) {
}
