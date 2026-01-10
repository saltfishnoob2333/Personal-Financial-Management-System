package com.finance.view;

import com.finance.controller.BillController;
import com.finance.controller.CategoryController;
import com.finance.controller.UserController;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private UserController userController;
    private CategoryController categoryController;
    private BillController billController;

    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel;

    public MainFrame(UserController userController) {
        this.userController = userController;
        this.categoryController = new CategoryController(userController);
        this.billController = new BillController(userController);

        initComponents();
    }

    private void initComponents() {
        setTitle("个人收支记账系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // 创建菜单栏
        createMenuBar();

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建欢迎标签
        welcomeLabel = new JLabel("欢迎，", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        updateWelcomeLabel();
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();

        // 添加账单管理标签页
        BillPanel billPanel = new BillPanel(billController, categoryController);
        tabbedPane.addTab("账单管理", billPanel);

        // 添加分类管理标签页
        CategoryPanel categoryPanel = new CategoryPanel(categoryController);
        tabbedPane.addTab("分类管理", categoryPanel);

        // 添加统计报表标签页
        ReportPanel reportPanel = new ReportPanel(billController);
        tabbedPane.addTab("统计报表", reportPanel);

        // 添加用户管理标签页
        UserPanel userPanel = new UserPanel(userController);
        tabbedPane.addTab("用户管理", userPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 文件菜单
        JMenu fileMenu = new JMenu("文件");
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // 工具菜单
        JMenu toolsMenu = new JMenu("工具");
        JMenuItem refreshItem = new JMenuItem("刷新数据");
        refreshItem.addActionListener(e -> refreshAllTabs());
        toolsMenu.add(refreshItem);
        menuBar.add(toolsMenu);

        setJMenuBar(menuBar);
    }

    private void updateWelcomeLabel() {
        if (userController.isLoggedIn()) {
            String nickname = userController.getCurrentUser().getNickname();
            welcomeLabel.setText("欢迎，" + nickname + "！");
        }
    }

    private void refreshAllTabs() {
        Component[] tabs = tabbedPane.getComponents();
        for (Component tab : tabs) {
            if (tab instanceof Refreshable) {
                ((Refreshable) tab).refresh();
            }
        }
        JOptionPane.showMessageDialog(this, "数据已刷新", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    // 可刷新接口
    interface Refreshable {
        void refresh();
    }
}