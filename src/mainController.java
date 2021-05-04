import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class mainController {

    @FXML
    public javafx.scene.control.TextField username;
    @FXML
    public javafx.scene.control.TextField password;

    public String user = "";

    // public static int loggingIn(String k1, int v1) throws IOException {
    public void logIn(ActionEvent event) throws IOException {
        String k1 = username.getText();
        String v1 = password.getText();
        this.user = k1;
        if(k1 == null || k1.length() == 0 || v1 == null || v1.length() == 0){
            Alert notice = new Alert(Alert.AlertType.ERROR, " Please Make Sure You entered a Username and Password. ");
            notice.showAndWait();
            return;
        }
        // declaring my HashMap
        HashMap<Object, Object> customer = new HashMap<>();
        File file = new File("customer.properties");
        FileInputStream reader = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(reader);
        reader.close();

        HashMap<Object, Object> driver = new HashMap<>();
        File file1 = new File("driver.properties");
        FileInputStream reader1 = new FileInputStream(file1);
        Properties properties1 = new Properties();
        properties1.load(reader1);
        reader.close();

        HashMap<Object, Object> username = new HashMap<>();
        File file2 = new File("username.properties");
        FileInputStream reader2 = new FileInputStream(file2);
        Properties properties2 = new Properties();
        properties2.load(reader2);
        reader.close();

        // filling the HashMap with the property file is empty nothing will be in it
        // but the following if statement will fill it if anything is passed
        for(String mainKeys: properties.stringPropertyNames()){
            customer.put(mainKeys, properties.get(mainKeys));
        }
        for(String mainKeys: properties1.stringPropertyNames()){
            driver.put(mainKeys, properties1.get(mainKeys));
        }
        for(String mainKeys: properties2.stringPropertyNames()){
            username.put(mainKeys, properties2.get(mainKeys));
        }
        String usr = (String) username.get(k1);
        String customers = (String) customer.get(k1);
        String drivers = (String) driver.get(k1);
        // THIS MEANS A VALID USERNAME AND PASSWORD COMBINATION WAS ENTERED GOING TO MAKE TWO SCENES BASED OFF WHETHER OR NOT THIS PERSON IS AN ADMINISTATOR

        if ((username.containsKey(k1) &&  usr.equals(v1)) || (driver.containsKey(k1) &&  drivers.equals(v1)) || (customer.containsKey(k1) &&  customers.equals(v1)))
        {

            //Loading in our fxmls for each member (each user has different fxml)
            Parent managerList = FXMLLoader.load(getClass().getResource("/Style/fxmls/list.fxml"));
            //Parent shopperList = FXMLLoader.load(getClass().getResource("/Style/fxmls/list.fxml"));
            //Parent driverList = FXMLLoader.load(getClass().getResource("/Style/fxmls/list.fxml"));

            Scene manager = new Scene(managerList);
           // Scene shopper = new Scene(managerList);
           // Scene delivery = new Scene(managerList);

            //Obtaining stage information and setting our new scene/fxml
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if(username.containsKey(k1)) {
                currentStage.setScene(manager);
                currentStage.show();
            }
            else if(driver.containsKey(k1)) {
                //currentStage.setScene(delivery);
                //currentStage.show();
                System.out.println("THIS WILL BE THE DRIVER FXML");
            } else {
                //currentStage.setScene(shopper);
                //currentStage.show();
                System.out.println("THIS WILL BE THE CUSTOMER FXML");
            }


            Alert notice = new Alert(Alert.AlertType.CONFIRMATION, "Login Successful.");
            notice.showAndWait();
        }
        else {
            Alert notice = new Alert(Alert.AlertType.ERROR, "The Username or password is incorrect!");
            notice.showAndWait();
         }

    }
}
