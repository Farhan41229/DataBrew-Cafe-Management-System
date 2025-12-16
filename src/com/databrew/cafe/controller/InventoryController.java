package com.databrew.cafe.controller;

import com.databrew.cafe.model.InventoryItem;
import com.databrew.cafe.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class InventoryController {
    @FXML
    private TableView<InventoryItem> inventoryTable;
    @FXML
    private TableColumn<InventoryItem, Long> ingredientCol;
    @FXML
    private TableColumn<InventoryItem, Double> qtyCol;

    private final InventoryService inventoryService = new InventoryService();

    @FXML
    public void initialize() {
        ingredientCol.setCellValueFactory(new PropertyValueFactory<>("ingredientId"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        refresh();
    }

    private void refresh() {
        try {
            List<InventoryItem> items = inventoryService.listInventory();
            inventoryTable.setItems(FXCollections.observableArrayList(items));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Inventory load failed: " + e.getMessage()).showAndWait();
        }
    }
}
