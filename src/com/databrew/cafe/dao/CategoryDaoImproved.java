package com.databrew.cafe.dao;

import com.databrew.cafe.model.Category;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Improved CategoryDao using BaseDao utilities.
 * 
 * This is an example of how to refactor existing DAOs to use the BaseDao class.
 * Benefits:
 * - Less boilerplate code
 * - Automatic resource management
 * - Consistent error handling
 * - Better logging
 * 
 * Compare this with the original CategoryDao.java to see the improvements!
 */
public class CategoryDaoImproved extends BaseDao {

    /**
     * Finds all categories, ordered by name.
     * 
     * Original code: 18 lines with manual try-with-resources
     * Improved code: 3 lines using BaseDao.queryForList()
     * 
     * @return List of all categories
     * @throws SQLException if database operation fails
     */
    public List<Category> findAll() throws SQLException {
        String sql = "SELECT id, name, description FROM categories ORDER BY name";
        return queryForList(sql, this::mapCategory);
    }

    /**
     * Finds a category by ID.
     * 
     * @param id The category ID
     * @return The category, or null if not found
     * @throws SQLException if database operation fails
     */
    public Category findById(long id) throws SQLException {
        String sql = "SELECT id, name, description FROM categories WHERE id = ?";
        return queryForObject(sql, this::mapCategory, id);
    }

    /**
     * Inserts a new category.
     * 
     * @param category The category to insert
     * @return The generated ID
     * @throws SQLException if database operation fails
     */
    public long insert(Category category) throws SQLException {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        return executeInsertAndGetKey(sql, category.getName(), category.getDescription());
    }

    /**
     * Updates an existing category.
     * 
     * @param category The category to update
     * @return Number of rows affected
     * @throws SQLException if database operation fails
     */
    public int update(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
        return executeUpdate(sql, category.getName(), category.getDescription(), category.getId());
    }

    /**
     * Deletes a category by ID.
     * 
     * @param id The category ID
     * @return Number of rows affected
     * @throws SQLException if database operation fails
     */
    public int delete(long id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id = ?";
        return executeUpdate(sql, id);
    }

    /**
     * Counts total number of categories.
     * 
     * @return The count
     * @throws SQLException if database operation fails
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM categories";
        return queryForInt(sql, "cnt");
    }

    /**
     * Maps a ResultSet row to a Category object.
     * 
     * @param rs The ResultSet
     * @return The mapped Category
     * @throws SQLException if mapping fails
     */
    private Category mapCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        return c;
    }
}
