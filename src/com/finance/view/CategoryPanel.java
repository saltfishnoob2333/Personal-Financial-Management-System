package com.finance.view;

import com.finance.controller.CategoryController;
import com.finance.entity.Category;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoryPanel extends JPanel implements MainFrame.Refreshable {
    private CategoryController categoryController;

    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeCombo;

    public CategoryPanel(CategoryController categoryController) {
        this.categoryController = categoryController;
        initComponents();
        loadCategories();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 创建顶部面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        topPanel.add(new JLabel("分类类型:"));
        typeCombo = new JComboBox<>(new String[]{"全部", "收入", "支出"});
        typeCombo.addActionListener(e -> loadCategories());
        topPanel.add(typeCombo);

        add(topPanel, BorderLayout.NORTH);

        // 创建表格
        String[] columns = {"ID", "分类名称", "类型", "创建者"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);

        // 创建底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("添加分类");
        addButton.addActionListener(e -> openAddCategoryDialog());
        buttonPanel.add(addButton);

        JButton editButton = new JButton("编辑分类");
        editButton.addActionListener(e -> openEditCategoryDialog());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("删除分类");
        deleteButton.addActionListener(e -> deleteCategory());
        buttonPanel.add(deleteButton);

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadCategories());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCategories() {
        // 清空表格
        tableModel.setRowCount(0);

        List<Category> categories = categoryController.getAllCategories();
        if (categories != null) {
            String selectedType = (String) typeCombo.getSelectedItem();

            for (Category category : categories) {
                // 根据筛选条件过滤
                if (!selectedType.equals("全部")) {
                    String expectedType = selectedType.equals("收入") ? "INCOME" : "EXPENSE";
                    if (!category.getType().equals(expectedType)) {
                        continue;
                    }
                }

                String creator = category.getUserId() == 0 ? "系统" : "用户";
                String typeDisplay = category.getType().equals("INCOME") ? "收入" : "支出";

                Object[] row = {
                        category.getId(),
                        category.getName(),
                        typeDisplay,
                        creator
                };
                tableModel.addRow(row);
            }
        }
    }

    private void openAddCategoryDialog() {
        new CategoryDialog(null, categoryController, this).setVisible(true);
    }

    private void openEditCategoryDialog() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的分类", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer categoryId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Category category = categoryController.getCategoryById(categoryId);

        if (category != null) {
            // 只能编辑用户自己的分类
            if (category.getUserId() == 0) {
                JOptionPane.showMessageDialog(this, "不能编辑系统默认分类", "错误",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            new CategoryDialog(category, categoryController, this).setVisible(true);
        }
    }

    private void deleteCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的分类", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer categoryId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Category category = categoryController.getCategoryById(categoryId);

        if (category == null) {
            return;
        }

        // 不能删除系统默认分类
        if (category.getUserId() == 0) {
            JOptionPane.showMessageDialog(this, "不能删除系统默认分类", "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除分类 \"" + category.getName() + "\" 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = categoryController.deleteCategory(categoryId);

            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功", "成功",
                        JOptionPane.INFORMATION_MESSAGE);
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void refresh() {
        loadCategories();
    }
}

// 分类对话框
class CategoryDialog extends JDialog {
    private Category category;
    private CategoryController categoryController;
    private CategoryPanel categoryPanel;

    private JTextField nameField;
    private JComboBox<String> typeCombo;

    public CategoryDialog(Category category, CategoryController categoryController,
                          CategoryPanel categoryPanel) {
        super((Frame) null, category == null ? "添加分类" : "编辑分类", true);
        this.category = category;
        this.categoryController = categoryController;
        this.categoryPanel = categoryPanel;

        initComponents();
        if (category != null) {
            loadCategoryData();
        }
    }

    private void initComponents() {
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 分类名称
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("分类名称:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);

        // 分类类型
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("分类类型:"), gbc);

        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"收入", "支出"});
        formPanel.add(typeCombo, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> saveCategory());

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadCategoryData() {
        if (category != null) {
            nameField.setText(category.getName());
            typeCombo.setSelectedItem(category.getType().equals("INCOME") ? "收入" : "支出");
        }
    }

    private void saveCategory() {
        String name = nameField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        String typeValue = type.equals("收入") ? "INCOME" : "EXPENSE";

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "分类名称不能为空", "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success;
        if (category == null) {
            // 添加分类
            success = categoryController.addCategory(name, typeValue);
        } else {
            // 更新分类
            success = categoryController.updateCategory(category.getId(), name, typeValue);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "保存成功", "成功",
                    JOptionPane.INFORMATION_MESSAGE);
            categoryPanel.refresh();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "保存失败，可能分类名称已存在", "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}