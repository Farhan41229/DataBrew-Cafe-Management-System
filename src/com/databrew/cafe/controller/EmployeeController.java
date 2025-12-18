package com.databrew.cafe.controller;

import com.databrew.cafe.dao.AttendanceDao;
import com.databrew.cafe.dao.EmployeeDao;
import com.databrew.cafe.dao.ShiftDao;
import com.databrew.cafe.dao.UserDao;
import com.databrew.cafe.util.PasswordUtil;
import com.databrew.cafe.model.Attendance;
import com.databrew.cafe.model.Employee;
import com.databrew.cafe.model.Shift;
import com.databrew.cafe.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EmployeeController {
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Number> colEmpId;
    @FXML
    private TableColumn<Employee, String> colEmpName;
    @FXML
    private TableColumn<Employee, String> colEmpBranch;
    @FXML
    private TableColumn<Employee, Number> colEmpAge;
    @FXML
    private TableColumn<Employee, String> colEmpShift;
    @FXML
    private TableColumn<Employee, Number> colEmpSalary;
    @FXML
    private TableColumn<Employee, String> colEmpStatus;

    @FXML
    private TextField nameField;
    @FXML
    private TextField branchField;
    @FXML
    private TextField ageField;
    @FXML
    private ComboBox<String> statusCombo;
    @FXML
    private TextField positionField;
    @FXML
    private DatePicker hireDatePicker;
    @FXML
    private TextField salaryField;
    @FXML
    private TextField bankField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<Shift> shiftBox;
    @FXML
    private Label currentShiftLabel;

    @FXML
    private TableView<Attendance> attendanceTable;
    @FXML
    private TableColumn<Attendance, LocalDate> attDateCol;
    @FXML
    private TableColumn<Attendance, String> attShiftCol;
    @FXML
    private TableColumn<Attendance, String> attStatusCol;

    @FXML
    private VBox detailsPane;
    @FXML
    private VBox shiftsPane;
    @FXML
    private VBox accountsPane;
    @FXML
    private Button tabDetailsBtn;
    @FXML
    private Button tabShiftsBtn;
    @FXML
    private Button tabAccountsBtn;

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final ShiftDao shiftDao = new ShiftDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final UserDao userDao = new UserDao();
    private ObservableList<Employee> employees = FXCollections.observableArrayList();
    private List<Shift> shifts;

    @FXML
    public void initialize() {
        wireTable();
        wireAttendanceTable();
        setActiveTab("details");
        loadData();
    }

    private void wireTable() {
        colEmpId.setCellValueFactory(c -> new javafx.beans.property.SimpleLongProperty(c.getValue().getId()));
        colEmpName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmpBranch.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBranch()));
        colEmpAge.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(
                c.getValue().getAge() == null ? 0 : c.getValue().getAge()));
        colEmpShift.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(shiftNameFromId(c.getValue().getShiftId())));
        colEmpSalary.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getSalary()));
        colEmpStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> loadEmployee(n));
        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive"));
        statusCombo.getSelectionModel().selectFirst();
        shiftBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Shift item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatShift(item));
            }
        });
        shiftBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Shift item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatShift(item));
            }
        });
    }

    private void wireAttendanceTable() {
        if (attendanceTable == null || attDateCol == null || attShiftCol == null || attStatusCol == null) {
            return; // Attendance table not present in this view
        }
        attDateCol
                .setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getWorkDate()));
        attShiftCol.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getShiftId())));
        attStatusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
    }

    private void loadData() {
        try {
            shifts = shiftDao.findAll();
            shiftBox.setItems(FXCollections.observableArrayList(shifts));
            employees = FXCollections.observableArrayList(employeeDao.findAll());
            employeeTable.setItems(employees);
            if (!employees.isEmpty()) {
                employeeTable.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            showError("Failed to load employees: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdateEmployee() {
        Employee sel = employeeTable.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;
        try {
            Employee updated = buildEmployeeFromForm(sel);
            if (updated == null) {
                return;
            }
            employeeDao.update(sel);
            loadData();
            employeeTable.getSelectionModel().select(sel);
        } catch (NumberFormatException ex) {
            showError("Salary must be numeric");
        } catch (SQLException e) {
            showError("Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void onAddEmployee() {
        try {
            Employee e = buildEmployeeFromForm(new Employee());
            if (e == null) {
                return;
            }
            long newId = employeeDao.insert(e);
            loadData();
            employees.stream().filter(emp -> emp.getId() == newId).findFirst()
                    .ifPresent(emp -> employeeTable.getSelectionModel().select(emp));
        } catch (NumberFormatException ex) {
            showError("Salary must be numeric");
        } catch (SQLException e) {
            showError("Add failed: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteEmployee() {
        Employee sel = employeeTable.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this employee?", ButtonType.OK,
                ButtonType.CANCEL);
        confirm.showAndWait().filter(btn -> btn == ButtonType.OK).ifPresent(btn -> {
            try {
                employeeDao.delete(sel.getId());
                loadData();
            } catch (SQLException e) {
                showError("Delete failed: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onAssignShift() {
        Employee sel = employeeTable.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;
        Shift chosen = shiftBox.getValue();
        if (chosen == null) {
            showError("Select a shift");
            return;
        }
        try {
            attendanceDao.upsertAssignment(sel.getId(), chosen.getId(), LocalDate.now());
            sel.setShiftId(chosen.getId());
            employeeDao.update(sel);
            currentShiftLabel.setText(formatShift(chosen));
            loadAttendance(sel);
        } catch (SQLException e) {
            showError("Assign failed: " + e.getMessage());
        }
    }

    @FXML
    private void onManageShifts() {
        Alert info = new Alert(Alert.AlertType.INFORMATION, "Shift management is not implemented yet.", ButtonType.OK);
        info.setHeaderText("Manage Shifts");
        info.showAndWait();
    }

    @FXML
    private void onUpdateAccounts() {
        onUpdateEmployee();
    }

    @FXML
    private void onSave() {
        Employee sel = employeeTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            onAddEmployee();
        } else {
            onUpdateEmployee();
        }
    }

    @FXML
    private void onClearForm() {
        employeeTable.getSelectionModel().clearSelection();
        nameField.clear();
        branchField.clear();
        ageField.clear();
        statusCombo.getSelectionModel().selectFirst();
        positionField.clear();
        hireDatePicker.setValue(null);
        salaryField.clear();
        bankField.clear();
        passwordField.clear();
        shiftBox.getSelectionModel().clearSelection();
        currentShiftLabel.setText("None");
        if (attendanceTable != null) {
            attendanceTable.setItems(FXCollections.observableArrayList());
        }
        setActiveTab("details");
    }

    private void loadEmployee(Employee e) {
        if (e == null)
            return;
        nameField.setText(e.getFullName());
        branchField.setText(e.getBranch());
        ageField.setText(e.getAge() == null ? "" : String.valueOf(e.getAge()));
        statusCombo.getSelectionModel().select(e.getStatus() == null ? "Active" : e.getStatus());
        positionField.setText(e.getPosition());
        hireDatePicker.setValue(e.getHireDate());
        salaryField.setText(String.valueOf(e.getSalary()));
        bankField.setText(e.getBankAccount());
        passwordField.clear();
        if (e.getShiftId() != null && shifts != null) {
            Shift match = shifts.stream().filter(s -> s.getId() == e.getShiftId()).findFirst().orElse(null);
            shiftBox.getSelectionModel().select(match);
            currentShiftLabel.setText(match == null ? String.valueOf(e.getShiftId()) : formatShift(match));
        } else {
            shiftBox.getSelectionModel().clearSelection();
            currentShiftLabel.setText("None");
        }
        loadAttendance(e);
    }

    private void loadAttendance(Employee e) {
        if (attendanceTable == null) {
            return;
        }
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

    private Employee buildEmployeeFromForm(Employee target) {
        if (target == null) {
            target = new Employee();
        }
        if (nameField.getText() == null || nameField.getText().isBlank()) {
            showError("Name is required");
            return null;
        }
        target.setFullName(nameField.getText().trim());
        target.setBranch(branchField.getText() == null ? null : branchField.getText().trim());
        target.setAge(ageField.getText().isBlank() ? null : Integer.parseInt(ageField.getText().trim()));
        target.setStatus(statusCombo.getSelectionModel().getSelectedItem());
        target.setPosition(positionField.getText());
        target.setHireDate(hireDatePicker.getValue());
        if (salaryField.getText() == null || salaryField.getText().isBlank()) {
            target.setSalary(0);
        } else {
            target.setSalary(Double.parseDouble(salaryField.getText().trim()));
        }
        target.setBankAccount(bankField.getText());
        if (passwordField.getText() != null && !passwordField.getText().isBlank()) {
            target.setPasswordHash(PasswordUtil.hash(passwordField.getText()));
        }
        Shift chosen = shiftBox.getSelectionModel().getSelectedItem();
        target.setShiftId(chosen == null ? null : chosen.getId());
        target.setUserId(null);
        return target;
    }

    private String formatShift(Shift s) {
        return s.getName() + " (" + s.getStartTime() + "-" + s.getEndTime() + ")";
    }

    private String shiftNameFromId(Long id) {
        if (id == null || shifts == null) {
            return "Unassigned";
        }
        return shifts.stream()
                .filter(s -> s.getId() == id)
                .map(Shift::getName)
                .findFirst()
                .orElse("Unassigned");
    }

    @FXML
    private void handleShowDetails() {
        setActiveTab("details");
    }

    @FXML
    private void handleShowShifts() {
        setActiveTab("shifts");
    }

    @FXML
    private void handleShowAccounts() {
        setActiveTab("accounts");
    }

    private void setActiveTab(String tab) {
        togglePane(detailsPane, tab.equals("details"));
        togglePane(shiftsPane, tab.equals("shifts"));
        togglePane(accountsPane, tab.equals("accounts"));
        updatePillState(tabDetailsBtn, tab.equals("details"));
        updatePillState(tabShiftsBtn, tab.equals("shifts"));
        updatePillState(tabAccountsBtn, tab.equals("accounts"));
    }

    private void togglePane(VBox pane, boolean visible) {
        if (pane != null) {
            pane.setVisible(visible);
            pane.setManaged(visible);
        }
    }

    private void updatePillState(Button btn, boolean active) {
        if (btn == null)
            return;
        if (active) {
            if (!btn.getStyleClass().contains("pill-btn-active")) {
                btn.getStyleClass().add("pill-btn-active");
            }
        } else {
            btn.getStyleClass().remove("pill-btn-active");
        }
    }
}
