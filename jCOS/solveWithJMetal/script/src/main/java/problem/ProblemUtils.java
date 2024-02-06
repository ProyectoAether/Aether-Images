package problem;

import dao.Cliente;

import java.util.List;
import java.util.stream.Collectors;

public class ProblemUtils {

    public static Electrico createProblem(Cliente c, double[] precio) {
        return new Electrico(c, precio);
    }

    public static List<Electrico> createProblems(List<Cliente> cs, double[] precio) {
        return cs.stream().map(cliente -> createProblem(cliente, precio)).collect(Collectors.toList());
    }





}
