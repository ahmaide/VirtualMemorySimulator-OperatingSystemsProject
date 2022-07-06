package Program.tables;

        import PageReplacement.Components.Frame;
        import PageReplacement.Components.Page;
        import PageReplacement.Policy.LeastRecentlyUsed;
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

public class LRU implements Initializable {
    @FXML
    private TableColumn<LeastRecentlyUsed, Integer> numberOfFaults;

    @FXML
    private TableColumn<LeastRecentlyUsed, Integer> processID;

    @FXML
    private TableView<Page> PT;

    @FXML
    private TableColumn<Page, Integer> pageNumber;

    @FXML
    private TableView<LeastRecentlyUsed> processes;

    @FXML
    private Label TC;

    @FXML
    private Label TF;

    @FXML
    private TableColumn<Frame, Boolean> bitReference;

    @FXML
    private TableColumn<Frame, Page> page;

    @FXML
    private TableView<Frame> Memory;

    @FXML
    private Label hit;

    @FXML
    private Label miss;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<LeastRecentlyUsed> processesTable = FXCollections.observableArrayList(Main.LRU);
        numberOfFaults.setCellValueFactory(new PropertyValueFactory<LeastRecentlyUsed, Integer>("numberOfFaults"));
        processID.setCellValueFactory(new PropertyValueFactory<LeastRecentlyUsed, Integer>("processID"));

        ObservableList<Page> pt = FXCollections.observableArrayList(Main.LRUpages);
        pageNumber.setCellValueFactory(new PropertyValueFactory<Page, Integer>("pageNumber"));

        ObservableList<Frame> memory = FXCollections.observableArrayList(Main.LRUmemory);
        bitReference.setCellValueFactory(new PropertyValueFactory<Frame, Boolean>("bitReference"));
        page.setCellValueFactory(new PropertyValueFactory<Frame, Page>("page"));


        processes.setItems(processesTable);
        PT.setItems(pt);
        Memory.setItems(memory);

        TC.setText(String.valueOf(Main.LRUtotalCycles));
        TF.setText(String.valueOf(Main.LRUtotalFaults));

        hit.setText(Main.LRUhit + "%");
        miss.setText(Main.LRUmiss + "%");

    }

    @FXML
    void LRUlog(MouseEvent event) throws IOException {
        Writer fileWriter = new FileWriter("lrulogs", false);
        for (String str : Main.logsLRU){
            fileWriter.write(str + "\n");
        }
        fileWriter.close();
        File f = new File("lrulogs");
        Desktop.getDesktop().open(f);
    }
}
