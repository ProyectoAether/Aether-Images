package problem;

import dao.Recommendation;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateRecommendations {

    @SuppressWarnings("unused")
    public static Recommendation generate(Electrico problem) {
        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        double mutationProbability = 1.0 / problem.numberOfVariables();
        double mutationDistributionIndex = 20.0;
        var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

        var selection = new NaryTournamentSelection<DoubleSolution>(2, new ObjectiveComparator<>(0));

        //Termination termination = new TerminationByEvaluations(150000);
        /*Termination termination = new TerminationByComputingTime( 5 * 1000);

        int populationSize = 1000;
        int offspringPopulationSize = 1000;

        var algorithm =
                new NSGAII<>(
                        problem,
                        populationSize,
                        offspringPopulationSize,
                        crossover,
                        mutation,
                        termination);*/
        int populationSize = 100;
        NSGAII<DoubleSolution> algorithm =
                new NSGAIIBuilder<>(problem, crossover, mutation, populationSize)
                        .setSelectionOperator(selection)
                        .setMaxEvaluations(5 * 1000)
                        .build();

        //algorithm.getObservable().register(new FitnessObserver(100));

        algorithm.run();

        List<DoubleSolution> population = algorithm.getResult();
        // JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
        // JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());

        DoubleSolution solution = (DoubleSolution) algorithm.getResult().get(0);

        List<Double> pc = Arrays.stream(problem.getCliente().getPotenciaContratada()).boxed().collect(Collectors.toList());

        return new Recommendation(
                problem.getCliente(),
                solution.variables().stream().mapToDouble(Double::doubleValue).toArray(),
                problem.calculaTp(pc),
                solution.objectives()[0]
        );
    }

    public static List<Recommendation> generate(List<Electrico> problems) {
        return problems.stream().map(GenerateRecommendations::generate).collect(Collectors.toList());
    }

}
