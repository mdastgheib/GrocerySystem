import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Properties;

public class managerController {
    @FXML
    public javafx.scene.control.TextField takeItem;
    @FXML
    public javafx.scene.control.TextField takeQ;
    @FXML
    public javafx.scene.control.TextField returnItem;
    @FXML
    public javafx.scene.control.TextField returnQ;
   @FXML
    public javafx.scene.control.TextArea list;

    @FXML
    public javafx.scene.control.TextField addItem;
    @FXML
    public javafx.scene.control.TextField addQ;
    @FXML
    public javafx.scene.control.TextField addP;
    @FXML
    public javafx.scene.control.TextField addLoc;
    @FXML
    public javafx.scene.control.TextField addExpr;



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

   public void addingColumn() throws Exception{
       String item = addItem.getText();
       String quantity = addQ.getText();
       String price = addP.getText();
       String location = addLoc.getText();
       String expiration = addExpr.getText();

       if( item == null || item.length() == 0 || quantity == null || quantity.length() == 0 || price == null || price.length() == 0 || location == null || location.length() == 0 || expiration == null || expiration.length() == 0){
           Alert notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields empty. ");
           notice.showAndWait();
           return;
       }

       Connection con = MySQLConnection.getConnection();
       String sql = "INSERT INTO Groceries VALUES (?, ?, ?, ?, ?)";
       PreparedStatement stmt = con.prepareStatement(sql);
       stmt.setString(1,  item);
       stmt.setInt(2, Integer.parseInt(quantity));
       stmt.setDouble(3, Double.parseDouble(price));
       stmt.setString(4, location);
       stmt.setString(5, expiration);
       stmt.executeUpdate();
   }
    // ATTEMPTING TO do addition with the SQL database
    public void testAddition() throws Exception{
        try{
            // grabing the text from the user (which are entered in the text fields)
            String itemName = returnItem.getText();
            String itemQuantity = returnQ.getText();
            // ERROR CHECKING
            if( itemName == null || itemName.length() == 0 || itemQuantity == null || itemQuantity.length() == 0){
                Alert notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields empty. ");
                notice.showAndWait();
                return;
            }
            Connection con = MySQLConnection.getConnection();
            String sql = " UPDATE Groceries SET Quantity = Quantity + ? WHERE Item = ?"; // we dont have the amount field yet i believe
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
            String itemName = takeItem.getText();
            String itemQuantity = takeQ.getText();
            // ERROR CHECKING
            if( itemName == null || itemName.length() == 0 || itemQuantity == null || itemQuantity.length() == 0){
                Alert notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields empty. ");
                notice.showAndWait();
                return;
            }
            Connection con = MySQLConnection.getConnection();
            String sql = " UPDATE Groceries SET Quantity = Quantity - ?  WHERE Item = ?"; // we dont have the amount field yet i believe
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1,  Integer.parseInt(itemQuantity));
            stmt.setString(2, itemName);
            stmt.executeUpdate();

        } catch (Exception e){
            System.out.println(e);
        }
    }


    public void taking() throws IOException{
        String key = takeItem.getText();
        String value = takeQ.getText();
        Alert notice;

        if(key == null || key.length() == 0 || value == null || value.length() == 0 )
        {
            notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields empty. ");
            notice.showAndWait();
        }

        HashMap<Object, Object> map = new HashMap<>();
        File file = new File("inventory.properties");
        FileInputStream reader = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(reader);
        reader.close();

        // filling the HashMap with the property file, if empty nothing will be in it
        // but the following if statement will fill it if anything is passed
        for(String mainKeys: properties.stringPropertyNames())
        {
            map.put(mainKeys, properties.get(mainKeys));
        }


        if(map.containsKey(key))
        {
            // Here i am converting from an object to a String (due to HashMap)
            String keyVal = (String) map.get(key);
            // I am making the keyVal and the val into ints
            int x = Integer.parseInt(keyVal);
            int y = Integer.parseInt(String.valueOf(value));
            int replaceVal = x - y;
            // them making the subtracted outcome into a string and replacing the old value in the HashMap
            String temp = Integer.toString(replaceVal);
            map.replace(key,map.get(key), temp);
            properties.putAll(map);
            FileOutputStream write = new FileOutputStream(file, true);
            properties.store(write, null);
            notice = new Alert(Alert.AlertType.CONFIRMATION, "Item successfully found and taken");
        }
        else
        {
            notice = new Alert(Alert.AlertType.ERROR, "Item not found, please check what items are in stock.");
        }
        notice.showAndWait();
    }
    public void returning() throws IOException
    {
        // checking to see if the user did not enter an item
        String key = returnItem.getText();
        String value = returnQ.getText();
        if(key == null || key.length() == 0 || value == null || value.length() == 0 ){
            Alert notice = new Alert(Alert.AlertType.ERROR, "Please do not leave any fields empty. ");
            notice.showAndWait();
        }
        // declaring my HashMap
        HashMap<Object, Object> map = new HashMap<>();
        File file = new File("inventory.properties");
        FileInputStream reader = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(reader);
        reader.close();

        // filling the HashMap with the property file is empty nothing will be in it
        // but the following if statement will fill it if anything is passed
        for(String mainKeys: properties.stringPropertyNames()){
            map.put(mainKeys, properties.get(mainKeys));
        }

        // checking if the map already contains the key
        if(map.containsKey(key)){
            // Here i am converting from an object to a String (due to HashMap)
            String keyVal = (String) map.get(key);
            // I am making the keyVal and the val into ints
            int x = Integer.parseInt(keyVal);
            int y = Integer.parseInt(String.valueOf(value));
            int replaceVal = x + y;
            String temp = Integer.toString(replaceVal);
            // replacing old value in the HashMap
            map.replace(key,map.get(key), temp);
            properties.putAll(map);
            FileOutputStream write = new FileOutputStream(file, true);
            properties.store(write, null);
            // notice = new Alert(Alert.AlertType.CONFIRMATION, "Item successfully added");
        } else {
            // so if this is not in the HashMap we just add it following the same procedure as above
            int y = Integer.parseInt(String.valueOf(value));
            int replaceVal = 0 + y;
            String temp = Integer.toString(replaceVal);
            map.put(key, temp);
            properties.putAll(map);
            FileOutputStream write = new FileOutputStream(file, true);
            properties.store(write, null);
            //notice = new Alert(Alert.AlertType.CONFIRMATION, "New item added to list!");

        }
        //notice.showAndWait();

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
        list.setText(finalMap);
    }
}
