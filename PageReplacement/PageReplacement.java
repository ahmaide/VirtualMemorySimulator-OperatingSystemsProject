package PageReplacement;

import PageReplacement.Components.Frame;
import PageReplacement.Components.Memory;
import PageReplacement.Components.Process;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

public class PageReplacement implements Runnable {
    int numOfProcess;
    int numOfFrames;
    int minNumOfFrames;
    final static int cyclesForDisk = 300;
    final static int cyclesForContext = 5;
    final static int cyclesForMemoryAccess = 1;
    double timeQuantum = 5;
    Process runningProcess;

    ArrayList<Thread> processesThreads;
    ArrayList<Process> waitingQueue;
    ArrayList<Process> readyQueue = new ArrayList<Process>();
    ArrayList<Process> blockingQueue = new ArrayList<Process>();
    ArrayList<String> logs = new ArrayList<>();
    private Memory memory;

    int totalCycles = 0;
    int totalNumOfFaults = 0;
    double currentTime;
    double time;
    private HashSet<Frame> LRUcache;

    int option = 0;
    int cursor;

    public void setOption(int option) {
        this.option = option;
    }

    ReentrantLock lock = new ReentrantLock();

    public PageReplacement(ArrayList<Process> processes, double timeQuantum, int numOfProcess, int numOfFrames, int minNumOfFrames) {
        this.waitingQueue = processes;
        Collections.sort(this.waitingQueue, Comparator.comparing(Process::getArrivalTime));
        this.processesThreads = new ArrayList<>();
        for (Process process : this.waitingQueue) {
            Thread thread = new Thread(process);
            this.processesThreads.add(thread);
            thread.start();
        }
        this.timeQuantum = timeQuantum * 1000;
        this.numOfFrames = numOfFrames;
        this.numOfProcess = numOfProcess;
        this.minNumOfFrames = minNumOfFrames;
    }

    public void readFile(File file) throws FileNotFoundException {
        Scanner input = new Scanner(file);
        int pID, size, trace, tempPageNumber;
        double start, duration;
        boolean isProcessValid = true;
        this.numOfProcess = Integer.parseInt(input.nextLine());
        this.numOfFrames = Integer.parseInt(input.nextLine());
        this.minNumOfFrames = Integer.parseInt(input.nextLine());
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
            if (isProcessValid) {
                this.waitingQueue.add(new Process(pID, start, duration, size, memoryTraces));
            }
        }
    }

    Process findRecordByPID(ArrayList<Process> records, long PID) {
        for (Process record : records) {
            if (record.getProcessID() == PID) return record;
        }
        return null;
    }

    public Memory getMemory() {
        return memory;
    }


    public void RoundRobin() {
        this.memory = new Memory(numOfFrames);
        this.LRUcache = new HashSet<>();
        int pageLocation;
        cursor = 0;
        boolean status = true;
        for (Process process : this.waitingQueue) {
            process.setRemainingTime(process.getBurstTime());
            process.setStartTime(-1);
        }
        while (!isFinished()) {
            arrivingProcesses();
            if (!readyQueue.isEmpty() && (this.runningProcess == null || this.runningProcess != readyQueue.get(0))) {
                logs.add("Context switch");
                this.currentTime += cyclesForContext;
                this.totalCycles += cyclesForContext;
            }
            if (!this.readyQueue.isEmpty()) {
                this.runningProcess = this.readyQueue.get(0);
                logs.add("Process (" + runningProcess.getProcessID() + ") joined ready at " + this.currentTime);
                readyQueue.remove(0);
                pageLocation = runningProcess.getPageLocation();
                if (runningProcess.getStartTime() == -2) {
                    runningProcess.setStartTime(this.currentTime);
                }
                this.time = 0;
                logs.add("Process (" + runningProcess.getProcessID() + ") is under simulation");
                if (Double.compare(this.runningProcess.getRemainingTime(), this.timeQuantum) <= 0) {
                    simulate(this.runningProcess.getRemainingTime());
                } else {
                    simulate(this.timeQuantum);
                }
            } else {
                this.currentTime += 1;
            }
        }
    }

    public void simulate(double finish) {
        boolean status = true;
        int pageLocation = runningProcess.getPageLocation();
        while (Double.compare(time, finish) <= 0 && pageLocation < runningProcess.getPageTable().size()) {
            status = true;
            Frame frame = new Frame(runningProcess.getPageTable().get(pageLocation));
            if (memory.contains(frame)){
                logs.add("Frame (" + frame.getPage().getPageNumber() + ") accessed at " + this.currentTime);
                this.time += cyclesForMemoryAccess;
                this.currentTime += cyclesForMemoryAccess;
                this.totalCycles += cyclesForMemoryAccess;
            }else {
                logs.add("Frame (" + frame.getPage().getPageNumber() + ") not found in memory");
                if (option == 0) {
                    status = FirstInFirstOut(frame);
                } else if (option == 1) {
                    status = LeastRecentlyUsed(frame);
                }
                this.time += cyclesForDisk;
                this.totalNumOfFaults++;
                this.totalCycles += cyclesForDisk;
                this.currentTime += cyclesForDisk;
                runningProcess.setNumberOfFaults(runningProcess.getNumberOfFaults() + 1);
                frame.setBitReference(true);
                pageLocation++;
                status = false;
            }
            arrivingProcesses();
            if (!status) {
                logs.add("Context switch");
                break;
            }
        }
        runningProcess.setPageLocation(pageLocation);
        if (!status){
            runningProcess.setActualBurstTime(runningProcess.getActualBurstTime() + this.time);
            this.runningProcess.setRemainingTime(this.runningProcess.getRemainingTime() - this.time);
            readyQueue.add(runningProcess);
        }else{
            if (finish < this.timeQuantum){
                runningProcess.setActualBurstTime(runningProcess.getActualBurstTime() + this.timeQuantum - finish);
                runningProcess.setFinishTime(this.currentTime);
                this.runningProcess.setTurnaround(this.runningProcess.getFinishTime() - this.runningProcess.getArrivalTime());
                this.runningProcess.setWaitTime(this.runningProcess.getTurnaround() - this.runningProcess.getBurstTime());
                this.runningProcess.setRemainingTime(0);
                logs.add("Process (" + runningProcess.getProcessID() + ") finished at " + this.currentTime);
            }else{
                runningProcess.setActualBurstTime(runningProcess.getActualBurstTime() + this.timeQuantum);
                this.runningProcess.setRemainingTime(this.runningProcess.getRemainingTime() - this.timeQuantum);
                readyQueue.add(runningProcess);
                logs.add("Process (" + runningProcess.getProcessID() + ") spend time quantum at " + this.currentTime);
            }
        }
    }

    public void arrivingProcesses() {
        for (Process process : this.waitingQueue) {
            if (Double.compare(process.getArrivalTime(), this.currentTime) <= 0 && Double.compare(process.getStartTime(), -1) == 0) {
                this.readyQueue.add(process);
                logs.add("Process (" + process.getProcessID() + ") joined ready queue at " + this.currentTime);
                process.setStartTime(-2);
            }
        }
    }

    public Boolean isFinished() {
        for (Process p : this.waitingQueue) {
            if (p.getRemainingTime() != 0) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> getLogs() {
        return logs;
    }

    public boolean FirstInFirstOut(Frame frame) {
        if (!this.memory.contains(frame)) {
            blockingQueue.add(runningProcess);
            logs.add("Process (" + runningProcess.getProcessID() + ") joined blocked queue at " + this.currentTime);
            if (this.memory.size() == this.memory.getMemorySize()) {
                this.memory.removeFirst();
                this.memory.add(frame);
            } else {
                this.memory.add(frame);
            }
            logs.add("Frame (" + frame.getPage().getPageNumber() + ") read from disk at " + this.currentTime + 300);
        }
        this.time += cyclesForMemoryAccess;
        this.currentTime += cyclesForMemoryAccess;
        this.totalCycles += cyclesForMemoryAccess;
        return true;
    }

    public boolean LeastRecentlyUsed(Frame frame) {
        if (!this.memory.contains(frame)) {
            blockingQueue.add(runningProcess);
            logs.add("Process (" + runningProcess.getProcessID() + ") joined blocked queue at " + this.currentTime);
        }
        if (!this.LRUcache.contains(frame)) {
            if (this.memory.size() == this.memory.getMemorySize()) {
                Frame last = this.memory.removeLast();
                LRUcache.remove(last);
            }
        } else {
            this.memory.remove(frame);
        }
        this.memory.push(frame);
        LRUcache.add(frame);
        this.time += cyclesForMemoryAccess;
        this.currentTime += cyclesForMemoryAccess;
        this.totalCycles += cyclesForMemoryAccess;
        return true;
    }

    @Override
    public void run() {

    }
}