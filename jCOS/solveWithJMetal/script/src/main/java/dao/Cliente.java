package dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cliente {

    public enum Company{Conquense,Endesa}
    private double[][] consumos;
    private double[] potenciaContratada;
    private String cups;

    private Company company;

    private List<Contract> contractList;
    public Cliente(){
        contractList = new ArrayList<>();
    }
    public Cliente(double[][] consumos, double[] potenciaContratada, String cups,Company company) {
        this.consumos = consumos;
        this.potenciaContratada = potenciaContratada;
        this.cups = cups;
        this.company = company;
        contractList = new ArrayList<>();

    }

    public double[][] getConsumos() {
        return consumos;
    }

    public void setConsumos(double[][] consumos) {
        this.consumos = consumos;
    }

    public double[] getPotenciaContratada() {
        return potenciaContratada;
    }

    public void setPotenciaContratada(double[] potenciaContratada) {
        this.potenciaContratada = potenciaContratada;
    }

    public String getCups() {
        return cups;
    }

    public void setCups(String cups) {
        this.cups = cups;
    }

    public List<Contract> getContractList() {
        return contractList;
    }

    public void setContractList(List<Contract> contractList) {
        this.contractList = contractList;
    }

    public void setContract(Contract contract){
        this.contractList.add(contract);
    }
    @Override
    public String toString() {
        return "Cliente [consumos=" + Arrays.deepToString(consumos) + ", potenciaContratada="
                + Arrays.toString(potenciaContratada) + ", cups=" + cups + ", company=" + company + ", contractList="
                + contractList + "]";
    }

    
}
