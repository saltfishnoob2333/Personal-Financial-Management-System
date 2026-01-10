package com.finance.view;

import com.finance.controller.UserController;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private UserController userController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame(UserController userController) {
        this.userController = userController;
        initComponents();
    }

    private void initComponents() {
        setTitle("个人收支记账系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // 创建面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建标题
        JLabel titleLabel = new JLabel("个人收支记账系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建表单面板
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

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        loginButton = new JButton("登录");
        loginButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        loginButton.addActionListener(e -> login());

        registerButton = new JButton("注册");
        registerButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        registerButton.addActionListener(e -> openRegisterDialog());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 添加回车键监听
        getRootPane().setDefaultButton(loginButton);

        add(mainPanel);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = userController.login(username, password);

        if (success) {
            JOptionPane.showMessageDialog(this, "登录成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            new MainFrame(userController).setVisible(true);
            dispose(); // 关闭登录窗口
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegisterDialog() {
        new RegisterDialog(this, userController).setVisible(true);
    }
}