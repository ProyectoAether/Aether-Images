package dao;

public class Constraint {
    public enum Tipo {low, high, between, equal,undefined};
    private double smaller;
    private double greater;

    private Tipo constraintType;

    public Constraint(){

    }

    public double getSmaller() {
        return smaller;
    }

    public void setSmaller(double smaller) {
        this.smaller = smaller;
    }

    public double getGreater() {
        return greater;
    }

    public void setGreater(double greater) {
        this.greater = greater;
    }

    public Tipo getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(Tipo constraintType) {
        this.constraintType = constraintType;
    }
    public void setConstraintType(String constraintTipo) {
        if(constraintTipo!=null) {
            if (constraintTipo.equalsIgnoreCase(Tipo.low.name())) {
                this.constraintType = Tipo.low;
            } else if (constraintTipo.equalsIgnoreCase(Tipo.high.name())) {
                this.constraintType = Tipo.high;
            } else if (constraintTipo.equalsIgnoreCase(Tipo.equal.name())) {
                this.constraintType = Tipo.equal;
            }else if (constraintTipo.equalsIgnoreCase(Tipo.between.name())) {
                this.constraintType = Tipo.between;
            }else {
                this.constraintType = Tipo.undefined;
            }

        }else{
            this.constraintType = Tipo.undefined;
        }
    }
}
