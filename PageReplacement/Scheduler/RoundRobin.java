package PageReplacement.Scheduler;

import PageReplacement.Components.Process;

import java.util.ArrayList;

public class RoundRobin {
    protected int pid;
    protected double startTime;
    protected double finishTime;
    protected double arrivalTime;
    protected double actualBurstTime;
    protected double waitTime;
    protected double turnaround;
    public RoundRobin(Process process){
        this.pid = process.getProcessID();
        this.startTime = process.getStartTime();
        this.finishTime = process.getFinishTime();
        this.arrivalTime = process.getArrivalTime();
        this.actualBurstTime = process.getBurstTime();
        this.waitTime = process.getWaitTime();
        this.turnaround = process.getTurnaround();
    }

    public double getPid() {
        return pid;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getFinishTime() {
        return finishTime;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public double getActualBurstTime() {
        return actualBurstTime;
    }

    public double getWaitTime() {
        return waitTime;
    }

    public double getTurnaround() {
        return turnaround;
    }
}
