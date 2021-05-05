import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

public class managerController implements Initializable{
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


        //Disabling all text boxes until user selects an option from comboBox
        disabler(true);
        //Setting values into combobox
        optionBox.getItems().addAll("Add Item", "Delete Item", "Update Item");
        optionBox.setPromptText("Select an Operation:");

        //Checking for change in combobox that will then be ran through confirmButton function
        optionBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == "Add Item") {
                disabler(false);
            }

            else if (newValue == "Delete Item") {
                disabler(true);
                itemTXT.setDisable(false);
                confirmBtn.setDisable(false);
            }

            else if (newValue == "Update Item") {
                disabler(false);
                confirmBtn.setDisable(false);
            }});
    }// End initialize method

    public void confirmButton(ActionEvent event) throws Exception
    {
        if (optionBox.getValue() == "Add Item") {
            addingItem();
        }
        else if (optionBox.getValue() == "Delete Item") {
            deleteItem();
        }
        else if (optionBox.getValue() == "Update Item"){
            //updateItem();

        }

    }
   public void addingItem() throws Exception{
       String item = itemTXT.getText();
       String quantity = quantityTXT.getText();
       String price = priceTXT.getText();
       String location = locationTXT.getText();
       String expiration = expirationTXT.getText();

       if( item == null || item.length() == 0 || quantity == null || quantity.length() == 0 || price == null || price.length() == 0 || location == null || location.length() == 0 || expiration == null || expiration.length() == 0)
       {
           Alert notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields blank!");
           notice.showAndWait();
           return;
       }

       Connection con = MySQLConnection.getConnection();
       String sql = "INSERT INTO Groceries VALUES (?, ?, ?, ?, ?)";
       PreparedStatement addItem = con.prepareStatement(sql);
       addItem.setString(1,  item);
       addItem.setInt(2, Integer.parseInt(quantity));
       addItem.setDouble(3, Double.parseDouble(price));
       addItem.setString(4, location);
       addItem.setString(5, expiration);
       addItem.executeUpdate();
   }


    public void deleteItem() throws Exception {
        String item = itemTXT.getText();
        if (item == null || item.length() == 0) {
            Alert notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields empty.");
            notice.showAndWait();
            return;
        }
        Connection con = MySQLConnection.getConnection();
        String sql = "DELETE FROM Groceries WHERE item = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, item);
        stmt.executeUpdate();
    }


   public void UpdateItem() throws Exception
   {

   }

    // ATTEMPTING TO do addition with the SQL database
    public void testAddition() {
        try{
            // grabing the text from the user (which are entered in the text fields)
            String itemName = itemTXT.getText();
            String itemQuantity = quantityTXT.getText();
            // ERROR CHECKING
            if( itemName == null || itemName.length() == 0 || itemQuantity == null || itemQuantity.length() == 0){
                Alert notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields empty. ");
                notice.showAndWait();
                return;
            }
            Connection con = MySQLConnection.getConnection();
            String sql = "UPDATE Groceries SET Quantity = Quantity + ? WHERE Item = ?"; // we dont have the amount field yet i believe
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1,  Integer.parseInt(itemQuantity));
            stmt.setString(2, itemName);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void testSubtraction() throws Exception{
        try{
            String itemName = itemTXT.getText();
            String itemQuantity = quantityTXT.getText();
            // ERROR CHECKING
            if( itemName == null || itemName.length() == 0 || itemQuantity == null || itemQuantity.length() == 0){
                Alert notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields empty. ");
                notice.showAndWait();
                return;
            }
            Connection con = MySQLConnection.getConnection();
            String sql = "UPDATE Groceries SET Quantity = Quantity - ?  WHERE Item = ?"; // we dont have the amount field yet i believe
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1,  Integer.parseInt(itemQuantity));
            stmt.setString(2, itemName);
            stmt.executeUpdate();

        } catch (Exception e){
            System.out.println(e);
        }
    }


    public void loadList() throws IOException {
        String finalMap ="";
        HashMap<Object, Object> map = new HashMap<>();
        File file = new File("inventory.properties");
        FileInputStream reader = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(reader);
        reader.close();
        // filling the HashMap
        for(String mainKeys: properties.stringPropertyNames()){
            map.put(mainKeys, properties.get(mainKeys));
        }

        // putting the HashMap into a formatted String
        String theMap = map.toString();
        String[] newMap = theMap.split(",");
        for( int i = 0; i < newMap.length; i++){
            if(i == 0 ) {
                finalMap += newMap[i].substring(1) + "\n";
            } else if (i == map.size() - 1){
                finalMap += newMap[i].substring(0, newMap[i].length() - 1) + "\n";
            } else {
                finalMap += newMap[i] + "\n";
            }
        }
        //list.setText(finalMap);
    }

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
