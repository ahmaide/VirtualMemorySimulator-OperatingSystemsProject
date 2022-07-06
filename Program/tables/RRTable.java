package Program.tables;

import PageReplacement.Scheduler.RoundRobin;
import Program.Main;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class RRTable implements Initializable {
    @FXML
    private TableColumn<RoundRobin, Double> arrivalTime;

    @FXML
    private TableColumn<RoundRobin, Double> finishTime;

    @FXML
    private TableColumn<RoundRobin, Double> startTime;

    @FXML
    private TableColumn<RoundRobin, Double> turnaround;

    @FXML
    private TableView<RoundRobin> table;

    @FXML
    private TableColumn<RoundRobin, Double> waitTime;

    @FXML
    private JFXButton RRlogs;

    @FXML
    private TableColumn<RoundRobin, Integer> pid;

    @FXML
    private Label atAvg;

    @FXML
    private Label btAvg;

    @FXML
    private Label ftAvg;

    @FXML
    private Label stAvg;

    @FXML
    private Label taAvg;

    @FXML
    private Label wtAvg;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<RoundRobin> RRtable = FXCollections.observableArrayList(Main.option == 0 ? Main.FIFORR : Main.LRURR);
        pid.setCellValueFactory(new PropertyValueFactory<RoundRobin, Integer>("pid"));
        startTime.setCellValueFactory(new PropertyValueFactory<RoundRobin, Double>("startTime"));
        finishTime.setCellValueFactory(new PropertyValueFactory<RoundRobin, Double>("finishTime"));
        arrivalTime.setCellValueFactory(new PropertyValueFactory<RoundRobin, Double>("arrivalTime"));
        waitTime.setCellValueFactory(new PropertyValueFactory<RoundRobin, Double>("waitTime"));
        turnaround.setCellValueFactory(new PropertyValueFactory<RoundRobin, Double>("turnaround"));
        table.setItems(RRtable);

        atAvg.setText(String.valueOf(Main.option == 0 ? Main.FIFOat : Main.LRUat));
        wtAvg.setText(String.valueOf(Main.option == 0 ? Main.FIFOwt : Main.LRUwt));
        stAvg.setText(String.valueOf(Main.option == 0 ? Main.FIFOst : Main.LRUst));
        btAvg.setText(String.valueOf(Main.option == 0 ? Main.FIFObt : Main.LRUbt));
        taAvg.setText(String.valueOf(Main.option == 0 ? Main.FIFOta : Main.LRUta));
        ftAvg.setText(String.valueOf(Main.option == 0 ? Main.FIFOft : Main.LRUft));
    }
}
