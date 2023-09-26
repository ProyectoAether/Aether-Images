import java.io.IOException;
import java.util.ArrayList;
// import java.util.ArrayList;
import java.util.List;
// import org.uma.jmetal.util.observer.impl.FitnessObserver;
import java.util.logging.Logger;

// import dao.Recommendation;
import org.json.simple.parser.ParseException;

import dao.Cliente;
import problem.DatasetUtils;
// import problem.Electrico;
import utils.semantic.JSONUtils;
import utils.semantic.OWLUtils;

import java.io.FileWriter;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        final String OUTPUT = "data/restrictions.json";
        final Logger log = Logger.getLogger(Main.class.getName());


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
                log.info("Electric problem created");
                electric_owlUtils.generateJSON(OUTPUT, clients_contracts);
                break;

            case "knapsack":
                /*
                 * -----KNAPSACK PROBLEMS-----
                 * 
                 * <UNUSED>
                */
                
                log.info("Knapsack problems do not need semantics");
                try {
                    FileWriter fileWriter = new FileWriter(OUTPUT);
                    fileWriter.write("{}");
                    fileWriter.close();
                } catch(IOException err) {
                    log.severe(err.getStackTrace().toString());
                }

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
                log.info("Knapsack multiobjective problem created");
                MOknapsack_owlUtils.generateJSONConstrainsKnapsack(OUTPUT);
                break;

            default:
                throw new IllegalArgumentException("Problem type not supported");
        }
    }
}