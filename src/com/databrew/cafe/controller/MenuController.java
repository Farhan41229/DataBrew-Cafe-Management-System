package com.databrew.cafe.controller;

import com.databrew.cafe.dao.CategoryDao;
import com.databrew.cafe.dao.MenuDao;
import com.databrew.cafe.model.Category;
import com.databrew.cafe.model.MenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MenuController {
    @FXML
    private TableView<MenuItem> menuTable;
    @FXML
    private TableColumn<MenuItem, String> nameCol;
    @FXML
    private TableColumn<MenuItem, Number> priceCol;
    @FXML
    private TableColumn<MenuItem, String> categoryCol;
    @FXML
    private TableColumn<MenuItem, Boolean> statusCol;

    private final MenuDao menuDao = new MenuDao();
    private final CategoryDao categoryDao = new CategoryDao();
    private ObservableList<MenuItem> items = FXCollections.observableArrayList();
    private List<Category> categories;

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        priceCol.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getPrice()));
        categoryCol.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(resolveCategoryName(c.getValue().getCategoryId())));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isActive()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setText(null);
                } else {
                    setText(active ? "Active" : "Hidden");
                }
            }
        });
        refresh();
    }

    private void refresh() {
        try {
            categories = categoryDao.findAll();
            items = FXCollections.observableArrayList(menuDao.findAll());
            menuTable.setItems(items);
            menuTable.refresh();
        } catch (SQLException e) {
            showError("Failed to load menu: " + e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        MenuItem created = showEditDialog(null);
        if (created != null) {
            try {
                menuDao.insert(created);
                refresh();
            } catch (SQLException e) {
                showError("Create failed: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onEdit() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        MenuItem updated = showEditDialog(selected);
        if (updated != null) {
            try {
                menuDao.update(updated);
                refresh();
            } catch (SQLException e) {
                showError("Update failed: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onDelete() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getName() + "?", ButtonType.OK,
                ButtonType.CANCEL);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                menuDao.delete(selected.getId());
                refresh();
            } catch (SQLException e) {
                showError("Delete failed: " + e.getMessage());
            }
        }
    }

    private MenuItem showEditDialog(MenuItem existing) {
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Menu Item" : "Edit Menu Item");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField imageField = new TextField();
        ComboBox<Category> categoryBox = new ComboBox<>(FXCollections.observableArrayList(categories));
        categoryBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Category c) {
                return c == null ? "" : c.getName();
            }

            @Override
            public Category fromString(String s) {
                return null;
            }
        });
        CheckBox activeBox = new CheckBox("Active");

        if (existing != null) {
            nameField.setText(existing.getName());
            priceField.setText(String.valueOf(existing.getPrice()));
            imageField.setText(existing.getDescription());
            activeBox.setSelected(existing.isActive());
            categoryBox.getSelectionModel().select(categories.stream()
                    .filter(c -> c.getId() == existing.getCategoryId())
                    .findFirst().orElse(null));
        } else {
            activeBox.setSelected(true);
            if (!categories.isEmpty())
                categoryBox.getSelectionModel().selectFirst();
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Name"), nameField);
        grid.addRow(1, new Label("Price"), priceField);
        grid.addRow(2, new Label("Category"), categoryBox);
        grid.addRow(3, new Label("Image Path"), imageField);
        grid.addRow(4, new Label("Status"), activeBox);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK)
                return null;
            try {
                double price = Double.parseDouble(priceField.getText());
                if (categoryBox.getValue() == null)
                    return null;
                MenuItem item = existing == null ? new MenuItem() : existing;
                item.setName(nameField.getText());
                item.setPrice(price);
                item.setCategoryId(categoryBox.getValue().getId());
                item.setDescription(imageField.getText());
                item.setActive(activeBox.isSelected());
                return item;
            } catch (NumberFormatException ex) {
                showError("Price must be numeric");
                return null;
            }
        });

        Optional<MenuItem> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private String resolveCategoryName(long categoryId) {
        if (categories == null)
            return "";
        return categories.stream()
                .filter(c -> c.getId() == categoryId)
                .map(Category::getName)
                .findFirst()
                .orElse("Unknown");
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
