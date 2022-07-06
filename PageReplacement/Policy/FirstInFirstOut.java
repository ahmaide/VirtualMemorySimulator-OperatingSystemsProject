package PageReplacement.Policy;

import PageReplacement.Components.Process;

public class FirstInFirstOut {
    protected int processID;
    protected int numberOfFaults;

    public FirstInFirstOut(Process process){
        this.processID = process.getProcessID();
        this.numberOfFaults = process.getNumberOfFaults();
    }

    public int getProcessID() {
        return processID;
    }

    public int getNumberOfFaults() {
        return numberOfFaults;
    }
}
