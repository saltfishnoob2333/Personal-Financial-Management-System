package com.finance.view;

import com.finance.controller.UserController;
import javax.swing.*;

public class FinanceApplication {
    public static void main(String[] args) {
        // 设置外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 启动应用程序
        SwingUtilities.invokeLater(() -> {
            UserController userController = new UserController();
            new LoginFrame(userController).setVisible(true);
        });
    }
}