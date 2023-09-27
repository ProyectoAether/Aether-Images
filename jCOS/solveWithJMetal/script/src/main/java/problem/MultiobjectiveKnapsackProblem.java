

package problem;

import dao.KnapsackConstraint;
import org.json.simple.parser.ParseException;
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import utils.semantic.JSONUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;

public class MultiobjectiveKnapsackProblem extends AbstractIntegerProblem {

    // Atributos del problema
    private List<Integer> profits;
    private List<Integer> weights;
    private int capacity;
    private int numberOfItems;

    //private OWLUtils owlUtils;
    private JSONUtils jsonUtils;
    private List<Function<IntegerSolution, Integer>> constraints;
    public MultiobjectiveKnapsackProblem(List<Integer>  profits, List<Integer>  weights, int capacity, String constrainsPath) throws IOException, ParseException {
        this.profits = profits;
        this.weights = weights;
        this.capacity = capacity;
        this.numberOfItems = profits.size();
        numberOfObjectives(2);
        variableBounds(getLowerBounds(),getUpperBounds());
        name("MultiobjectiveKnapsack");
       // owlUtils = new OWLUtils("src/main/resources/ontology/knapsack-problem.owl");
        jsonUtils = new JSONUtils();
      //  constraints = owlUtils.getConstrainsKnapsack();
        KnapsackConstraint knapsackConstraint= jsonUtils.parseJSON(constrainsPath);
        constraints = jsonUtils.getConstrainsKnapsack(knapsackConstraint);
        if(constraints!=null && !constraints.isEmpty()) {
            numberOfConstraints(1);
        }
    }
    public MultiobjectiveKnapsackProblem(String path, String constrainsPath) throws IOException, ParseException {
        this.readData(path);
        numberOfObjectives(2);

        variableBounds(getLowerBounds(),getUpperBounds());
        name("MultiobjectiveKnapsack");
        //owlUtils = new OWLUtils("src/main/resources/ontology/knapsack-problem.owl");
        //constraints = owlUtils.getConstrainsKnapsack();
        jsonUtils = new JSONUtils();
        //  constraints = owlUtils.getConstrainsKnapsack();
        KnapsackConstraint knapsackConstraint= jsonUtils.parseJSON(constrainsPath);
        constraints = jsonUtils.getConstrainsKnapsack(knapsackConstraint);
        if(constraints!=null && !constraints.isEmpty()) {
            numberOfConstraints(1);
        }
    }

    @Override
    public IntegerSolution evaluate(IntegerSolution solution) {
        int totalValue = 0;
        int totalWeight = 0;
        for (int i = 0; i < numberOfItems; i++) {
            if (solution.variables().get(i) == 1) {
                totalValue += profits.get(i);
                totalWeight += weights.get(i);
            }
        }
        if (constraints!=null){
            int valueConstraint =0;
            for (Function<IntegerSolution, Integer> function: constraints) {
                valueConstraint+=function.apply(solution);
            }
            solution.constraints()[0] = valueConstraint;
        }
        if (totalWeight > capacity) {
            totalValue = 0;
        }
        solution.objectives()[0]= -1.0 *totalValue; // Maximize value
        solution.objectives()[1] =totalWeight; // Minimize weight
        return solution;
    }


    @Override
    public int numberOfVariables() {
        return numberOfItems;
    }


    public List<Integer> getLowerBounds() {
        List<Integer> lowerBounds = new ArrayList<>();
        for (int i = 0; i < numberOfItems; i++) {
            lowerBounds.add(0);
        }
        return lowerBounds;
    }


    public List<Integer> getUpperBounds() {
        List<Integer> upperBounds = new ArrayList<>();
        for (int i = 0; i < numberOfItems; i++) {
            upperBounds.add(1);
        }
        return upperBounds;
    }

    private void readData(String filePath){

        try{

            weights = new ArrayList<>();
            profits = new ArrayList<>();
            //Creating instance to avoid static member methods
            InputStream is = new FileInputStream(filePath);

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            this.numberOfItems = Integer.parseInt(br.readLine());
            this.capacity = Integer.parseInt(br.readLine());
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                weights.add(Integer.parseInt(st.nextToken()));
                profits.add(Integer.parseInt(st.nextToken()));
            }
            is.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


}