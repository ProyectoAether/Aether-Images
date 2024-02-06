import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dao.Cliente;
import dao.Recommendation;

import org.json.simple.parser.ParseException;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.IntegerSBXCrossover;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.mutation.impl.IntegerPolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import problem.DatasetUtils;
import problem.Electrico;
import problem.GenerateRecommendations;
import problem.Knapsack;
import problem.MultiobjectiveKnapsackProblem;
import problem.ProblemUtils;
import utils.semantic.JSONUtils;


public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        final String OUTPUT = "/mnt/shared/results.csv";
        String problem_type = args[0];

        if (args.length == 0) {
            throw new IllegalArgumentException("No arguments found");
        } else if (args.length < 3) {
            throw new IllegalArgumentException("Not enough arguments found. 'problem-type', 'Restrictions file' and 'problem data file' minimum needed");
        }

        switch (problem_type.toLowerCase()) {
            case ("electric"):
                /* 
                 * -----ELECTRIC PROBLEMS-----
                 * 
                 * Must recive: 
                 *  - ARG1 --> Restrictions JSON file (from createRestrictions)
                 *  - ARG2 --> Problem information file (conquense file)
                 *  - ARG3 --> Number of clients to read from ARG1. Default value = 10
                 *  - ARG4 --> Prices list as an string (e.g. "{1,2,3}"). Default value = {0.25, 0.17, 0.30}
                 * 
                 * Output:
                 *  CSV file with the prices recommendations for each client (output/results.csv)
                */

                String restriction_path = args[1];
                String conquense_path = args[2];
                Integer clients_number;
                double[] precio = {};
                
                try {clients_number = Integer.parseInt(args[3]);}
                catch(IndexOutOfBoundsException e) {clients_number = 10;}

                try {
                    String[] precio_arg = args[4].split(", |,");
                    for (String p:precio_arg) {
                        p = p.replace("{", "");
                        p = p.replace("}", "");
                        precio = Arrays.copyOf(precio, precio.length+1);
                        precio[precio.length-1] = Double.parseDouble(p);
                    }
                } catch(IndexOutOfBoundsException e) {
                    precio = new double[]{0.25, 0.17, 0.30};
                }
                
                JSONUtils jsonUtils = new JSONUtils();
                List<Cliente> clientes = DatasetUtils.loadClientsConquense(conquense_path, clients_number);
                List<Cliente> clients = jsonUtils.parseJSON(restriction_path, clientes);

                List<Electrico> problems = ProblemUtils.createProblems(clients, precio);
                List<Recommendation> recommendations = GenerateRecommendations.generate(problems);
                problem.DatasetUtils.writeRecommendationsToCsv(recommendations, OUTPUT);    // Result into CSV
                break;

            case "knapsack":
                /* 
                 * -----KNAPSACK PROBLEMS-----
                 * 
                 * Must recive: 
                 *  - ARG1 --> Knapsack restrictions file from upstream (JSON file)
                 *  - ARG2 --> Knapsack Instance (.kp file)
                 *  - ARG3 --> Population size. Default value = 100
                 *  - ARG4 --> Max. evaluations. Default value = 25000
                 * 
                 * Output:
                 *  CSV file with the objectives and variables of the problem (output/results.csv)
                */

                String kp_restrictions = args[1];
                JMetalLogger.logger.info(String.format("Knapsack problem do not have restrictions(%s)", kp_restrictions));

                String kp_instance = args[2];
                Integer population_size;
                Integer max_evaluations;
                try{population_size = Integer.parseInt(args[3]);}
                catch(IndexOutOfBoundsException e) {population_size = 100;}
                try{max_evaluations = Integer.parseInt(args[4]);}
                catch(IndexOutOfBoundsException e){max_evaluations = 25000;}
                
            
                Knapsack kp_problem = new Knapsack(kp_instance);

                Algorithm<BinarySolution> kp_algorithm;
                CrossoverOperator<BinarySolution> kp_crossover;
                MutationOperator<BinarySolution> kp_mutation;
                SelectionOperator<List<BinarySolution>, BinarySolution> kp_selection;

                kp_crossover = new SinglePointCrossover<BinarySolution>(0.9) ;

                double kp_mutationProbability = 1.0 / kp_problem.bitsFromVariable(0) ;
                kp_mutation = new BitFlipMutation<BinarySolution>(kp_mutationProbability) ;

                kp_selection = new BinaryTournamentSelection<BinarySolution>();

                kp_algorithm = new GeneticAlgorithmBuilder<>(kp_problem, kp_crossover, kp_mutation)
                        .setPopulationSize(population_size)
                        .setMaxEvaluations(max_evaluations)
                        .setSelectionOperator(kp_selection)
                        .build() ;

                AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(kp_algorithm).execute();


                BinarySolution solution = kp_algorithm.getResult() ;
                List<BinarySolution> population = new ArrayList<>(1) ;
                population.add(solution);

                long computingTime = algorithmRunner.getComputingTime() ;

                problem.DatasetUtils.KnapsackCSV(solution, OUTPUT);

                JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
                JMetalLogger.logger.info("Objectives and Variables values have been written to file results.csv");

                // JMetalLogger.logger.info("Fitness: " + (-1)*solution.objectives()[0]) ;
                // JMetalLogger.logger.info("Solution: " + solution.variables().get(0)) ;
                break;
            
            case "multiobjective-knapsack":
                /* 
                 * -----MULTIOBJECTIVE KNAPSACK PROBLEMS-----
                 * 
                 * Must recive: 
                 *  - ARG1 --> Multiobjective knapsack restrictions from upstream (JSON file)
                 *  - ARG2 --> Multiobjective knapsack instance from upstream (kp file)
                 *  - ARG3 --> Population Size
                 *  - ARG4 --> Max. evaluations
                 * 
                 * Output:
                 *  CSV file with the objectives and variables of the problem (output/results.csv)
                */

                String multiobjective_knapsack_restrictions = args[1];
                String multiobjective_knapsack_instance = args[2];
                Integer populationSize;
                Integer maxEvaluations;
                try{populationSize = Integer.parseInt(args[3]);}
                catch(IndexOutOfBoundsException e){populationSize = 100;}
                try{maxEvaluations = Integer.parseInt(args[4]);}
                catch(IndexOutOfBoundsException e){maxEvaluations = 25000;}

                Problem<IntegerSolution> mo_problem = new MultiobjectiveKnapsackProblem(multiobjective_knapsack_instance, multiobjective_knapsack_restrictions);
                double mo_crossoverProbability = 0.9;
                double mo_crossoverDistributionIndex = 20.0;
                CrossoverOperator<IntegerSolution> mo_crossover = new IntegerSBXCrossover(mo_crossoverProbability,mo_crossoverDistributionIndex);

                double mo_mutationProbability = 1.0 / mo_problem.numberOfVariables();
                double mo_mutationDistributionIndex = 20.0;
                MutationOperator<IntegerSolution> mo_mutation = new IntegerPolynomialMutation(mo_mutationProbability,mo_mutationDistributionIndex);

                SelectionOperator<List<IntegerSolution>, IntegerSolution> mo_selection = new BinaryTournamentSelection<>(
                        new RankingAndCrowdingDistanceComparator<>());
                NSGAII<IntegerSolution> mo_algorithm =
                        new NSGAIIBuilder<>(mo_problem, mo_crossover, mo_mutation, populationSize)
                                .setSelectionOperator(mo_selection)
                                .setMaxEvaluations(maxEvaluations)
                                .build();

                AlgorithmRunner mo_algorithmRunner = new AlgorithmRunner.Executor(mo_algorithm)
                        .execute();
                List<IntegerSolution> solutions = mo_algorithm.getResult();
                long mo_computingTime = mo_algorithmRunner.getComputingTime() ;

                problem.DatasetUtils.MOKnapsackCSV(solutions, OUTPUT);
                // new SolutionListOutput(solutions)
                //         .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                //         .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                //         .print();

                JMetalLogger.logger.info("Total execution time: " + mo_computingTime + "ms");
                JMetalLogger.logger.info("Objectives and Variables values have been written to file results.csv");

                break;
        }   
    }
}