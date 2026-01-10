package com.finance.view;

import com.finance.controller.UserController;
import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {
    private UserController userController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField nicknameField;

    public RegisterDialog(JFrame parent, UserController userController) {
        super(parent, "用户注册", true);
        this.userController = userController;
        initComponents();
    }

    private void initComponents() {
        setSize(400, 350);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("确认密码:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        confirmPasswordField = new JPasswordField(15);
        formPanel.add(confirmPasswordField, gbc);

        // 昵称
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("昵称:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        nicknameField = new JTextField(15);
        formPanel.add(nicknameField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(e -> register());

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String nickname = nicknameField.getText().trim();

        // 验证输入
        if (username.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段都必须填写", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "密码长度至少6位", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 检查用户名是否存在
        if (userController.checkUsernameExists(username)) {
            JOptionPane.showMessageDialog(this, "用户名已存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 注册用户
        boolean success = userController.register(username, password, nickname);

        if (success) {
            JOptionPane.showMessageDialog(this, "注册成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "注册失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}