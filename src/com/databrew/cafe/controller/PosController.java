package com.databrew.cafe.controller;

import com.databrew.cafe.dao.MenuDao;
import com.databrew.cafe.model.MenuItem;
import com.databrew.cafe.model.Order;
import com.databrew.cafe.model.OrderItem;
import com.databrew.cafe.service.OrderService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PosController {
    @FXML
    private ListView<MenuItem> menuList;
    @FXML
    private TableView<OrderItem> cartTable;
    @FXML
    private Label totalLabel;
    @FXML
    private TextField customerField;
    @FXML
    private ComboBox<String> methodCombo;

    private final MenuDao menuDao = new MenuDao();
    private final OrderService orderService = new OrderService();
    private final List<OrderItem> cart = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            List<MenuItem> items = menuDao.findActive();
            menuList.setItems(FXCollections.observableArrayList(items));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Menu load failed: " + e.getMessage()).showAndWait();
        }
        methodCombo.setItems(FXCollections.observableArrayList("CASH", "CARD", "MFS"));
        methodCombo.getSelectionModel().selectFirst();
        cartTable.setItems(FXCollections.observableArrayList(cart));
    }

    @FXML
    public void onAdd() {
        MenuItem selected = menuList.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        OrderItem oi = new OrderItem();
        oi.setMenuItemId(selected.getId());
        oi.setQuantity(1);
        oi.setUnitPrice(selected.getPrice());
        oi.setLineTotal(selected.getPrice());
        cart.add(oi);
        cartTable.setItems(FXCollections.observableArrayList(cart));
        updateTotal();
    }

    @FXML
    public void onCheckout() {
        if (cart.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Cart is empty").showAndWait();
            return;
        }
        Order order = new Order();
        order.setCustomerName(customerField.getText());
        order.setCustomerType("GENERAL");
        order.setItems(cart);
        double subtotal = cart.stream().mapToDouble(OrderItem::getLineTotal).sum();
        order.setSubtotal(subtotal);
        order.setDiscountAmount(0);
        order.setTaxAmount(0);
        order.setTotal(subtotal);
        order.setTaxId(null);
        order.setDiscountId(null);

        try {
            long orderId = orderService.createOrderWithItems(order, cart);
            orderService.recordPaymentAndInvoice(orderId, order.getTotal(), methodCombo.getValue());
            new Alert(Alert.AlertType.INFORMATION, "Order completed").showAndWait();
            cart.clear();
            cartTable.setItems(FXCollections.observableArrayList(cart));
            updateTotal();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Checkout failed: " + e.getMessage()).showAndWait();
        }
    }

    private void updateTotal() {
        double total = cart.stream().mapToDouble(OrderItem::getLineTotal).sum();
        totalLabel.setText(String.format("$%.2f", total));
    }
}
