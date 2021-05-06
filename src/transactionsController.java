import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class transactionsController extends managerController {

    @FXML
    private TableView<Table> fxTable;
    @FXML
    private TableColumn<Table, String> column_history;

    ObservableList<Table> oblist;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try
        {
            Connection con = MySQLConnection.getConnection();
            oblist = FXCollections.observableArrayList();
            ResultSet rs = con.createStatement().executeQuery("select * from Transactions");

            while (rs.next()) {
                oblist.add(new Table(rs.getString(1)));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        column_history.setCellValueFactory(new PropertyValueFactory<>("History"));

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
}
