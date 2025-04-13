package org.example.labbd;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class SecondController implements Initializable {
    @FXML private HBox root;
    @FXML private VBox inputPane;
    @FXML private TextField firstNameField;
    @FXML private TextField secondNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField genderField;
    @FXML private TextField emailField;
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, Integer> clientIdColumn;
    @FXML private TableColumn<Client, String> firstNameColumn;
    @FXML private TableColumn<Client, String> secondNameColumn;
    @FXML private TableColumn<Client, String> lastNameColumn;
    @FXML private TableColumn<Client, String> phoneNumberColumn;
    @FXML private TableColumn<Client, String> genderColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableView<Report> reportTable;
    @FXML private TableColumn<Report, Integer> reportIdColumn;
    @FXML private TableColumn<Report, Integer> reportFirstNameColumn;
    @FXML private TableColumn<Report, String> reportLastNameColumn;
    @FXML private TableColumn<Report, Integer> reportVisitCountColumn;
    @FXML private TableColumn<Report, Integer> reportTotalPriceColumn;
    @FXML private StackPane tablePane;
    @FXML private Button clientsButton;
    @FXML private Button reportButton;
    @FXML private Button exportButton;
    @FXML private Label labelState;

    private ObservableList<Client> clientData = FXCollections.observableArrayList();
    private ObservableList<Report> reportData = FXCollections.observableArrayList();

    public static class Client {
        private int ClientID;
        private String firstName;
        private String secondName;
        private String lastName;
        private String phoneNumber;
        private String gender;
        private String email;

        public Client(int ClientID, String firstName, String secondName, String lastName, String phoneNumber, String gender, String email) {
            this.ClientID = ClientID;
            this.firstName = firstName;
            this.secondName = secondName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.gender = gender;
            this.email = email;
        }

        public int getClientID() { return ClientID; }
        public String getFirstName() { return firstName; }
        public String getSecondName() { return secondName; }
        public String getLastName() { return lastName; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getGender() { return gender; }
        public String getEmail() { return email; }

        public void setFirstName(String firstName) { this.firstName = firstName; }
        public void setSecondName(String secondName) { this.secondName = secondName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public void setGender(String gender) { this.gender = gender; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class Report {
        private int clientID;
        private String firstName;
        private String lastName;
        private int visitCount;
        private int totalPrice;

        public Report(int clientID, String firstName, String lastName, int visitCount, int totalPrice) {
            this.clientID = clientID;
            this.firstName = firstName;
            this.lastName = lastName;
            this.visitCount = visitCount;
            this.totalPrice = totalPrice;
        }

        public int getClientID() { return clientID; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public int getVisitCount() { return visitCount; }
        public int getTotalPrice() { return totalPrice; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Настройка колонок таблицы клиентов
        clientIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        secondNameColumn.setCellValueFactory(new PropertyValueFactory<>("secondName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Настройка колонок таблицы отчёта
        reportIdColumn.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        reportFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        reportLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        reportVisitCountColumn.setCellValueFactory(new PropertyValueFactory<>("visitCount"));
        reportTotalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        // Привязка данных к таблицам
        clientTable.setItems(clientData);
        reportTable.setItems(reportData);

        // Загрузка данных
        loadClientData();
        loadReportData();

        // Слушатель для заполнения полей ввода при выборе клиента
        clientTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                firstNameField.setText(newValue.getFirstName());
                secondNameField.setText(newValue.getSecondName());
                lastNameField.setText(newValue.getLastName());
                phoneNumberField.setText(newValue.getPhoneNumber());
                genderField.setText(newValue.getGender());
                emailField.setText(newValue.getEmail());
            }
        });

        // Слушатели для автоматического обновления при изменении текста
        addTextFieldListeners();

        // Установка начального состояния — режим клиентов
        showClients();
    }

    private void addTextFieldListeners() {
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> updateSelectedClient("Firstname", newValue));
        secondNameField.textProperty().addListener((observable, oldValue, newValue) -> updateSelectedClient("Fathername", newValue));
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> updateSelectedClient("Lastname", newValue));
        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> updateSelectedClient("NumberPhone", newValue));
        genderField.textProperty().addListener((observable, oldValue, newValue) -> updateSelectedClient("Gender", newValue));
        emailField.textProperty().addListener((observable, oldValue, newValue) -> updateSelectedClient("Email", newValue));
    }

    private void updateSelectedClient(String columnName, String newValue) {
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient == null || !clientTable.isVisible()) return;

        // Обновляем данные в объекте Client
        switch (columnName) {
            case "Firstname": selectedClient.setFirstName(newValue); break;
            case "Fathername": selectedClient.setSecondName(newValue); break;
            case "Lastname": selectedClient.setLastName(newValue); break;
            case "NumberPhone": selectedClient.setPhoneNumber(newValue); break;
            case "Gender": selectedClient.setGender(newValue); break;
            case "Email": selectedClient.setEmail(newValue); break;
        }

        // Обновляем базу данных
        String sql = "UPDATE Client SET " + columnName + " = ? WHERE ID_Client = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newValue);
            pstmt.setInt(2, selectedClient.getClientID());
            pstmt.executeUpdate();
            labelState.setText("Поле " + columnName + " обновлено для клиента ID: " + selectedClient.getClientID());
        } catch (SQLException e) {
            e.printStackTrace();
            labelState.setText("Ошибка при обновлении поля " + columnName + ": " + e.getMessage());
        }

        // Обновляем таблицу
        clientTable.refresh();
        loadReportData();
    }

    private void loadClientData() {
        clientData.clear();
        String sql = "SELECT ID_Client, Firstname, Fathername, Lastname, NumberPhone, Gender, Email FROM Client";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientData.add(new Client(
                        rs.getInt("ID_Client"),
                        rs.getString("Firstname"),
                        rs.getString("Fathername"),
                        rs.getString("Lastname"),
                        rs.getString("NumberPhone"),
                        rs.getString("Gender"),
                        rs.getString("Email")
                ));
            }
            labelState.setText("Данные клиентов загружены!");
        } catch (SQLException e) {
            e.printStackTrace();
            labelState.setText("Ошибка при загрузке данных клиентов: " + e.getMessage());
        }
    }

    private void loadReportData() {
        reportData.clear();
        String sql = """
            SELECT cl.ID_Client AS id, cl.Firstname AS Имя, cl.Lastname AS Фамилия,
                   COUNT(rov.ID_Visits) AS Количество_посещений,
                   COALESCE(SUM(sub.Price), 0) AS Общая_стоимость
            FROM Client cl
            LEFT JOIN sale_of_subscriptions sos ON sos.Client_ID_Client = cl.ID_Client
            LEFT JOIN subscription sub ON sos.Subscription_ID_Subscription = sub.ID_Subscription
            LEFT JOIN registration_of_visits rov ON rov.Sale_of_subscriptions_Bank_card_num = sos.Bank_card_num
                AND rov.Sale_of_subscriptions_Subscription_ID_Subscription = sos.Subscription_ID_Subscription
                AND rov.Sale_of_subscriptions_Fitness_center_ID_Fitness_center = sos.Fitness_center_ID_Fitness_center
            GROUP BY cl.ID_Client, cl.Firstname, cl.Lastname
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reportData.add(new Report(
                        rs.getInt("id"),
                        rs.getString("Имя"),
                        rs.getString("Фамилия"),
                        rs.getInt("Количество_посещений"),
                        rs.getInt("Общая_стоимость")
                ));
            }
            labelState.setText("Данные отчёта загружены!");
        } catch (SQLException e) {
            e.printStackTrace();
            labelState.setText("Ошибка при загрузке данных отчёта: " + e.getMessage());
        }
    }

    @FXML
    private void addClient() {
        String firstName = firstNameField.getText();
        String secondName = secondNameField.getText();
        String lastName = lastNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String gender = genderField.getText();
        String email = emailField.getText();
        String sql = "INSERT INTO Client (Firstname, Fathername, Lastname, NumberPhone, Gender, Email) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, secondName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, gender);
            pstmt.setString(6, email);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Вставка клиента не удалась, ни одна строка не затронута.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    labelState.setText("Клиент добавлен с ID: " + generatedId);
                } else {
                    labelState.setText("Клиент добавлен, но ID не получен.");
                }
            }
            clearFields();
            loadClientData();
            labelState.setText("Клиент добавлен: " + firstName + " " + lastName);
        } catch (SQLException e) {
            e.printStackTrace();
            labelState.setText("Ошибка при добавлении клиента: " + e.getMessage());
        }
    }

    @FXML
    private void deleteClient() {
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            labelState.setText("Выберите клиента для удаления!");
            return;
        }
        String sql = "DELETE FROM Client WHERE ID_Client = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selectedClient.getClientID());
            pstmt.executeUpdate();
            String firstName = selectedClient.getFirstName();
            String lastName = selectedClient.getLastName();
            clearFields();
            loadClientData();
            labelState.setText("Клиент удалён: " + firstName + " " + lastName);
        } catch (SQLException e) {
            e.printStackTrace();
            labelState.setText("Ошибка при удалении клиента: " + e.getMessage());
        }
    }

    @FXML
    private void showClients() {
        clientTable.setVisible(true);
        reportTable.setVisible(false);
        inputPane.setVisible(true);
        exportButton.setVisible(false);
        clientsButton.setDisable(true);
        reportButton.setDisable(false);
        labelState.setText("Управление данными клиентов");
    }

    @FXML
    private void showReport() {
        clientTable.setVisible(false);
        reportTable.setVisible(true);
        inputPane.setVisible(false);
        exportButton.setVisible(true);
        clientsButton.setDisable(false);
        reportButton.setDisable(true);
        labelState.setText("Отображение отчёта по посещениям");
    }

    @FXML
    private void exportToExcel() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку для сохранения отчёта");
        // Устанавливаем начальную директорию на папку пользователя
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());

        if (selectedDirectory == null) {
            labelState.setText("Сохранение отменено: папка не выбрана");
            return;
        }

        File outputFile = new File(selectedDirectory, "ClientVisitsReport.xlsx");
        String filePath = outputFile.getAbsolutePath();

        Workbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("Clients Visits Report");
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Имя", "Фамилия", "Количество посещений", "Общая стоимость"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Report report : reportData) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getClientID());
                row.createCell(1).setCellValue(report.getFirstName());
                row.createCell(2).setCellValue(report.getLastName());
                row.createCell(3).setCellValue(report.getVisitCount());
                row.createCell(4).setCellValue(report.getTotalPrice());
            }

            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
                labelState.setText("Отчёт сохранён в " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            String errorMessage = e.getMessage().contains("Отказано в доступе")
                    ? "Отказано в доступе. Выберите другую папку, например, Документы."
                    : "Ошибка сохранения: " + e.getMessage();
            labelState.setText(errorMessage);
            System.err.println("Failed to save Excel file at: " + filePath);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                System.err.println("Error closing workbook: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        firstNameField.clear();
        secondNameField.clear();
        lastNameField.clear();
        phoneNumberField.clear();
        genderField.clear();
        emailField.clear();
    }

    @FXML
    private void openMainForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/labbd/MainForm.fxml"));
            Parent mainRoot = loader.load();
            Stage mainStage = new Stage();
            mainStage.setTitle("Fitness Center Database Manager");
            mainStage.setScene(new Scene(mainRoot));
            mainStage.show();

            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            labelState.setText("Ошибка при открытии главного окна: " + e.getMessage());
        }
    }
}