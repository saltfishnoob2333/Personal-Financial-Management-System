package com.finance.dao;

import com.finance.entity.Category;
import java.util.List;

public interface CategoryDao {
    // 获取所有系统默认分类
    List<Category> getSystemCategories();

    // 获取用户自定义分类
    List<Category> getUserCategories(Integer userId);

    // 根据类型获取分类
    List<Category> getCategoriesByType(Integer userId, String type);

    // 添加分类
    boolean add(Category category);

    // 更新分类
    boolean update(Category category);

    // 删除分类
    boolean delete(Integer id, Integer userId);

    // 根据ID查询分类
    Category findById(Integer id);

    // 检查分类是否存在
    boolean exists(String name, Integer userId);
}