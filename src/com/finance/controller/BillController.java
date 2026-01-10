package com.finance.controller;

import com.finance.entity.Bill;
import com.finance.service.BillService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BillController {
    private BillService billService = new BillService();
    private UserController userController;

    public BillController(UserController userController) {
        this.userController = userController;
    }

    /**
     * 添加账单
     */
    public boolean addBill(Integer categoryId, BigDecimal amount,
                           String type, String remark, Date billDate) {
        if (!userController.isLoggedIn()) {
            return false;
        }

        Bill bill = new Bill(userController.getCurrentUser().getId(),
                categoryId, amount, type, remark, billDate);
        return billService.addBill(bill);
    }

    /**
     * 更新账单
     */
    public boolean updateBill(Integer id, Integer categoryId, BigDecimal amount,
                              String type, String remark, Date billDate) {
        if (!userController.isLoggedIn()) {
            return false;
        }

        Bill bill = new Bill(userController.getCurrentUser().getId(),
                categoryId, amount, type, remark, billDate);
        bill.setId(id);
        return billService.updateBill(bill);
    }

    /**
     * 删除账单
     */
    public boolean deleteBill(Integer id) {
        if (!userController.isLoggedIn()) {
            return false;
        }

        return billService.deleteBill(id, userController.getCurrentUser().getId());
    }

    /**
     * 获取账单
     */
    public Bill getBillById(Integer id) {
        if (!userController.isLoggedIn()) {
            return null;
        }

        return billService.getBillById(id, userController.getCurrentUser().getId());
    }

    /**
     * 查询账单列表
     */
    public List<Bill> getBills(Date startDate, Date endDate, String type,
                               Integer categoryId, int page, int pageSize) {
        if (!userController.isLoggedIn()) {
            return null;
        }

        return billService.getBills(userController.getCurrentUser().getId(),
                startDate, endDate, type, categoryId, page, pageSize);
    }

    /**
     * 获取账单总数
     */
    public int getBillCount(Date startDate, Date endDate, String type, Integer categoryId) {
        if (!userController.isLoggedIn()) {
            return 0;
        }

        return billService.getBillCount(userController.getCurrentUser().getId(),
                startDate, endDate, type, categoryId);
    }

    /**
     * 获取分页总数
     */
    public int getTotalPages(Date startDate, Date endDate, String type,
                             Integer categoryId, int pageSize) {
        int totalCount = getBillCount(startDate, endDate, type, categoryId);
        return billService.getTotalPages(totalCount, pageSize);
    }

    /**
     * 获取月度收支统计
     */
    public Map<String, BigDecimal> getMonthlySummary(int year, int month) {
        if (!userController.isLoggedIn()) {
            return null;
        }

        return billService.getMonthlySummary(userController.getCurrentUser().getId(), year, month);
    }

    /**
     * 获取分类收支统计
     */
    public List<Map<String, Object>> getCategoryStats(Date startDate, Date endDate, String type) {
        if (!userController.isLoggedIn()) {
            return null;
        }

        return billService.getCategoryStats(userController.getCurrentUser().getId(),
                startDate, endDate, type);
    }

    /**
     * 获取收支趋势数据
     */
    public List<Map<String, Object>> getTrendData(Date startDate, Date endDate) {
        if (!userController.isLoggedIn()) {
            return null;
        }

        return billService.getTrendData(userController.getCurrentUser().getId(), startDate, endDate);
    }
}