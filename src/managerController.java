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
import java.util.ResourceBundle;

public class managerController implements Initializable{
    // DECLARING FXML variables
    // these variables are used whenever referencing objects in the fxml files
    @FXML
    private ComboBox<String> optionBox;
    @FXML
    private Button confirmBtn;
    @FXML
    private TextField itemTXT;
    @FXML
    private TextField quantityTXT;
    @FXML
    private TextField locationTXT;
    @FXML
    private TextField priceTXT;
    @FXML
    private TextField expirationTXT;

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

    // declaring our table
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

        //Disabling all text boxes until user selects an option from comboBox
        disabler(true);
        //Setting values into combobox
        optionBox.getItems().addAll("Add Item", "Delete Item", "Update Item");
        optionBox.setPromptText("Select an Operation:");

        //Checking for change in combobox that will then be ran through confirmButton function
        optionBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Add Item":
                    disabler(false);
                    break;
                case "Delete Item":
                    disabler(true);
                    itemTXT.setDisable(false);
                    confirmBtn.setDisable(false);
                    break;
                case "Update Item":
                    disabler(false);
                    confirmBtn.setDisable(false);
                    break;
            }
        });
    }// End initialize method

    public void confirmButton(ActionEvent event) throws Exception
    {
        // checking the option box value and calling functions accordingly
        if (optionBox.getValue().equals("Add Item")) {
            addingItem();
        }
        else if (optionBox.getValue().equals("Delete Item")) {
            deleteItem();
        }
        else if (optionBox.getValue().equals("Update Item")){
            updateItem();
        }

    }// End confirmButton method

   public void addingItem() throws Exception{

        // getting the values passed in through the text fields
       String item = itemTXT.getText();
       String quantity = quantityTXT.getText();
       String price = priceTXT.getText();
       String location = locationTXT.getText();
       String expiration = expirationTXT.getText();

       // checking to see if any of the fields are NULL as all fields must
       // contain a value inorder for the add method to function properly
       if( item == null || item.length() == 0 || quantity == null || quantity.length() == 0 || price == null || price.length() == 0 || location == null || location.length() == 0 || expiration == null || expiration.length() == 0)
       {
           Alert notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields blank!");
           notice.showAndWait();
           return;
       }
        // this string is for the transaction view which records all transactions
       String addH = item + " with a quantity of " + quantity + " was added to the inventory";
       // connection to sql
       Connection con = MySQLConnection.getConnection();
       // creating the sql strings which are then passed through prepared statements
       String sql = "INSERT INTO Groceries VALUES (?, ?, ?, ?, ?)";
       String add = "INSERT INTO Transactions VALUES (?)";
       PreparedStatement addItem = con.prepareStatement(sql);
       PreparedStatement addHistory = con.prepareStatement(add);
       // filling in prepared statements
       addItem.setString(1,  item);
       addItem.setInt(2, Integer.parseInt(quantity));
       addItem.setDouble(3, Double.parseDouble(price));
       addItem.setString(4, location);
       addItem.setString(5, expiration);
       addHistory.setString(1, addH);
       addItem.executeUpdate();
       addHistory.executeUpdate();
       System.out.println(add);
       // updating the tables
       updateTable();


   } // end addingItem() method


    public void deleteItem() throws Exception {
        // getting the string from the text field
        String item = itemTXT.getText();
        // error checking if null
        if (item == null || item.length() == 0) {
            Alert notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields empty.");
            notice.showAndWait();
            return;
        }
        // creating a string for transaction view
        String deleteH = item + " was deleted from the inventory";
        // connecting to sql server and creating sql statements
        Connection con = MySQLConnection.getConnection();
        String sql = "DELETE FROM Groceries WHERE item = ?";
        // passing sql statements throuhg prepared statements to protect form injection
        PreparedStatement stmt = con.prepareStatement(sql);
        PreparedStatement delH = con.prepareStatement("INSERT INTO Transactions VALUES (?)");
        delH.setString(1, deleteH);
        stmt.setString(1, item);
        stmt.executeUpdate();
        delH.executeUpdate();
        // calling update table
        updateTable();
    } // end of deleteItem() method

   public void updateItem() throws Exception
   {
       // getting the text from fxml fields
       String itemName = itemTXT.getText();
       String itemQuantity = quantityTXT.getText();
       String price = priceTXT.getText();
       String location = locationTXT.getText();
       String exprDt = expirationTXT.getText();

       // error checking, basically checking if all fields are NULL which we dont want is ok is some are NULL all cannot be null though
       if( (itemQuantity == null || itemQuantity.length() == 0) && (price == null || price.length() == 0) && (location == null || location.length() == 0) && (exprDt == null || exprDt.length() == 0)){
           Alert notice = new Alert(Alert.AlertType.ERROR, "Please Enter the Values you want updated");
           notice.showAndWait();
           return;
       }
       // if item quantity field isnt null we update quantity
       if(itemQuantity != null && itemQuantity.length() != 0){
           try{
            // creating sql connection
           Connection con = MySQLConnection.getConnection();
           // creating transaction string
           String qnt = itemName + "  was updated with a quantity of  " + itemQuantity;
           // creating sql string and passing through prepared statements
           String sql = "UPDATE Groceries SET Quantity = Quantity + ? WHERE Item = ?";
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
       // if price isnt null we update price
       if(price != null && price.length() != 0){
            try{
                // creating sql connection
                Connection con = MySQLConnection.getConnection();
                // creating sql string and passing through prepared statements
                String sql = "UPDATE Groceries SET Price =  ? WHERE Item = ?";
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setDouble(1,  Double.parseDouble(price));
                stmt.setString(2, itemName);
                stmt.executeUpdate();

            } catch (Exception e){
                System.out.println(e);
            }
       }
       // if location isnt null we update location
       if( location != null && location.length() != 0 ){
           try{
               // creating sql connection
               Connection con = MySQLConnection.getConnection();
               // creating sql string and passing through prepared statements
               String sql = "UPDATE Groceries SET Location =  ? WHERE Item = ?";
               PreparedStatement stmt = con.prepareStatement(sql);
               stmt.setString(1,  location);
               stmt.setString(2, itemName);
               stmt.executeUpdate();

           } catch (Exception e){
               System.out.println(e);
           }
       }
       // if exprDt isnt null we update exprDt
       if( exprDt != null && exprDt.length() != 0){
           try{
               // creating sql connection
               Connection con = MySQLConnection.getConnection();
               // creating sql string and passing through prepared statements
               String sql = "UPDATE Groceries SET Expiration =  ? WHERE Item = ?";
               PreparedStatement stmt = con.prepareStatement(sql);
               stmt.setString(1,  exprDt);
               stmt.setString(2, itemName);
               stmt.executeUpdate();

           } catch (Exception e){
               System.out.println(e);
           }
       }
       // calling update table
      updateTable();
   } // end updateItem() method

   public void updateTable(){
       try
       {
           // connecting to sql
           Connection con = MySQLConnection.getConnection();
           oblist = FXCollections.observableArrayList();
           ResultSet rs = con.createStatement().executeQuery("select * from Groceries");
            // filling the table
           while (rs.next()) {
               oblist.add(new Table(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),rs.getString(5)));
           }

       } catch (SQLException throwables) {
           throwables.printStackTrace();
       }
    // setting the column values
       column_item.setCellValueFactory(new PropertyValueFactory<>("Item"));
       column_quantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
       column_price.setCellValueFactory(new PropertyValueFactory<>("Price"));
       column_location.setCellValueFactory(new PropertyValueFactory<>("Location"));
       column_expiration.setCellValueFactory(new PropertyValueFactory<>("Expiration"));

       fxTable.setItems(null);
       fxTable.setItems(oblist);

   }// end updateTable() method

   public void returnMain(ActionEvent event) throws IOException {
        // this method is related to a button that brings back to the main login screen
        Parent mainPage = FXMLLoader.load(getClass().getResource("/Style/fxmls/main.fxml"));
        Scene main = new Scene(mainPage);
        //Obtaining stage information and setting our new scene/fxml
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(main);
        currentStage.show();
    }// end returnMain() method

    public void viewTransactions(ActionEvent event) throws IOException {
        // this method is a button that brings up a scene which displays the transaction history
        Parent transactions = FXMLLoader.load(getClass().getResource("/Style/fxmls/transactions.fxml"));
        Scene T = new Scene(transactions);
        //Obtaining stage information and setting our new scene/fxml
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(T);
        currentStage.show();
    }// end viewTransactions() method

    //Method that will intake boolean value for setDisable options
    public void disabler (Boolean option) {
        itemTXT.setDisable(option);
        priceTXT.setDisable(option);
        quantityTXT.setDisable(option);
        locationTXT.setDisable(option);
        expirationTXT.setDisable(option);
        confirmBtn.setDisable(option);

    }//End disabler method


}
