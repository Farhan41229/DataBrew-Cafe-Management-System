package com.databrew.cafe.service;

import com.databrew.cafe.dao.InventoryDao;
import com.databrew.cafe.model.InventoryItem;

import java.sql.SQLException;
import java.util.List;

public class InventoryService {
    private final InventoryDao inventoryDao = new InventoryDao();

    public List<InventoryItem> listInventory() throws SQLException {
        return inventoryDao.listAll();
    }

    public List<InventoryItem> listLowStock() throws SQLException {
        return inventoryDao.findLowStock();
    }

    public long addItem(String name, String unit, double minThreshold, double quantity) throws SQLException {
        return inventoryDao.insertIngredientWithStock(name, unit, minThreshold, quantity);
    }

    public void updateItem(long inventoryId, long ingredientId, String name, String unit, double minThreshold,
            double quantity) throws SQLException {
        inventoryDao.updateItem(inventoryId, ingredientId, name, unit, minThreshold, quantity);
    }

    public void deleteItem(long inventoryId, long ingredientId) throws SQLException {
        inventoryDao.deleteItem(inventoryId, ingredientId);
    }
}
