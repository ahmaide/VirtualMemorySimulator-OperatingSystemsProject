package PageReplacement.Components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

public class Memory{
    private Deque<Frame> memory;
    protected ArrayList<Page> pageTable;
    private final int size;

    public Memory(int size){
        this.memory = new LinkedList<>();
        this.pageTable = new ArrayList<Page>();
        this.size = size;
    }

    public synchronized void  add(Frame page){
        this.memory.add(page);
    }

    public synchronized void push(Frame frame){
        this.memory.push(frame);
    }

    public synchronized void remove(Frame frame){
        this.memory.remove(frame);
    }

    public synchronized Frame removeFirst(){
        return this.memory.removeFirst();
    }
    public synchronized int getMemorySize(){
        return this.size;
    }
    public synchronized Frame removeLast(){
        return this.memory.removeFirst();
    }
    public synchronized int size(){
        return this.memory.size();
    }

    public synchronized boolean contains(Frame page){
        for (Frame frame : this.memory){
            if (frame.getPage().getPageNumber() == page.getPage().getPageNumber()){
                return true;
            }
        }
        return false;
    }
}
