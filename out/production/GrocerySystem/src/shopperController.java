import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

public class shopperController implements Initializable{

    @FXML
    private Button addtoCartBtn;
    @FXML
    private Button placeOrderBtn;
    @FXML
    private TextField itemTXT;
    @FXML
    private TextField quantityTXT;
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

    /*Table Schema (SQL DB)
    - DB Name: Inventory
    - Table Name: Groceries
    +------------+-------------+------+-----+---------+-------+
    | Field      | Type        | Null | Key | Default | Extra |
    +------------+-------------+------+-----+---------+-------+
    | Item       | varchar(20) | NO   |     | NULL    |       |
    | Quantity   | int         | NO   |     | NULL    |       |
    | Price      | double      | NO   |     | NULL    |       |
    | Location   | varchar(20) | NO   |     | NULL    |       |
    | Expiration | varchar(20) | NO   |     | NULL    |       |
    +------------+-------------+------+-----+---------+-------+

    SQl Queries:
     - Add New Item:
        INSERT INTO Groceries VALUES ('ITEM', 'QUANTITY', 'PRICE', 'LOCATION', 'EXPIRATION');
     - Updating (Add/Sub):
         UPDATE Groceries SET Quantity = Quantity -/+ 2 WHERE Item = 'NAME';

    */


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        updateTable();

        BooleanBinding booleanBind = itemTXT.textProperty().isEmpty()
                .or(quantityTXT.textProperty().isEmpty());

        addtoCartBtn.disableProperty().bind(booleanBind);

        BooleanBinding booleanBind2 = itemTXT.textProperty().isEmpty()
                .or(quantityTXT.textProperty().isEmpty());

        placeOrderBtn.disableProperty().bind(booleanBind2);

    }// End initialize method

    public void removeItem() throws Exception
    {
        //additem subtract item
        // update expr date, update price, update location
        String itemName = itemTXT.getText();
        String itemQuantity = quantityTXT.getText();

        if( (itemName == null || itemName.length() == 0 || itemQuantity == null || itemQuantity.length() == 0 || Integer.parseInt(itemQuantity) <=0) ){
            Alert notice = new Alert(Alert.AlertType.ERROR, "Please Enter an Item from the List and a Positive Value to Add to Cart");
            notice.showAndWait();
            return;
        }
        if(itemQuantity != null && itemQuantity.length() != 0){
            try{

                Connection con = MySQLConnection.getConnection();
                String qnt = itemName + "  was updated with a quantity of  -" + itemQuantity;
                String sql = "UPDATE Groceries SET Quantity = Quantity - ? WHERE Item = ?"; // we dont have the amount field yet i believe
                PreparedStatement stmt = con.prepareStatement(sql);
                PreparedStatement qntH = con.prepareStatement("INSERT INTO Transactions VALUES (?)");
                qntH.setString(1, qnt);
                stmt.setInt(1,  Integer.parseInt(itemQuantity));
                stmt.setString(2, itemName);
                stmt.executeUpdate();
                qntH.executeUpdate();

            } catch (Exception e) {

                System.out.println(e);
            }
        }

        updateTable();
    }

    public void updateTable(){
        try
        {
            Connection con = MySQLConnection.getConnection();
            oblist = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery("select * from Groceries");

            while (rs.next()) {
                oblist.add(new Table(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),rs.getString(5)));
            }

        } catch (SQLException throwables) {
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

    public void returnMain(ActionEvent event) throws IOException {
        Parent mainPage = FXMLLoader.load(getClass().getResource("/Style/fxmls/main.fxml"));
        Scene main = new Scene(mainPage);
        //Obtaining stage information and setting our new scene/fxml
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(main);
        currentStage.show();
    }

    public void placeOrder(ActionEvent event) throws IOException {
        Parent mainPage = FXMLLoader.load(getClass().getResource("/Style/fxmls/driver.fxml"));
        Scene main = new Scene(mainPage);
        //Obtaining stage information and setting our new scene/fxml
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(main);
        currentStage.show();
    }
}
