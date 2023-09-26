package dao;

import java.util.ArrayList;
import java.util.List;

public class KnapsackConstraint {


    List<ItemKnapSack> inList;
    List<ItemKnapSack> offList;
    int capacity;
    public KnapsackConstraint(){
        this.inList = new ArrayList<>();
        this.offList = new ArrayList<>();
    }

    public List<ItemKnapSack> getInList() {
        return inList;
    }

    public void setInList(List<ItemKnapSack> inList) {
        this.inList = inList;
    }
    public void setInList(ItemKnapSack in) {
        this.inList.add(in);
    }


    public List<ItemKnapSack> getOffList() {
        return offList;
    }

    public void setOffList(List<ItemKnapSack> offList) {
        this.offList = offList;
    }
    public void setOffList(ItemKnapSack off) {
        this.offList.add(off);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
