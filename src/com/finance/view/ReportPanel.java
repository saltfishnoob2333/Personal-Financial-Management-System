package com.finance.view;

import com.finance.controller.BillController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportPanel extends JPanel implements MainFrame.Refreshable {
    private BillController billController;

    private JComboBox<Integer> yearCombo;
    private JComboBox<Integer> monthCombo;
    private JComboBox<String> reportTypeCombo;
    private JTextArea reportArea;
    private JTable statsTable;
    private DefaultTableModel tableModel;

    public ReportPanel(BillController billController) {
        this.billController = billController;
        initComponents();
        loadReport();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 创建控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // 年份选择
        controlPanel.add(new JLabel("年份:"));
        yearCombo = new JComboBox<>();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        for (int year = currentYear - 5; year <= currentYear; year++) {
            yearCombo.addItem(year);
        }
        yearCombo.setSelectedItem(currentYear);
        yearCombo.addActionListener(e -> loadReport());
        controlPanel.add(yearCombo);

        // 月份选择
        controlPanel.add(new JLabel("月份:"));
        monthCombo = new JComboBox<>();
        for (int month = 1; month <= 12; month++) {
            monthCombo.addItem(month);
        }
        monthCombo.setSelectedItem(cal.get(Calendar.MONTH) + 1);
        monthCombo.addActionListener(e -> loadReport());
        controlPanel.add(monthCombo);

        // 报表类型选择
        controlPanel.add(new JLabel("报表类型:"));
        reportTypeCombo = new JComboBox<>(new String[]{
                "月度收支汇总", "支出分类统计", "收入分类统计", "收支趋势分析"
        });
        reportTypeCombo.addActionListener(e -> loadReport());
        controlPanel.add(reportTypeCombo);

        // 刷新按钮
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadReport());
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);

        // 创建中心面板（使用卡片布局切换不同视图）
        JPanel centerPanel = new JPanel(new CardLayout());

        // 文本报表面板
        JPanel textPanel = new JPanel(new BorderLayout());
        reportArea = new JTextArea();
        reportArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        reportArea.setEditable(false);
        JScrollPane textScroll = new JScrollPane(reportArea);
        textPanel.add(textScroll, BorderLayout.CENTER);

        // 表格统计面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel();
        statsTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(statsTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        centerPanel.add(textPanel, "text");
        centerPanel.add(tablePanel, "table");

        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        Integer year = (Integer) yearCombo.getSelectedItem();
        Integer month = (Integer) monthCombo.getSelectedItem();

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        // 显示对应的面板
        CardLayout cl = (CardLayout) ((JPanel) getComponent(1)).getLayout();

        switch (reportType) {
            case "月度收支汇总":
                showMonthlySummary(year, month);
                cl.show((JPanel) getComponent(1), "text");
                break;
            case "支出分类统计":
                showCategoryStats(startDate, endDate, "EXPENSE", "支出");
                cl.show((JPanel) getComponent(1), "table");
                break;
            case "收入分类统计":
                showCategoryStats(startDate, endDate, "INCOME", "收入");
                cl.show((JPanel) getComponent(1), "table");
                break;
            case "收支趋势分析":
                showTrendAnalysis(startDate, endDate);
                cl.show((JPanel) getComponent(1), "table");
                break;
        }
    }

    private void showMonthlySummary(int year, int month) {
        Map<String, BigDecimal> summary = billController.getMonthlySummary(year, month);

        StringBuilder sb = new StringBuilder();
        sb.append("==================== 月度收支汇总 ====================\n");
        sb.append(String.format("统计期间：%d年%d月\n\n", year, month));

        if (summary != null && !summary.isEmpty()) {
            DecimalFormat df = new DecimalFormat("#,##0.00");

            BigDecimal income = summary.get("income");
            BigDecimal expense = summary.get("expense");
            BigDecimal balance = summary.get("balance");

            sb.append(String.format("总收入：￥%s\n",
                    income != null ? df.format(income) : "0.00"));
            sb.append(String.format("总支出：￥%s\n",
                    expense != null ? df.format(expense) : "0.00"));
            sb.append("--------------------------------------------\n");
            sb.append(String.format("月结余：￥%s\n\n",
                    balance != null ? df.format(balance) : "0.00"));

            // 计算支出占比
            if (income != null && income.compareTo(BigDecimal.ZERO) > 0) {
                double expenseRatio = expense != null ?
                        expense.divide(income, 4, BigDecimal.ROUND_HALF_UP)
                                .multiply(new BigDecimal(100)).doubleValue() : 0;
                sb.append(String.format("支出占比：%.2f%%\n", expenseRatio));
            }

            // 结余率
            if (income != null && income.compareTo(BigDecimal.ZERO) > 0) {
                double balanceRatio = balance != null ?
                        balance.divide(income, 4, BigDecimal.ROUND_HALF_UP)
                                .multiply(new BigDecimal(100)).doubleValue() : 0;
                sb.append(String.format("结余率：%.2f%%\n", balanceRatio));
            }
        } else {
            sb.append("本月暂无收支记录\n");
        }

        sb.append("\n==================================================\n");
        sb.append("生成时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        reportArea.setText(sb.toString());
    }

    private void showCategoryStats(Date startDate, Date endDate, String type, String typeName) {
        java.util.List<Map<String, Object>> stats = billController.getCategoryStats(startDate, endDate, type);

        // 设置表头
        String[] columns = {"分类名称", "金额", "占比"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        if (stats != null && !stats.isEmpty()) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            DecimalFormat percentFormat = new DecimalFormat("0.00");

            for (Map<String, Object> stat : stats) {
                String category = (String) stat.get("category");
                BigDecimal amount = (BigDecimal) stat.get("amount");
                double percentage = (Double) stat.get("percentage");

                Object[] row = {
                        category,
                        "￥" + df.format(amount),
                        percentFormat.format(percentage) + "%"
                };
                tableModel.addRow(row);
            }
        }
    }

    private void showTrendAnalysis(Date startDate, Date endDate) {
        java.util.List<Map<String, Object>> trendData = billController.getTrendData(startDate, endDate);

        // 设置表头
        String[] columns = {"月份", "收入", "支出", "结余", "结余率"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        if (trendData != null && !trendData.isEmpty()) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            DecimalFormat percentFormat = new DecimalFormat("0.00");

            for (Map<String, Object> data : trendData) {
                String month = (String) data.get("month");
                BigDecimal income = (BigDecimal) data.get("income");
                BigDecimal expense = (BigDecimal) data.get("expense");

                if (income == null) income = BigDecimal.ZERO;
                if (expense == null) expense = BigDecimal.ZERO;

                BigDecimal balance = income.subtract(expense);
                double balanceRatio = 0;

                if (income.compareTo(BigDecimal.ZERO) > 0) {
                    balanceRatio = balance.divide(income, 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal(100)).doubleValue();
                }

                Object[] row = {
                        month,
                        "￥" + df.format(income),
                        "￥" + df.format(expense),
                        "￥" + df.format(balance),
                        percentFormat.format(balanceRatio) + "%"
                };
                tableModel.addRow(row);
            }
        }
    }

    @Override
    public void refresh() {
        loadReport();
    }
}