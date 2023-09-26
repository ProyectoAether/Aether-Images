package dao;

import java.util.Arrays;

public class Recommendation {

    private Cliente cliente;
    private double[] potenciaRecomendada;
    private double tpActual;
    private double tpRecomendado;

    public Recommendation(Cliente cliente, double[] potenciaRecomendada, double tpActual, double tpRecomendado) {
        this.cliente = cliente;
        this.potenciaRecomendada = potenciaRecomendada;
        this.tpActual = tpActual;
        this.tpRecomendado = tpRecomendado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public double[] getPotenciaRecomendada() {
        return potenciaRecomendada;
    }

    public void setPotenciaRecomendada(double[] potenciaRecomendada) {
        this.potenciaRecomendada = potenciaRecomendada;
    }

    public double getTpActual() {
        return tpActual;
    }

    public void setTpActual(double tpActual) {
        this.tpActual = tpActual;
    }

    public double getTpRecomendado() {
        return tpRecomendado;
    }

    public void setTpRecomendado(double tpRecomendado) {
        this.tpRecomendado = tpRecomendado;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "cliente=" + cliente +
                ", potenciaRecomendada=" + Arrays.toString(potenciaRecomendada) +
                ", tpActual=" + tpActual +
                ", tpRecomendado=" + tpRecomendado +
                '}';
    }

}
