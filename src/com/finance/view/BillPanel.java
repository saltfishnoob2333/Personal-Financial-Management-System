package com.finance.view;

import com.finance.controller.BillController;
import com.finance.controller.CategoryController;
import com.finance.entity.Bill;
import com.finance.entity.Category;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BillPanel extends JPanel implements MainFrame.Refreshable {
    private BillController billController;
    private CategoryController categoryController;

    private JTable billTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeFilter;
    private JComboBox<Category> categoryFilter;
    private JTextField startDateField;
    private JTextField endDateField;
    private JLabel pageInfoLabel;

    private int currentPage = 1;
    private final int pageSize = 20;
    private Date startDateFilter = null;
    private Date endDateFilter = null;
    private String typeFilterValue = "";
    private Integer categoryFilterId = null;

    public BillPanel(BillController billController, CategoryController categoryController) {
        this.billController = billController;
        this.categoryController = categoryController;
        initComponents();
        loadBills();
        loadCategories();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 创建顶部筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("收支类型:"));
        typeFilter = new JComboBox<>(new String[]{"全部", "收入", "支出"});
        typeFilter.addActionListener(e -> filterChanged());
        filterPanel.add(typeFilter);

        filterPanel.add(new JLabel("分类:"));
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem(new Category("全部", "", 0));
        categoryFilter.addActionListener(e -> filterChanged());
        filterPanel.add(categoryFilter);

        filterPanel.add(new JLabel("开始日期:"));
        startDateField = new JTextField(10);
        startDateField.setToolTipText("格式: yyyy-MM-dd");
        filterPanel.add(startDateField);

        filterPanel.add(new JLabel("结束日期:"));
        endDateField = new JTextField(10);
        endDateField.setToolTipText("格式: yyyy-MM-dd");
        filterPanel.add(endDateField);

        JButton filterButton = new JButton("筛选");
        filterButton.addActionListener(e -> applyDateFilter());
        filterPanel.add(filterButton);

        JButton clearFilterButton = new JButton("清除筛选");
        clearFilterButton.addActionListener(e -> clearFilters());
        filterPanel.add(clearFilterButton);

        add(filterPanel, BorderLayout.NORTH);

        // 创建表格
        String[] columns = {"ID", "日期", "分类", "收支类型", "金额", "备注", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billTable = new JTable(tableModel);
        billTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(billTable);
        add(scrollPane, BorderLayout.CENTER);

        // 创建底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("添加账单");
        addButton.addActionListener(e -> openAddBillDialog());
        buttonPanel.add(addButton);

        JButton editButton = new JButton("编辑账单");
        editButton.addActionListener(e -> openEditBillDialog());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("删除账单");
        deleteButton.addActionListener(e -> deleteBill());
        buttonPanel.add(deleteButton);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);

        // 分页面板
        JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton firstPageButton = new JButton("首页");
        firstPageButton.addActionListener(e -> goToPage(1));
        pagePanel.add(firstPageButton);

        JButton prevPageButton = new JButton("上一页");
        prevPageButton.addActionListener(e -> goToPage(currentPage - 1));
        pagePanel.add(prevPageButton);

        pageInfoLabel = new JLabel("第 1 页");
        pagePanel.add(pageInfoLabel);

        JButton nextPageButton = new JButton("下一页");
        nextPageButton.addActionListener(e -> goToPage(currentPage + 1));
        pagePanel.add(nextPageButton);

        JButton lastPageButton = new JButton("末页");
        lastPageButton.addActionListener(e -> {
            int totalPages = billController.getTotalPages(
                    startDateFilter, endDateFilter, typeFilterValue, categoryFilterId, pageSize);
            goToPage(totalPages);
        });
        pagePanel.add(lastPageButton);

        bottomPanel.add(pagePanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadCategories() {
        List<Category> categories = categoryController.getAllCategories();
        if (categories != null) {
            for (Category category : categories) {
                categoryFilter.addItem(category);
            }
        }
    }

    private void loadBills() {
        // 清空表格
        tableModel.setRowCount(0);

        // 获取账单数据
        List<Bill> bills = billController.getBills(
                startDateFilter, endDateFilter, typeFilterValue,
                categoryFilterId, currentPage, pageSize);

        if (bills != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Bill bill : bills) {
                Object[] row = {
                        bill.getId(),
                        dateFormat.format(bill.getBillDate()),
                        bill.getCategoryName(),
                        bill.getType().equals("INCOME") ? "收入" : "支出",
                        bill.getAmount(),
                        bill.getRemark(),
                        datetimeFormat.format(bill.getCreateTime())
                };
                tableModel.addRow(row);
            }
        }

        // 更新分页信息
        updatePageInfo();
    }

    private void updatePageInfo() {
        int totalCount = billController.getBillCount(
                startDateFilter, endDateFilter, typeFilterValue, categoryFilterId);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        pageInfoLabel.setText(String.format("第 %d/%d 页，共 %d 条记录",
                currentPage, totalPages, totalCount));
    }

    private void filterChanged() {
        String selectedType = (String) typeFilter.getSelectedItem();
        typeFilterValue = selectedType.equals("全部") ? "" :
                selectedType.equals("收入") ? "INCOME" : "EXPENSE";

        Category selectedCategory = (Category) categoryFilter.getSelectedItem();
        categoryFilterId = (selectedCategory != null && selectedCategory.getId() != null) ?
                selectedCategory.getId() : null;

        currentPage = 1;
        loadBills();
    }

    private void applyDateFilter() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (!startDateField.getText().trim().isEmpty()) {
                startDateFilter = sdf.parse(startDateField.getText().trim());
            } else {
                startDateFilter = null;
            }

            if (!endDateField.getText().trim().isEmpty()) {
                endDateFilter = sdf.parse(endDateField.getText().trim());
            } else {
                endDateFilter = null;
            }

            currentPage = 1;
            loadBills();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期格式错误，请使用 yyyy-MM-dd 格式",
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFilters() {
        typeFilter.setSelectedIndex(0);
        categoryFilter.setSelectedIndex(0);
        startDateField.setText("");
        endDateField.setText("");
        startDateFilter = null;
        endDateFilter = null;
        typeFilterValue = "";
        categoryFilterId = null;
        currentPage = 1;
        loadBills();
    }

    private void goToPage(int page) {
        int totalPages = billController.getTotalPages(
                startDateFilter, endDateFilter, typeFilterValue, categoryFilterId, pageSize);

        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        currentPage = page;
        loadBills();
    }

    private void openAddBillDialog() {
        new BillDialog(null, billController, categoryController, this).setVisible(true);
    }

    private void openEditBillDialog() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的账单", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer billId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Bill bill = billController.getBillById(billId);

        if (bill != null) {
            new BillDialog(bill, billController, categoryController, this).setVisible(true);
        }
    }

    private void deleteBill() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的账单", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除这条账单吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Integer billId = (Integer) tableModel.getValueAt(selectedRow, 0);
            boolean success = billController.deleteBill(billId);

            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功", "成功",
                        JOptionPane.INFORMATION_MESSAGE);
                loadBills();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void refresh() {
        loadBills();
    }
}

// 账单对话框
class BillDialog extends JDialog {
    private Bill bill;
    private BillController billController;
    private CategoryController categoryController;
    private BillPanel billPanel;

    private JComboBox<Category> categoryCombo;
    private JComboBox<String> typeCombo;
    private JTextField amountField;
    private JTextField dateField;
    private JTextArea remarkArea;

    public BillDialog(Bill bill, BillController billController,
                      CategoryController categoryController, BillPanel billPanel) {
        super((Frame) null, bill == null ? "添加账单" : "编辑账单", true);
        this.bill = bill;
        this.billController = billController;
        this.categoryController = categoryController;
        this.billPanel = billPanel;

        initComponents();
        if (bill != null) {
            loadBillData();
        }
    }

    private void initComponents() {
        setSize(400, 350);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 收支类型
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("收支类型:"), gbc);

        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"收入", "支出"});
        typeCombo.addActionListener(e -> updateCategories());
        formPanel.add(typeCombo, gbc);

        // 分类
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("分类:"), gbc);

        gbc.gridx = 1;
        categoryCombo = new JComboBox<>();
        formPanel.add(categoryCombo, gbc);

        // 金额
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("金额:"), gbc);

        gbc.gridx = 1;
        amountField = new JTextField(15);
        formPanel.add(amountField, gbc);

        // 日期
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("日期:"), gbc);

        gbc.gridx = 1;
        dateField = new JTextField(15);
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        formPanel.add(dateField, gbc);

        // 备注
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("备注:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        remarkArea = new JTextArea(3, 15);
        remarkArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(remarkArea);
        formPanel.add(scrollPane, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> saveBill());

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 初始化分类
        updateCategories();
    }

    private void updateCategories() {
        String type = (String) typeCombo.getSelectedItem();
        String typeValue = type.equals("收入") ? "INCOME" : "EXPENSE";

        List<Category> categories = categoryController.getCategoriesByType(typeValue);
        categoryCombo.removeAllItems();

        if (categories != null) {
            for (Category category : categories) {
                categoryCombo.addItem(category);
            }
        }
    }

    private void loadBillData() {
        if (bill != null) {
            // 设置类型
            typeCombo.setSelectedItem(bill.getType().equals("INCOME") ? "收入" : "支出");

            // 更新分类列表并选中对应分类
            updateCategories();
            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                Category category = categoryCombo.getItemAt(i);
                if (category.getId().equals(bill.getCategoryId())) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }

            // 设置其他字段
            amountField.setText(bill.getAmount().toString());
            dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(bill.getBillDate()));
            remarkArea.setText(bill.getRemark() != null ? bill.getRemark() : "");
        }
    }

    private void saveBill() {
        try {
            // 验证输入
            if (categoryCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "请选择分类", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            String type = (String) typeCombo.getSelectedItem();
            String typeValue = type.equals("收入") ? "INCOME" : "EXPENSE";

            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "金额必须大于0", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date billDate = sdf.parse(dateField.getText().trim());

            String remark = remarkArea.getText().trim();

            boolean success;
            if (bill == null) {
                // 添加新账单
                success = billController.addBill(
                        selectedCategory.getId(), amount, typeValue, remark, billDate);
            } else {
                // 更新现有账单
                success = billController.updateBill(
                        bill.getId(), selectedCategory.getId(), amount, typeValue, remark, billDate);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "保存成功", "成功",
                        JOptionPane.INFORMATION_MESSAGE);
                billPanel.refresh();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "保存失败", "错误",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "金额格式错误", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期格式错误，请使用 yyyy-MM-dd 格式",
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}