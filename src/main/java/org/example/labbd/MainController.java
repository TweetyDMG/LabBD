package org.example.labbd;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class MainController{
    @FXML
    private VBox root;
    @FXML
    private TextField databaseNameField;
    @FXML
    private TextField tableNameField;
    @FXML
    private TextField columnNameField;
    @FXML
    private TextField dataTypeField;
    @FXML
    private Label labelState;

    @FXML
    private void createDatabase() {
        String dbName = databaseNameField.getText();
        String sql = "CREATE DATABASE " + dbName;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            labelState.setText("База данных " + dbName + " создана!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createTable() {
        String dbName = databaseNameField.getText();
        String tableName = tableNameField.getText();
        String columnName = columnNameField.getText();
        String dataType = dataTypeField.getText();

        if(tableName.isEmpty() || columnName.isEmpty() || dataType.isEmpty()) {
            labelState.setText("Пожалуйста, заполните все поля.");
            return;
        }

        String useSql = "USE " + dbName + ";";
        String sql = "CREATE TABLE " + tableName + " (" + columnName + " " + dataType + " PRIMARY KEY)";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(useSql);
            stmt.executeUpdate(sql);
            labelState.setText("Таблица " + tableName + " создана в базе данных " + dbName);
        } catch (SQLException e) {
            e.printStackTrace();
            labelState.setText("Ошибка при создании таблицы " + e.getMessage());
        }
    }

    @FXML
    private void addColumn() {
        String dbName = databaseNameField.getText();
        String tableName = tableNameField.getText();
        String columnName = columnNameField.getText();
        String dataType = dataTypeField.getText();
        String useSql = "USE " + dbName + ";";
        String sql = "ALTER TABLE " + tableName + " ADD " + columnName + " " + dataType;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(useSql);
            stmt.executeUpdate(sql);
            labelState.setText("Столбец " + columnName + " добавлен в таблицу " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteColumn() {
        String dbName = databaseNameField.getText();
        String tableName = tableNameField.getText();
        String columnName = columnNameField.getText();
        String useSql = "USE " + dbName + ";";
        String sql = "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(useSql);
            stmt.executeUpdate(sql);
            labelState.setText("Столбец " + columnName + " удален из таблицы " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSecondForm(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/labbd/SecondForm.fxml"));
            Parent secondRoot = loader.load();
            Stage secondStage = new Stage();
            secondStage.setTitle("Управление данными клиентов");
            secondStage.setScene(new Scene(secondRoot));
            secondStage.show();

            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            labelState.setText("Ошибка при открытии второго окна: " + e.getMessage());
        }
    }
}