package utils.semantic;

import dao.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings(value = { "unchecked", "unused" })
public class JSONUtils {

    public List<Cliente> parseJSON(String path, List<Cliente> clientes) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(path);
        JSONArray  ja = (JSONArray) jsonParser.parse(fileReader);
        Iterator<JSONObject> iterator = ja.iterator();

        while (iterator.hasNext()){
            JSONObject object = iterator.next();
          //  System.out.println(object.get("client"));
            Cliente cliente = getCliente(clientes,object.get("client").toString().trim());
            //cliente.setCups(object.get("client").toString());
            //Contract
            JSONArray contracts = (JSONArray)object.get("contract");
            Iterator<JSONObject> iteratorContract = contracts.iterator();
          //  System.out.println(contracts.size());

            while (iteratorContract.hasNext()){

                Contract contract = new Contract();
                JSONArray contractsByUser = (JSONArray)iteratorContract.next().get("constraintList");
                Iterator<JSONObject> iteratorConstraints = contractsByUser.iterator();
                while (iteratorConstraints.hasNext()){
                    Constraint constraintDAO = new Constraint();
                    JSONObject constraint = iteratorConstraints.next();
                   // System.out.println(constraint.get("smaller"));
                    Long aux = (Long)constraint.get("smaller");
                    constraintDAO.setSmaller(aux.doubleValue());
                   // System.out.println(constraint.get("constraintType"));
                    constraintDAO.setConstraintType(constraint.get("constraintType").toString());
                    //System.out.println(constraint.get("greater"));
                    aux = (Long)constraint.get("greater");
                    constraintDAO.setGreater(aux.doubleValue());
                    contract.setConstraint(constraintDAO);
                }

                cliente.setContract(contract);
            }

        }
        return clientes;//crear las functions a partir de esto
    }
    public KnapsackConstraint parseJSON(String path) throws IOException, ParseException {
        KnapsackConstraint result = null;
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(path);
        JSONObject jsonObject = (JSONObject)jsonParser.parse(fileReader);
        if(jsonObject!=null){
            result = new KnapsackConstraint();
            Long aux = (Long)jsonObject.get("capacity");
            if(aux!=null){
                result.setCapacity(aux.intValue());
            }
            JSONArray include = (JSONArray)jsonObject.get("include");
            result.setInList(generateItemKnapSack(include));
            JSONArray exclude = (JSONArray)jsonObject.get("exclude");
            result.setOffList(generateItemKnapSack(exclude));
        }
        //JSONArray  ja = (JSONArray) jsonParser.parse(fileReader);
        //Iterator<JSONObject> iterator = ja.iterator();
        return result;
    }

    private List<ItemKnapSack> generateItemKnapSack(JSONArray array){
        List<ItemKnapSack> result = new ArrayList<>();
        if(array!=null && !array.isEmpty()){
            Iterator<JSONObject> itInclude = array.iterator();
            while (itInclude.hasNext()){
                JSONObject jsonObject = itInclude.next();
                Long auxWeight = (Long)jsonObject.get("weight");
                Long auxPosition = (Long)jsonObject.get("position");
                Long auxProfit = (Long)jsonObject.get("profit");
                ItemKnapSack item = new ItemKnapSack(auxPosition.intValue(),auxProfit.intValue(),auxWeight.intValue());
                result.add(item);
            }
        }
        return result;
    }
    private Cliente getCliente(List<Cliente> clientes, String id){
        Cliente result =null;
        List<Cliente> aux = clientes.stream().filter(cliente -> cliente.getCups().equalsIgnoreCase(id)).collect(Collectors.toList());
        if(aux!=null && !aux.isEmpty()){
            result = aux.get(0);
        }
        return result;
    }

    public List<Function<Double, Double>> getConstrainsConsumptions(double pm, double pf, Cliente cliente) {
        List<Function<Double, Double>> result = null;
        if (cliente != null) {
            result = new ArrayList<>();



            if (cliente.getContractList() != null && !cliente.getContractList().isEmpty()) {

                double valueGreater=0;
                double valueSmaller=0;

                Iterator<Contract> itContract = cliente.getContractList().iterator();
                while (itContract.hasNext()) {
                    Contract contract = itContract.next();
                    if(contract.getConstraintList()!=null && !contract.getConstraintList().isEmpty()){
                        Iterator<Constraint> it = contract.getConstraintList().iterator();
                        while(it.hasNext()){
                            Constraint constraint = it.next();
                            valueSmaller = constraint.getSmaller()/100.0;
                            valueGreater = constraint.getGreater()/100.0;
                          result.add(createPM(pm,valueSmaller,valueGreater));
                        }
                    }
                }
               // result.add(createPM(pm,value,valueSmaller,valueGreater));
            } // enfif
        }
        return result;
    }


    private Function<Double, Double> createPM(
            double pm, double valueSmaller,double valueGreater) {
        Function<Double, Double> constraint =
                potenciaContratada ->
                {
                    double result =pm;
                    if(pm <=  valueSmaller*potenciaContratada){
                        result =  valueSmaller*potenciaContratada;
                    }else if(pm > potenciaContratada * valueSmaller && pm < potenciaContratada * valueGreater){
                        result = pm;
                    }else{
                        result= potenciaContratada +
                                (2 * (pm - potenciaContratada));
                    }
                    return result;
                };


        return constraint;
    }

    public List<Function<IntegerSolution, Integer>> getConstrainsKnapsack(KnapsackConstraint knapsackConstraint) {
        List<Function<IntegerSolution, Integer>> result = null;
        if (knapsackConstraint != null) {
            result = new ArrayList<>();
            result.addAll(getConstrainsItems(false,knapsackConstraint.getOffList()));
            result.addAll(getConstrainsItems(true, knapsackConstraint.getInList()));
            result.add(createConstraintCapacity(knapsackConstraint.getCapacity()));

        }
        return result;
    }

    private List<Function<IntegerSolution, Integer>> getConstrainsItems(boolean included, List<ItemKnapSack> items){
        List<Function<IntegerSolution, Integer>> result = null;
        if(items!=null && !items.isEmpty()){
            result = new ArrayList<>();
            for (ItemKnapSack indv:items) {
                int position = indv.getPosition();
                result.add(createPositionFunction(included,position));
            }
        }
        return result;
    }
    private Function<IntegerSolution, Integer> createConstraintCapacity(int capacity){
        Function<IntegerSolution, Integer> constraint = solution -> solution.objectives()[1]<=capacity ? 0 : -Integer.MAX_VALUE;
        return constraint;
    }
    private Function<IntegerSolution, Integer> createPositionFunction(boolean included, int position)
    {
        Function<IntegerSolution, Integer> constraint =
                solution -> {
                    int result = -Integer.MAX_VALUE;
                    if(included && position>=0 && position<solution.variables().size()){
                        result = solution.variables().get(position)==1 ? 0 : -Integer.MAX_VALUE;
                    }else if(position>=0 && position<solution.variables().size()){
                        result = solution.variables().get(position)==0 ? 0 : -Integer.MAX_VALUE;
                    }
                    return result;
                };

        return constraint;
    }

   /* public static void main(String[] args) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader("electrica.json");
        JSONArray  ja = (JSONArray) jsonParser.parse(fileReader);
        Iterator<JSONObject> iterator = ja.iterator();
        while (iterator.hasNext()){
            JSONObject object = iterator.next();
            System.out.println(object.get("client"));
            //Contract
            JSONArray contracts = (JSONArray)object.get("contract");
            Iterator<JSONObject> iteratorContract = contracts.iterator();
            //System.out.println(contract.next().get("constraintList"));
            while (iteratorContract.hasNext()){
                JSONArray contractsByUser = (JSONArray)iteratorContract.next().get("constraintList");
                Iterator<JSONObject> iteratorConstraints = contractsByUser.iterator();
                while (iteratorConstraints.hasNext()){
                    JSONObject constraint = iteratorConstraints.next();
                    System.out.println(constraint.get("smaller"));
                    System.out.println(constraint.get("constraintTipo"));
                    System.out.println(constraint.get("greater"));
                }
            }
        }

    }*/
}
