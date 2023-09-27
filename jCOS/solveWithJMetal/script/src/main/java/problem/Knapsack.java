package problem;

// import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
// import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Knapsack  extends AbstractBinaryProblem {

    private int numberOPfItems;
    private int capacity;
    private List<Integer> weights;
    private List<Integer> profits;
    public Knapsack(int numberOfItems, int capacity,List<Integer> weights, List<Integer> profits  ){
        this.numberOPfItems= numberOfItems;
        this.capacity = capacity;
        this.weights = weights;
        this.profits = profits;

    }
    public Knapsack(String filePath){

        try{

            weights = new ArrayList<>();
            profits = new ArrayList<>();
            //Creating instance to avoid static member methods
            InputStream is = new FileInputStream(filePath);
            
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            this.numberOPfItems = Integer.parseInt(br.readLine());
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




    @Override
    public int numberOfVariables() {
        return 1;
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
    public int totalNumberOfBits() {
        return 0;
    }

    @Override
    public String name() {
        return "KnapSack";
    }

    @Override
    public BinarySolution evaluate(BinarySolution binarySolution) {
        double totalProfits =0.0;
        double totalWeigths =0.0;
        BitSet bitset = binarySolution.variables().get(0);
        for (int i = 0; i < bitset.length(); i++) {
            if (bitset.get(i)) {
                totalProfits += this.profits.get(i);
                totalWeigths +=this.weights.get(i);
            }
        }
        if(totalWeigths > this.capacity){
            totalProfits =0.0;
        }
        binarySolution.objectives()[0] = -1.0 * totalProfits;
        /*
        total_profits = 0.0
        total_weigths = 0.0

        for index, bits in enumerate(solution.variables[0]):
            if bits:
                total_profits += self.profits[index]
                total_weigths += self.weights[index]

        if total_weigths > self.capacity:
            total_profits = 0.0

        solution.objectives[0] = -1.0 * total_profits
        return solution
        * */
        return binarySolution;
    }

    @Override
    public List<Integer> listOfBitsPerVariable() {
        return Arrays.asList(numberOPfItems);
    }

    @Override
    public int bitsFromVariable(int i) {
        return numberOPfItems;
    }

}
