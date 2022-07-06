package Program;

import PageReplacement.Components.Frame;
import PageReplacement.Components.Memory;
import PageReplacement.Components.Page;
import PageReplacement.Components.Process;
import PageReplacement.PageReplacement;
import PageReplacement.Policy.FirstInFirstOut;
import PageReplacement.Policy.LeastRecentlyUsed;
import PageReplacement.Scheduler.RoundRobin;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main extends Application {
    private static Stage stage;
    private static Scene fileLoadingScene;
    private static Stage primaryStage;
    private static AnchorPane mainScreen;

    @FXML
    private JFXTextArea fileName = new JFXTextArea();

    @FXML
    private JFXTextArea tQ;

    @FXML
    private JFXTextArea memorySize;

    @FXML
    private JFXTextArea minFrames;

    @FXML
    private JFXTextArea numOfProcesses;

    static File file;

    static int numOfProcess;
    static int numOfFrames;
    static int minNumOfFrames;
    static double timeQuantum;
    static boolean runFlag = false;
    public static int FIFOtotalCycles = 0;
    public static int FIFOtotalFaults = 0;
    public static int LRUtotalCycles = 0;
    public static int LRUtotalFaults = 0;
    public static int option = 0;

    static ArrayList<Process> processesFIFO = new ArrayList<>();
    static ArrayList<Process> processesLRU = new ArrayList<>();

    public static ArrayList<String> logsLRU = new ArrayList<>();
    public static ArrayList<String> logsFIFO = new ArrayList<>();

    public static double FIFOst = 0, FIFOft = 0, FIFOat = 0, FIFObt = 0, FIFOta = 0, FIFOwt = 0;
    public static double LRUst = 0, LRUft = 0, LRUat = 0, LRUbt = 0, LRUta = 0, LRUwt = 0;
    public static double tFIFOhit = 0, tFIFOmiss = 0, tLRUhit = 0, tLRUmiss = 0;
    public static int FIFOhit = 0, FIFOmiss = 0, LRUhit = 0, LRUmiss = 0;

    public static ArrayList<RoundRobin> FIFORR = new ArrayList<>();
    public static ArrayList<RoundRobin> LRURR = new ArrayList<>();
    public static ArrayList<FirstInFirstOut> FIFO = new ArrayList<>();
    public static ArrayList<LeastRecentlyUsed> LRU = new ArrayList<>();
    public static ArrayList<Page> FIFOpages = new ArrayList<>();
    public static ArrayList<Page> LRUpages = new ArrayList<>();
    public static ArrayList<Frame> FIFOmemory = new ArrayList<>();
    public static ArrayList<Frame> LRUmemory = new ArrayList<>();
    static Memory tempMem2;
    static Memory tempMem1;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Main.primaryStage = primaryStage;
        AnchorPane fileLoadingScreen = FXMLLoader.load(getClass().getResource("screens/Start.fxml"));
        AnchorPane mainScreen = FXMLLoader.load(getClass().getResource("screens/RR_PR.fxml"));
        Main.mainScreen = mainScreen;
        fileLoadingScene = new Scene(fileLoadingScreen);
        Scene mainScene = new Scene(mainScreen);


        Main.primaryStage.setScene(fileLoadingScene);
        Main.primaryStage.show();
    }
    @FXML
    void browse(MouseEvent event) {
        File[] file = new File[1];
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        file[0] = fileChooser.showOpenDialog(primaryStage);
        if (file[0] != null)
            fileName.setText(file[0].getAbsolutePath());
    }

    @FXML
    void exit(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    void generate(MouseEvent event) throws IOException {
            JFXButton button = (JFXButton) event.getSource();
            Scene scene = button.getScene();
            AnchorPane mainScreen = FXMLLoader.load(getClass().getResource("screens/Generate.fxml"));
            scene.setRoot(mainScreen);
    }

    @FXML
    void loadFile(MouseEvent event) throws IOException {
        if (!fileName.getText().isEmpty()) {
            JFXButton button = (JFXButton) event.getSource();
            Scene scene = button.getScene();
            AnchorPane mainScreen = FXMLLoader.load(getClass().getResource("screens/RR_PR.fxml"));
            scene.setRoot(mainScreen);
            file = new File(fileName.getText());
            readFile(file);
        }else{
            fileName.setUnFocusColor(Paint.valueOf("RED"));
        }
    }

    @FXML
    void generateData(MouseEvent event) throws IOException {
        if (!numOfProcesses.getText().isEmpty() && !memorySize.getText().isEmpty() && !minFrames.getText().isEmpty()) {
            numOfProcess = Integer.parseInt(numOfProcesses.getText());
            numOfFrames = Integer.parseInt(memorySize.getText());
            minNumOfFrames = Integer.parseInt(minFrames.getText());
            Random r = new Random();

            for(int i = 0; i < numOfProcess; i++) {
                int numOfTraces = r.nextInt(100) + minNumOfFrames;
                int size = r.nextInt(200) + 1;
                ArrayList<Integer> memoryTraces = new ArrayList<>();
                memoryTraces = IntStream.of(r.ints(numOfTraces, 0, size << 12).toArray()).boxed().collect(Collectors.toCollection(ArrayList::new));
                int duration = 1000 * r.nextInt(10);
                int start = 1000 * r.nextInt(numOfProcess);
                processesFIFO.add(new Process(i, start, duration, size, memoryTraces));
                processesLRU.add(new Process(i, start, duration, size, memoryTraces));
            }
            JFXButton button = (JFXButton) event.getSource();
            Scene scene = button.getScene();
            AnchorPane mainScreen = FXMLLoader.load(getClass().getResource("screens/RR_PR.fxml"));
            scene.setRoot(mainScreen);

        }else{
            numOfProcesses.setUnFocusColor(Paint.valueOf("RED"));
            memorySize.setUnFocusColor(Paint.valueOf("RED"));
            minFrames.setUnFocusColor(Paint.valueOf("RED"));
        }
    }

    @FXML
    void FIFO(MouseEvent event) throws IOException {
        if (runFlag) {
            Stage stage = new Stage();
            AnchorPane fileLoadingScreen = FXMLLoader.load(getClass().getResource("screens/FIFO.fxml"));
            stage.setScene(new Scene(fileLoadingScreen));
            stage.show();
            option = 0;
        }
    }

    @FXML
    void LRU(MouseEvent event) throws IOException {
        if (runFlag) {
            Stage stage = new Stage();
            AnchorPane fileLoadingScreen = FXMLLoader.load(getClass().getResource("screens/LRU.fxml"));
            stage.setScene(new Scene(fileLoadingScreen));
            stage.show();
            option = 1;
        }
    }

    @FXML
    void autoTQ(ActionEvent event) {
        tQ.setText("5");
        timeQuantum = Double.parseDouble(tQ.getText());
    }

    @FXML
    void RR(MouseEvent event) throws IOException {
        if (runFlag) {
            Stage stage = new Stage();
            AnchorPane fileLoadingScreen = FXMLLoader.load(getClass().getResource("screens/RR.fxml"));


            stage.setScene(new Scene(fileLoadingScreen));
            stage.show();
        }
    }

    @FXML
    void run(MouseEvent event) {
        if (!tQ.getText().isEmpty()) {
            if (!runFlag) {
                PageReplacement fifoPR = new PageReplacement(processesFIFO, timeQuantum, numOfProcess, numOfFrames, minNumOfFrames);
                fifoPR.RoundRobin();

                PageReplacement lruPR = new PageReplacement(processesLRU, timeQuantum, numOfProcess, numOfFrames, minNumOfFrames);
                lruPR.setOption(1);
                lruPR.RoundRobin();
                logsLRU = lruPR.getLogs();
                logsFIFO = fifoPR.getLogs();
                tempMem1 = fifoPR.getMemory();
                runFlag = true;
                for (Process p : processesFIFO){
                    FIFORR.add(new RoundRobin(p));
                    FIFO.add(new FirstInFirstOut(p));
                    FIFOpages.addAll(p.getPageTable());
                    FIFOst += p.getStartTime();
                    FIFOft += p.getFinishTime();
                    FIFOat += p.getArrivalTime();
                    FIFObt += p.getBurstTime();
                    FIFOta += p.getTurnaround();
                    FIFOwt += p.getWaitTime();
                    FIFOtotalCycles += p.getActualBurstTime();
                    FIFOtotalFaults += p.getNumberOfFaults();
                    tFIFOmiss += (p.getNumberOfFaults()/(double)p.getPageTable().size());
                    tFIFOhit += ((p.getPageTable().size() - p.getNumberOfFaults())/(double)p.getPageTable().size());
                }
                tFIFOmiss /= processesFIFO.size();
                tFIFOhit /= processesFIFO.size();
                FIFOmiss = (int)(tFIFOmiss * 100);
                FIFOhit = (int)(tFIFOhit * 100);
                int memSize = tempMem1.size();
                for (int i = 0; i < memSize; i++){
                    FIFOmemory.add(tempMem1.removeFirst());
                }
                FIFOst /= processesFIFO.size();
                FIFOft /= processesFIFO.size();
                FIFOat /= processesFIFO.size();
                FIFObt /= processesFIFO.size();
                FIFOta /= processesFIFO.size();
                FIFOwt /= processesFIFO.size();

                for (Process p : processesLRU){
                    LRURR.add(new RoundRobin(p));
                    LRU.add(new LeastRecentlyUsed(p));
                    LRUpages.addAll(p.getPageTable());
                    LRUst += p.getStartTime();
                    LRUft += p.getFinishTime();
                    LRUat += p.getArrivalTime();
                    LRUbt += p.getBurstTime();
                    LRUta += p.getTurnaround();
                    LRUwt += p.getWaitTime();
                    LRUtotalCycles += p.getActualBurstTime();
                    LRUtotalFaults += p.getNumberOfFaults();
                    tLRUmiss += (p.getNumberOfFaults()/(double)p.getPageTable().size());
                    tLRUhit += ((p.getPageTable().size() - p.getNumberOfFaults())/(double)p.getPageTable().size());
                }
                tLRUhit /= processesLRU.size();
                tLRUmiss /= processesLRU.size();
                LRUmiss = (int)(tLRUmiss * 100);
                LRUhit = (int)(tLRUhit * 100);
                LRUst /= processesLRU.size();
                LRUft /= processesLRU.size();
                LRUat /= processesLRU.size();
                LRUbt /= processesLRU.size();
                LRUta /= processesLRU.size();
                LRUwt /= processesLRU.size();
                tempMem2 = lruPR.getMemory();
                memSize = tempMem2.size();
                for (int i = 0; i < memSize; i++){

                    LRUmemory.add(tempMem2.removeFirst());
                }
            }
        }else{
            tQ.setUnFocusColor(Paint.valueOf("RED"));
        }
    }





    public void readFile(File file) throws FileNotFoundException {
        Scanner input = new Scanner(file);
        int pID, size, trace, tempPageNumber;
        double start, duration;
        boolean isProcessValid = true;
        numOfProcess = Integer.parseInt(input.nextLine());
        numOfFrames = Integer.parseInt(input.nextLine());
        minNumOfFrames = Integer.parseInt(input.nextLine());
        String line;
        while (input.hasNext()) {
            ArrayList<Integer> memoryTraces = new ArrayList<>();
            isProcessValid = true;
            line = input.nextLine();
            String[] tokens = line.split(" ");
            pID = Integer.parseInt(tokens[0]);
            start = 1000 * Double.parseDouble(tokens[1]);
            duration = 1000 * Double.parseDouble(tokens[2]);
            size = Integer.parseInt(tokens[3]);
            for (int i = 4; i < tokens.length; i++) {
                trace = Integer.parseInt(tokens[i], 16);
                tempPageNumber = trace >> 12;
                if (tempPageNumber > size) {
                    isProcessValid = false;
                    break;
                }
                memoryTraces.add(trace);
            }
            if (memoryTraces.size() < minNumOfFrames){
                isProcessValid = false;
            }
            if (isProcessValid) {
                processesFIFO.add(new Process(pID, start, duration, size, memoryTraces));
                processesLRU.add(new Process(pID, start, duration, size, memoryTraces));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
