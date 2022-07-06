package PageReplacement.Components;

import java.util.ArrayList;

public class Process implements Runnable, Comparable<Process>{
    protected int processID;
    protected double startTime;
    protected double finishTime;
    protected double arrivalTime;
    protected double burstTime;
    protected double actualBurstTime;
    protected double size;
    protected double remainingTime;
    protected int numberOfFaults = 0;
    protected int pageLocation;
    protected double waitTime;
    protected double turnaround;
    protected ArrayList<Integer> memoryTraces = new ArrayList<>();
    protected ArrayList<Page> pageTable = new ArrayList<Page>();

    public Process(int processID, double arrivalTime, double burstTime, double size, ArrayList<Integer> memoryTraces) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.size = size;
        this.remainingTime = burstTime;
        this.memoryTraces = memoryTraces;
        for (int i = 0; i < memoryTraces.size(); i++){
            pageTable.add(new Page(memoryTraces.get(i) >> 12));
        }
        this.actualBurstTime = 0;
    }

    public Process() {

    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(double burstTime) {
        this.burstTime = burstTime;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(double remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getNumberOfFaults() {
        return numberOfFaults;
    }

    public void setNumberOfFaults(int numberOfFaults) {
        this.numberOfFaults = numberOfFaults;
    }

    public int getPageLocation() {
        return pageLocation;
    }

    public void setPageLocation(int pageLocation) {
        this.pageLocation = pageLocation;
    }

    public double getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(double waitTime) {
        this.waitTime = waitTime;
    }

    public double getTurnaround() {
        return turnaround;
    }

    public void setTurnaround(double turnaround) {
        this.turnaround = turnaround;
    }

    public ArrayList<Integer> getMemoryTraces() {
        return memoryTraces;
    }

    public void setMemoryTraces(ArrayList<Integer> memoryTraces) {
        this.memoryTraces = memoryTraces;
    }

    public double getActualBurstTime() {
        return actualBurstTime;
    }

    public void setActualBurstTime(double actualBurstTime) {
        this.actualBurstTime = actualBurstTime;
    }

    public ArrayList<Page> getPageTable() {
        return pageTable;
    }

    public void setPageTable(ArrayList<Page> pageTable) {
        this.pageTable = pageTable;
    }

    @Override
    public int compareTo(Process o) {
        return Double.compare(this.getArrivalTime(), o.getArrivalTime());
    }

    @Override
    public void run() {
        while (getRemainingTime() > 0) Thread.onSpinWait();
    }
}
