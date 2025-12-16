package com.databrew.cafe.controller;

import com.databrew.cafe.dao.AttendanceDao;
import com.databrew.cafe.dao.EmployeeDao;
import com.databrew.cafe.dao.ShiftDao;
import com.databrew.cafe.dao.UserDao;
import com.databrew.cafe.model.Attendance;
import com.databrew.cafe.model.Employee;
import com.databrew.cafe.model.Shift;
import com.databrew.cafe.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EmployeeController {
    @FXML
    private ListView<Employee> employeeList;
    @FXML
    private TextField positionField;
    @FXML
    private DatePicker hireDatePicker;
    @FXML
    private TextField salaryField;
    @FXML
    private TextField userIdField;
    @FXML
    private TableView<Attendance> attendanceTable;
    @FXML
    private TableColumn<Attendance, LocalDate> attDateCol;
    @FXML
    private TableColumn<Attendance, String> attShiftCol;
    @FXML
    private TableColumn<Attendance, String> attStatusCol;
    @FXML
    private ComboBox<Shift> shiftBox;
    @FXML
    private DatePicker shiftDatePicker;
    @FXML
    private TableView<User> employeeTable;
    @FXML
    private TableColumn<User, Number> colId;
    @FXML
    private TableColumn<User, String> colName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, Boolean> colActive;

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final ShiftDao shiftDao = new ShiftDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final UserDao userDao = new UserDao();
    private ObservableList<Employee> employees = FXCollections.observableArrayList();
    private List<Shift> shifts;

    @FXML
    public void initialize() {
        employeeList.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> loadEmployee(n));
        attDateCol
                .setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getWorkDate()));
        attShiftCol.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getShiftId())));
        attStatusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        shiftDatePicker.setValue(LocalDate.now());

        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleLongProperty(c.getValue().getId()));
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUsername()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        colActive.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isActive()));
        loadData();
    }

    private void loadData() {
        try {
            employees = FXCollections.observableArrayList(employeeDao.findAll());
            employeeList.setItems(employees);
            shifts = shiftDao.findAll();
            shiftBox.setItems(FXCollections.observableArrayList(shifts));
            employeeTable.setItems(FXCollections.observableArrayList(userDao.findAll()));
            if (!employees.isEmpty()) {
                employeeList.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            showError("Failed to load employees: " + e.getMessage());
        }
    }

    @FXML
    private void onSave() {
        Employee sel = employeeList.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;
        try {
            sel.setPosition(positionField.getText());
            sel.setHireDate(hireDatePicker.getValue());
            sel.setSalary(Double.parseDouble(salaryField.getText()));
            sel.setUserId(parseLongNullable(userIdField.getText()));
            employeeDao.update(sel);
            loadData();
            employeeList.getSelectionModel().select(sel);
        } catch (NumberFormatException ex) {
            showError("Salary must be numeric");
        } catch (SQLException e) {
            showError("Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void onAssignShift() {
        Employee sel = employeeList.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;
        Shift chosen = shiftBox.getValue();
        LocalDate date = shiftDatePicker.getValue();
        if (chosen == null || date == null) {
            showError("Select shift and date");
            return;
        }
        try {
            attendanceDao.upsertAssignment(sel.getId(), chosen.getId(), date);
            loadAttendance(sel);
        } catch (SQLException e) {
            showError("Assign failed: " + e.getMessage());
        }
    }

    private void loadEmployee(Employee e) {
        if (e == null)
            return;
        positionField.setText(e.getPosition());
        hireDatePicker.setValue(e.getHireDate());
        salaryField.setText(String.valueOf(e.getSalary()));
        userIdField.setText(e.getUserId() == null ? "" : String.valueOf(e.getUserId()));
        loadAttendance(e);
    }

    private void loadAttendance(Employee e) {
        try {
            List<Attendance> rows = attendanceDao.findByEmployee(e.getId());
            attendanceTable.setItems(FXCollections.observableArrayList(rows));
        } catch (SQLException ex) {
            showError("Load attendance failed: " + ex.getMessage());
        }
    }

    private Long parseLongNullable(String text) {
        if (text == null || text.isBlank())
            return null;
        return Long.parseLong(text.trim());
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
