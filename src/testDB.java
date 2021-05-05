import java.net.URL;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.*;

public class testDB implements Initializable
{
    @FXML
    private TableView <Table> fxTable;
    @FXML
    private TableColumn <Table, String> column_location;
    @FXML
    private TableColumn <Table, String> column_expiration;
    @FXML
    private TableColumn <Table, String> column_item;

    ObservableList <Table> oblist = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Calling our sql class to connect to our database
        try {
            Connection con = MySQLConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("select * from Groceries");

            while (rs.next())
            {
                oblist.add(new Table(rs.getString("Location"), rs.getString("Expiration"), rs.getString("Item")));
            }

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        column_location.setCellValueFactory(new PropertyValueFactory<>("Location"));
        column_expiration.setCellValueFactory(new PropertyValueFactory<>("Expiration"));
        column_item.setCellValueFactory(new PropertyValueFactory<>("Item"));

        fxTable.setItems(oblist);


    }
}
