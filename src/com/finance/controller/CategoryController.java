package com.finance.controller;

import com.finance.entity.Category;
import com.finance.service.CategoryService;
import java.util.List;

public class CategoryController {
    private CategoryService categoryService = new CategoryService();
    private UserController userController;

    public CategoryController(UserController userController) {
        this.userController = userController;
    }

    /**
     * 获取所有分类（包括系统默认）
     */
    public List<Category> getAllCategories() {
        if (!userController.isLoggedIn()) {
            return null;
        }
        return categoryService.getUserCategories(userController.getCurrentUser().getId());
    }

    /**
     * 根据类型获取分类
     */
    public List<Category> getCategoriesByType(String type) {
        if (!userController.isLoggedIn()) {
            return null;
        }
        return categoryService.getCategoriesByType(userController.getCurrentUser().getId(), type);
    }

    /**
     * 添加分类
     */
    public boolean addCategory(String name, String type) {
        if (!userController.isLoggedIn()) {
            return false;
        }

        Category category = new Category(name, type, userController.getCurrentUser().getId());
        return categoryService.addCategory(category);
    }

    /**
     * 更新分类
     */
    public boolean updateCategory(Integer id, String name, String type) {
        if (!userController.isLoggedIn()) {
            return false;
        }

        Category category = new Category(name, type, userController.getCurrentUser().getId());
        category.setId(id);
        return categoryService.updateCategory(category);
    }

    /**
     * 删除分类
     */
    public boolean deleteCategory(Integer id) {
        if (!userController.isLoggedIn()) {
            return false;
        }

        return categoryService.deleteCategory(id, userController.getCurrentUser().getId());
    }

    /**
     * 根据ID获取分类
     */
    public Category getCategoryById(Integer id) {
        return categoryService.getCategoryById(id);
    }
}