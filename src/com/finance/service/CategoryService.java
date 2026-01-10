package com.finance.service;

import com.finance.dao.CategoryDao;
import com.finance.dao.impl.CategoryDaoImpl;
import com.finance.entity.Category;
import java.util.List;

public class CategoryService {
    private CategoryDao categoryDao = new CategoryDaoImpl();

    /**
     * 获取系统默认分类
     */
    public List<Category> getSystemCategories() {
        return categoryDao.getSystemCategories();
    }

    /**
     * 获取用户分类（包括系统默认）
     */
    public List<Category> getUserCategories(Integer userId) {
        return categoryDao.getUserCategories(userId);
    }

    /**
     * 根据类型获取分类
     */
    public List<Category> getCategoriesByType(Integer userId, String type) {
        return categoryDao.getCategoriesByType(userId, type);
    }

    /**
     * 添加分类
     */
    public boolean addCategory(Category category) {
        // 检查分类名是否已存在
        if (categoryDao.exists(category.getName(), category.getUserId())) {
            return false;
        }
        return categoryDao.add(category);
    }

    /**
     * 更新分类
     */
    public boolean updateCategory(Category category) {
        Category existing = categoryDao.findById(category.getId());
        if (existing == null || !existing.getUserId().equals(category.getUserId())) {
            return false;
        }

        // 检查新名称是否与其他分类冲突
        if (!existing.getName().equals(category.getName()) &&
                categoryDao.exists(category.getName(), category.getUserId())) {
            return false;
        }

        return categoryDao.update(category);
    }

    /**
     * 删除分类
     */
    public boolean deleteCategory(Integer id, Integer userId) {
        // 检查分类是否存在且属于该用户
        Category category = categoryDao.findById(id);
        if (category == null || !category.getUserId().equals(userId)) {
            return false;
        }
        return categoryDao.delete(id, userId);
    }

    /**
     * 根据ID获取分类
     */
    public Category getCategoryById(Integer id) {
        return categoryDao.findById(id);
    }
}