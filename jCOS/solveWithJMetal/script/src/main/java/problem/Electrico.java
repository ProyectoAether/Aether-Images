package problem;

import dao.Cliente;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import utils.semantic.JSONUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Electrico extends AbstractDoubleProblem {

    private Cliente cliente;
    private double[] PRECIO;
    private int N_CONSUMOS;
    private int N_PERIODOS;
   // private OWLUtils owlUtils;
    private JSONUtils jsonUtils;

    public Electrico(Cliente cliente, double[] precio) {

        // TODO: assert consumos not null, assert consumos.length != 0, assert lenth todas las columnas de consumos = 3

        this.cliente = cliente;
        this.PRECIO = precio;
        this.N_CONSUMOS = cliente.getConsumos().length;
        this.N_PERIODOS = cliente.getConsumos()[0].length;
       // setNumberOfVariables(3); // revisar!
       // setNumberOfObjectives(1);

        List<Double> lowerLimit = Arrays.asList(0.0, 0.0, 0.0);
        List<Double> upperLimit = Arrays.asList(250.0, 250.0, 250.0);

        variableBounds(lowerLimit, upperLimit);
       // owlUtils = new OWLUtils();
        jsonUtils = new JSONUtils();
    }

    public Electrico() {
        super();
    }

    @Override
    public int numberOfVariables() {
        return 3;
    }

    @Override
    public int numberOfObjectives() {
        return 1;
    }

    @Override
    public int numberOfConstraints() {
        return 0;
    }

    @Override
    public void numberOfObjectives(int numberOfObjectives) {
        super.numberOfObjectives(numberOfObjectives);
    }

    @Override
    public void numberOfConstraints(int numberOfConstraints) {
        super.numberOfConstraints(numberOfConstraints);
    }

    @Override
    public String name() {
        return "Electrico";
    }

    @Override
    public void name(String name) {
        super.name(name);
    }

    @Override
    public void variableBounds(List<Double> lowerBounds, List<Double> upperBounds) {
        super.variableBounds(lowerBounds, upperBounds);
    }

    @Override
    public DoubleSolution createSolution() {
        return super.createSolution();
    }

    @Override
    public List<Bounds<Double>> variableBounds() {
        return super.variableBounds();
    }


    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public DoubleSolution evaluate(DoubleSolution doubleSolution) {

        List<Double> potenciaContratada = doubleSolution.variables().subList(0, 3);
        //double TPTotalVar = doubleSolution.variables().get(3);

        double TPTotal = calculaTp(potenciaContratada);

        //double constraint = (TPTotal);

        // doubleSolution.constraints()[0] = constraint;

        // System.out.println("Potencias: P1 -> "+potenciaContratada.get(0) +
        //         " P2 -> "+potenciaContratada.get(1)+
        //         " P3 -> "+potenciaContratada.get(2) +
        //         " **** Objetivo: " + TPTotal +
        //         " **** TP: " + TPTotal //+
        //         //" **** Constraint: " + constraint
        // );

        doubleSolution.objectives()[0] = TPTotal; // por defecto minimiza

        return doubleSolution;
    }

    public double calculaTp(List<Double> potenciaContratada) {
        double[][] potenciaFactura = new double[N_CONSUMOS][N_PERIODOS];
        double[] TP = new double[N_CONSUMOS];

        double TPTotal = 0.0;

        for(int i = 0; i < N_CONSUMOS; i++) {
            double sumTerminoPotencia = 0.0;
            for(int j = 0; j< N_PERIODOS; j++) {                
                double pm = this.cliente.getConsumos()[i][j];

                List<Function<Double,Double>> functions =jsonUtils.getConstrainsConsumptions(pm,potenciaFactura[i][j],cliente);
                // owlUtils.getConstrainsConsumptions(pm,potenciaFactura[i][j]);
                //jsonUtils.getConstrainsConsumptions(pm,potenciaFactura[i][j],cliente);
                for (Function<Double,Double> function: functions) {
                    potenciaFactura[i][j] = function.apply(potenciaContratada.get(j));
                }

               /* if(pm <= 0.85 * potenciaContratada.get(j)) { // TODO encapsular cada caso en funcion
                    potenciaFactura[i][j] = potenciaContratada.get(j) * 0.85;
                } else if(pm > potenciaContratada.get(j) * 0.85 && pm < potenciaContratada.get(j) * 1.05) {
                    potenciaFactura[i][j] = pm;
                } else {
                    potenciaFactura[i][j] = potenciaContratada.get(j) + (2 * (pm - potenciaContratada.get(j)));
                }*/

                sumTerminoPotencia += potenciaFactura[i][j] * PRECIO[j];
            }

            TP[i] = sumTerminoPotencia;
            TPTotal += sumTerminoPotencia;
        }

        return TPTotal;
    }

    @Override
    public String toString() {
        return "Electrico [cliente=" + cliente + ", PRECIO=" + Arrays.toString(PRECIO) + ", N_CONSUMOS=" + N_CONSUMOS
                + ", N_PERIODOS=" + N_PERIODOS + ", jsonUtils=" + jsonUtils + "]";
    }

    
}
