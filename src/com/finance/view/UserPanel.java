package com.finance.view;

import com.finance.controller.UserController;
import com.finance.entity.User;
import javax.swing.*;
import java.awt.*;

public class UserPanel extends JPanel implements MainFrame.Refreshable {
    private UserController userController;

    private JLabel usernameLabel;
    private JTextField nicknameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public UserPanel(UserController userController) {
        this.userController = userController;
        initComponents();
        loadUserInfo();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 创建标题
        JLabel titleLabel = new JLabel("用户信息管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 用户名（只读）
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        usernameLabel = new JLabel();
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);

        // 昵称
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("昵称:"), gbc);

        gbc.gridx = 1;
        nicknameField = new JTextField(20);
        formPanel.add(nicknameField, gbc);

        // 新密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("新密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("确认密码:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // 提示文本
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel hintLabel = new JLabel("提示：如果不想修改密码，请将密码字段留空");
        hintLabel.setFont(new Font("微软雅黑", Font.ITALIC, 12));
        hintLabel.setForeground(Color.GRAY);
        formPanel.add(hintLabel, gbc);

        // 警告文本
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JLabel warningLabel = new JLabel("警告：删除账户将永久删除所有数据，无法恢复！");
        warningLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        warningLabel.setForeground(Color.RED);
        formPanel.add(warningLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton saveButton = new JButton("保存修改");
        saveButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        saveButton.addActionListener(e -> saveUserInfo());

        JButton logoutButton = new JButton("退出登录");
        logoutButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        logoutButton.addActionListener(e -> logout());

        // 添加删除账户按钮
        JButton deleteAccountButton = new JButton("删除账户");
        deleteAccountButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        deleteAccountButton.setForeground(Color.RED);
        deleteAccountButton.addActionListener(e -> deleteAccount());

        buttonPanel.add(saveButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(deleteAccountButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUserInfo() {
        User currentUser = userController.getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            nicknameField.setText(currentUser.getNickname());
            passwordField.setText("");
            confirmPasswordField.setText("");
        }
    }

    private void saveUserInfo() {
        String nickname = nicknameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // 验证输入
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "昵称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.isEmpty()) {
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "密码长度至少6位", "错误",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "两次输入的密码不一致", "错误",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 如果密码为空，则保持原密码
        String finalPassword = password.isEmpty() ?
                userController.getCurrentUser().getPassword() : password;

        boolean success = userController.updateUser(finalPassword, nickname);

        if (success) {
            JOptionPane.showMessageDialog(this, "修改成功", "成功",
                    JOptionPane.INFORMATION_MESSAGE);
            loadUserInfo(); // 重新加载用户信息
        } else {
            JOptionPane.showMessageDialog(this, "修改失败", "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要退出登录吗？", "确认退出", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            userController.logout();

            // 关闭主窗口，返回登录界面
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }

            // 显示登录窗口
            UserController newUserController = new UserController();
            LoginFrame loginFrame = new LoginFrame(newUserController);
            loginFrame.setVisible(true);
        }
    }

    private void deleteAccount() {
        // 创建确认对话框
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><body style='width: 300px;'>" +
                        "<h3>警告：删除账户是不可逆的操作！</h3>" +
                        "<p>删除后，以下数据将被永久删除：</p>" +
                        "<ul>" +
                        "<li>您的所有账单记录</li>" +
                        "<li>您的自定义分类</li>" +
                        "<li>您的账户信息</li>" +
                        "</ul>" +
                        "<p><b>此操作无法撤销！</b></p>" +
                        "<p>您确定要继续吗？</p>" +
                        "</body></html>",
                "确认删除账户",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // 创建密码验证对话框
        JPasswordField passwordField = new JPasswordField(20);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("请输入您的密码以确认删除："), BorderLayout.NORTH);
        panel.add(passwordField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this,
                panel,
                "密码验证",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String password = new String(passwordField.getPassword());

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "密码不能为空！",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 验证密码
        boolean passwordCorrect = userController.verifyPassword(password);
        if (!passwordCorrect) {
            JOptionPane.showMessageDialog(this,
                    "密码错误！",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 最后一次确认
        int finalConfirm = JOptionPane.showConfirmDialog(this,
                "<html><body style='width: 300px;'>" +
                        "<h3 style='color: red;'>最后确认</h3>" +
                        "<p>您确定要永久删除账户吗？</p>" +
                        "<p><b>此操作将删除所有数据且无法恢复！</b></p>" +
                        "</body></html>",
                "最后确认",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);

        if (finalConfirm != JOptionPane.YES_OPTION) {
            return;
        }

        // 执行删除账户操作
        boolean success = userController.deleteAccount(password);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "账户已成功删除！",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);

            // 关闭主窗口
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }

            // 显示登录窗口
            UserController newUserController = new UserController();
            LoginFrame loginFrame = new LoginFrame(newUserController);
            loginFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "删除账户失败，请重试！",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        loadUserInfo();
    }
}