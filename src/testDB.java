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
    private TableColumn <Table, String> column_item;
    @FXML
    private TableColumn <Table, String> column_quantity;
    @FXML
    private TableColumn <Table, String> column_price;
    @FXML
    private TableColumn <Table, String> column_location;
    @FXML
    private TableColumn <Table, String> column_expiration;

    ObservableList <Table> oblist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Calling our sql class to connect to our database
        try {
            Connection con = MySQLConnection.getConnection();
            oblist = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery("select * from Groceries");

            while (rs.next())
            {
                oblist.add(new Table(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),rs.getString(5)));
            }

        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        column_item.setCellValueFactory(new PropertyValueFactory<>("Item"));
        column_quantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        column_price.setCellValueFactory(new PropertyValueFactory<>("Price"));
        column_location.setCellValueFactory(new PropertyValueFactory<>("Location"));
        column_expiration.setCellValueFactory(new PropertyValueFactory<>("Expiration"));

        fxTable.setItems(null);
        fxTable.setItems(oblist);


    }
}
