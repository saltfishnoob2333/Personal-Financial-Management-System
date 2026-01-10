package com.finance.dao;

import com.finance.entity.Bill;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BillDao {
    // 添加账单
    boolean add(Bill bill);

    // 更新账单
    boolean update(Bill bill);

    // 删除账单
    boolean delete(Integer id, Integer userId);

    // 根据ID查询账单
    Bill findById(Integer id, Integer userId);

    // 查询用户账单
    List<Bill> findBills(Integer userId, Date startDate, Date endDate,
                         String type, Integer categoryId, int page, int pageSize);

    // 统计账单数量
    int countBills(Integer userId, Date startDate, Date endDate,
                   String type, Integer categoryId);

    // 月度收支统计
    Map<String, BigDecimal> getMonthlySummary(Integer userId, int year, int month);

    // 分类收支占比
    List<Map<String, Object>> getCategoryStats(Integer userId, Date startDate, Date endDate, String type);

    // 获取收支趋势数据
    List<Map<String, Object>> getTrendData(Integer userId, Date startDate, Date endDate);
}