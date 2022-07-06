package Program.tables;

import PageReplacement.Components.Frame;
import PageReplacement.Components.Page;
import PageReplacement.Policy.FirstInFirstOut;
import Program.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ResourceBundle;

public class FIFO implements Initializable {
    @FXML
    private TableColumn<FirstInFirstOut, Integer> numberOfFaults;

    @FXML
    private TableColumn<FirstInFirstOut, Integer> processID;

    @FXML
    private TableView<Page> fifoPT;

    @FXML
    private TableColumn<Page, Integer> pageNumber;

    @FXML
    private TableView<FirstInFirstOut> fifoProcesses;

    @FXML
    private Label fifoTC;

    @FXML
    private Label fifoTF;

    @FXML
    private TableColumn<Frame, Boolean> bitReference;

    @FXML
    private TableColumn<Frame, Page> page;

    @FXML
    private TableView<Frame> fifoMemory;

    @FXML
    private Label hit;

    @FXML
    private Label miss;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<FirstInFirstOut> processesTable = FXCollections.observableArrayList(Main.FIFO);
        numberOfFaults.setCellValueFactory(new PropertyValueFactory<FirstInFirstOut, Integer>("numberOfFaults"));
        processID.setCellValueFactory(new PropertyValueFactory<FirstInFirstOut, Integer>("processID"));

        ObservableList<Page> pt = FXCollections.observableArrayList(Main.FIFOpages);
        pageNumber.setCellValueFactory(new PropertyValueFactory<Page, Integer>("pageNumber"));

        ObservableList<Frame> memory = FXCollections.observableArrayList(Main.FIFOmemory);
        bitReference.setCellValueFactory(new PropertyValueFactory<Frame, Boolean>("bitReference"));
        page.setCellValueFactory(new PropertyValueFactory<Frame, Page>("page"));


        fifoProcesses.setItems(processesTable);
        fifoPT.setItems(pt);
        fifoMemory.setItems(memory);

        fifoTC.setText(String.valueOf(Main.FIFOtotalCycles));
        fifoTF.setText(String.valueOf(Main.FIFOtotalFaults));

        hit.setText(Main.FIFOhit + "%");
        miss.setText(Main.FIFOmiss + "%");

    }
    @FXML
    void FIFOlog(MouseEvent event) throws IOException {
        Writer fileWriter = new FileWriter("fifolog", false);
        for (String str : Main.logsFIFO){
            fileWriter.write(str + "\n");
        }
        fileWriter.close();
        File f = new File("fifolog");
        Desktop.getDesktop().open(f);
    }
}
