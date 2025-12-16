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
}
