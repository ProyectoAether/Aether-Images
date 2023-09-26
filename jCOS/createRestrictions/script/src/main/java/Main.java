import java.io.IOException;
import java.util.ArrayList;
// import java.util.ArrayList;
import java.util.List;
// import org.uma.jmetal.util.observer.impl.FitnessObserver;

// import dao.Recommendation;
import org.json.simple.parser.ParseException;

import dao.Cliente;
import problem.DatasetUtils;
// import problem.Electrico;
import utils.semantic.JSONUtils;
import utils.semantic.OWLUtils;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        final String OUTPUT = "data/restrictions.json";

        if (args.length == 0) {
            throw new IllegalArgumentException("No arguments found. 'problem-type' and 'semantics', at least,  expected");
        } else if (args.length < 2) {
            throw new IllegalArgumentException("Not enough arguments found. 'problem-type' and 'semantics' minimum needed");
        }

        // Choose logic depending on the problem type declared
        switch (args[0].toLowerCase()) {
            case "electric":
                /* 
                 * -----ELECTRIC PROBLEMS-----
                 * 
                 * Must recive: 
                 *  - ARG1 --> Clients JSON file (conquense file)
                 *  - ARG2 --> Semantics OWL file (COP file)
                 *  - ARG3 --> Number of clients to read from ARG1
                 * 
                 * Output:
                 *  JSON file with clients and contracts restrictions in '/output/restrictions.json'
                */

                String conquense_file = args[2];
                String COPontology = args[1];
                Integer n_clients = Integer.parseInt(args[3]);

                List<Cliente> clients = DatasetUtils.loadClientsConquense(conquense_file, n_clients);
                OWLUtils electric_owlUtils = new OWLUtils(COPontology);
                electric_owlUtils.generateJSON(OUTPUT, clients);

                for (Cliente c:clients) {
                    c.setContractList(new ArrayList<>());
                }
                JSONUtils jsonUtils = new JSONUtils();
                List<Cliente> clients_contracts = jsonUtils.parseJSON(OUTPUT, clients);
                electric_owlUtils.generateJSON(OUTPUT, clients_contracts);
                break;

            case "knapsack":
                /*
                 * -----KNAPSACK PROBLEMS-----
                 * 
                 * <UNUSED>
                */
                throw new UnsupportedOperationException("Knapsack problems do not need semantics");
            case "multiobjective-knapsack":
                /*
                 * -----KNAPSACK MULTIOBJECTIVE PROBLEMS-----
                 * Must recive: 
                 *  - ARG1 --> Semantics of the Knapsack multiobjective problem (OWL file)
                 * 
                 * Output:
                 *  JSON file with knapsack restrictions in '/output/restrictions.json'
                */
                String knapsackProblem_path = args[1];

                OWLUtils MOknapsack_owlUtils = new OWLUtils(knapsackProblem_path);
                MOknapsack_owlUtils.generateJSONConstrainsKnapsack(OUTPUT);
                break;

            default:
                throw new IllegalArgumentException("Problem type not supported");
        }
    }
}