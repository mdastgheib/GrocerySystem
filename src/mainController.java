import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

    public void logIn(ActionEvent event) throws IOException {
        String userText = username.getText();
        String passText = password.getText();
        this.user = userText;

        if(userText == null || userText.length() == 0 || passText == null || passText.length() == 0)
        {
            Alert notice = new Alert(Alert.AlertType.ERROR, " Please Make Sure You entered a Username and Password. ");
            notice.showAndWait();
            return;
        }
        // declaring my HashMap
        HashMap<Object, Object> customer = new HashMap<>();
        File customerFile = new File("customer.properties");
        FileInputStream reader = new FileInputStream(customerFile);
        Properties properties = new Properties();
        properties.load(reader);
        reader.close();

        HashMap<Object, Object> manager = new HashMap<>();
        File managerFile = new File("manager.properties");
        FileInputStream reader2 = new FileInputStream(managerFile);
        Properties properties2 = new Properties();
        properties2.load(reader2);
        reader.close();

        // filling the HashMap with the property file is empty nothing will be in it
        // but the following if statement will fill it if anything is passed
        for(String mainKeys: properties.stringPropertyNames())
        {
            customer.put(mainKeys, properties.get(mainKeys));
        }
        for(String mainKeys: properties2.stringPropertyNames())
        {
            manager.put(mainKeys, properties2.get(mainKeys));
        }

        String usr = (String) manager.get(userText);
        String customers = (String) customer.get(userText);
        // THIS MEANS A VALID USERNAME AND PASSWORD COMBINATION WAS ENTERED GOING TO MAKE TWO SCENES BASED OFF WHETHER OR NOT THIS PERSON IS AN ADMINISTATOR

        if ((manager.containsKey(userText) &&  usr.equals(passText)) || (customer.containsKey(userText) &&  customers.equals(passText)))
        {

            //Loading in our fxmls for each member (each user has different fxml)
            Parent managerList = FXMLLoader.load(getClass().getResource("/Style/fxmls/manager.fxml"));
            Parent shopperList = FXMLLoader.load(getClass().getResource("/Style/fxmls/shopper.fxml"));

            Scene managerS = new Scene(managerList);
            Scene shopperS = new Scene(shopperList);

            //Obtaining stage information and setting our new scene/fxml
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            //Checking input to determine what type of user, then opening that fxml scene
            if(manager.containsKey(userText))
            {
                currentStage.setScene(managerS);
                currentStage.show();
            }
            else
            {
                currentStage.setScene(shopperS);
                currentStage.show();
            }

            //Alert notice = new Alert(Alert.AlertType.CONFIRMATION, "Login Successful.");
            //notice.showAndWait();

        } //End if-statement

        else
        {
            Alert notice = new Alert(Alert.AlertType.ERROR, "The Username or password is incorrect!");
            notice.showAndWait();
        }

    }
}
