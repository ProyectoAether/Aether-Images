package dao;

import java.util.ArrayList;
import java.util.List;

public class Contract {


    List<Constraint> constraintList;
    public Contract() {
        constraintList = new ArrayList<>();
    }
    public Contract(Constraint constraint) {
        super();
        constraintList.add(constraint);
    }

    public Contract(List<Constraint> constraintList) {
        this.constraintList = constraintList;
    }

    public List<Constraint> getConstraintList() {
        return constraintList;
    }

    public void setConstraintList(List<Constraint> constraintList) {
        this.constraintList = constraintList;
    }
    public void setConstraint(Constraint constraint){
        this.constraintList.add(constraint);
    }
}
