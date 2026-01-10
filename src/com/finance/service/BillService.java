package com.finance.service;

import com.finance.dao.BillDao;
import com.finance.dao.impl.BillDaoImpl;
import com.finance.entity.Bill;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BillService {
    private BillDao billDao = new BillDaoImpl();

    /**
     * 添加账单
     */
    public boolean addBill(Bill bill) {
        return billDao.add(bill);
    }

    /**
     * 更新账单
     */
    public boolean updateBill(Bill bill) {
        // 验证账单是否存在且属于该用户
        Bill existing = billDao.findById(bill.getId(), bill.getUserId());
        if (existing == null) {
            return false;
        }
        return billDao.update(bill);
    }

    /**
     * 删除账单
     */
    public boolean deleteBill(Integer id, Integer userId) {
        return billDao.delete(id, userId);
    }

    /**
     * 获取账单
     */
    public Bill getBillById(Integer id, Integer userId) {
        return billDao.findById(id, userId);
    }

    /**
     * 查询账单列表
     */
    public List<Bill> getBills(Integer userId, Date startDate, Date endDate,
                               String type, Integer categoryId, int page, int pageSize) {
        return billDao.findBills(userId, startDate, endDate, type, categoryId, page, pageSize);
    }

    /**
     * 获取账单总数
     */
    public int getBillCount(Integer userId, Date startDate, Date endDate,
                            String type, Integer categoryId) {
        return billDao.countBills(userId, startDate, endDate, type, categoryId);
    }

    /**
     * 获取月度收支统计
     */
    public Map<String, BigDecimal> getMonthlySummary(Integer userId, int year, int month) {
        return billDao.getMonthlySummary(userId, year, month);
    }

    /**
     * 获取分类收支统计
     */
    public List<Map<String, Object>> getCategoryStats(Integer userId, Date startDate,
                                                      Date endDate, String type) {
        return billDao.getCategoryStats(userId, startDate, endDate, type);
    }

    /**
     * 获取收支趋势数据
     */
    public List<Map<String, Object>> getTrendData(Integer userId, Date startDate, Date endDate) {
        return billDao.getTrendData(userId, startDate, endDate);
    }

    /**
     * 获取分页信息
     */
    public int getTotalPages(int totalCount, int pageSize) {
        return (int) Math.ceil((double) totalCount / pageSize);
    }
}