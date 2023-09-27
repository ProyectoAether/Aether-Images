package utils.semantic;

import dao.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({"unused"})
public class OWLUtils {
  static final int GRID_SIZE_X = 20;
  private OWLOntologyManager manager;
  private OWLOntology ontology;
  private OWLDataFactory factory;
  private IRI iri;
  private String pathOWL;
  private OWLReasoner reasoner;
  private OWLReasonerFactory reasonerFactory;

  private OWLObjectProperty lowerThan;
  private OWLObjectProperty equalThan;

  private OWLObjectProperty between;

  private OWLObjectProperty greaterThan;
  private OWLObjectProperty smallerThan;
  private OWLObjectProperty hasConstraint;
  private OWLObjectProperty hasContract;
  private OWLObjectProperty hasContractedPower;
  private OWLObjectProperty appliesTo;
  private OWLObjectProperty hasConstraintType;


  private  OWLNamedIndividual cop;
  private OWLDataProperty hasValueConsumption;
  private OWLDataProperty isMax;

   //knapsack
  private OWLNamedIndividual knapsackProblem;
  private OWLObjectProperty relates;
  private OWLObjectProperty isIncluded;
  private OWLObjectProperty isNotIncluded;
  private OWLObjectProperty hasItem;
  private OWLDataProperty hasCapacity;
  private OWLDataProperty hasProfit;
  private OWLDataProperty hasPosition;
  private OWLDataProperty hasWeight;



  public OWLUtils(String pathOWL) {
    this.manager = OWLManager.createOWLOntologyManager();
    this.factory = manager.getOWLDataFactory();
    this.pathOWL = pathOWL;
    this.ontology = null;
    this.reasonerFactory = new StructuralReasonerFactory();
    cop =
            factory.getOWLNamedIndividual(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/COP"));


    this.lowerThan =
            factory.getOWLObjectProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/lowerThan"));

    this.greaterThan =
            factory.getOWLObjectProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/greaterThan"));
    this.smallerThan =
            factory.getOWLObjectProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/smallerThan"));
    this.between =
            factory.getOWLObjectProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/between"));
    this.equalThan = factory.getOWLObjectProperty(
            IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/equalThan"));

    this.hasConstraint =
            factory.getOWLObjectProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/bigowl/hasConstraint"));
    this.hasConstraintType =
            factory.getOWLObjectProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/bigowl/hasConstraintType"));

    this.hasValueConsumption =
            factory.getOWLDataProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/hasValueConsumption"));

    this.appliesTo =
            factory.getOWLObjectProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/appliesTo"));

     this.hasContract =
            factory.getOWLObjectProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity/hasContract"));


     this.hasContractedPower =
            factory.getOWLObjectProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity/hasContractedPower"));

    this.hasValueConsumption =
            factory.getOWLDataProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/hasValueConsumption"));


    this.isMax =
            factory.getOWLDataProperty(
                    IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/isMax"));


    //knapsack
    this.knapsackProblem =
            factory.getOWLNamedIndividual(
                    IRI.create("http://www.ontologies.khaos.uma.es/knapsack-problem/KnapSackProblem"));
    //http://www.ontologies.khaos.uma.es/knapsack-problem/KnapSackProblem

    this.relates = factory.getOWLObjectProperty(
            IRI.create("http://www.ontologies.khaos.uma.es/knapsack-problem/relates"));
    this.isIncluded= factory.getOWLObjectProperty(
            IRI.create("http://www.ontologies.khaos.uma.es/knapsack-problem/isIncluded"));
    this.isNotIncluded= factory.getOWLObjectProperty(
            IRI.create("http://www.ontologies.khaos.uma.es/knapsack-problem/isNotIncluded"));
    this.hasItem = factory.getOWLObjectProperty(
            IRI.create("http://www.ontologies.khaos.uma.es/knapsack/hasItem"));
    this.hasCapacity = factory.getOWLDataProperty(
            IRI.create("http://www.ontologies.khaos.uma.es/knapsack/hasCapacity"));
    this.hasProfit = factory.getOWLDataProperty(
            IRI.create("http://www.ontologies.khaos.uma.es/knapsack/hasProfit"));
    this.hasPosition = factory.getOWLDataProperty(
            IRI.create("http://www.ontologies.khaos.uma.es/knapsack/hasPosition"));
    this.hasWeight = factory.getOWLDataProperty(
            IRI.create("http://www.ontologies.khaos.uma.es/knapsack/hasWeight"));
    setOwlUtils();

  }

  public  OWLUtils (){
    this("src/main/resources/ontology/electricity-problem.owl");
    //"src/main/resources/ontology/electricity-problem.owl"
    //"src/main/resources/ontology/knapsack-problem.owl"
  }
  private void setOwlUtils(){
    String path =System.getProperty("user.dir");
    this.addImport(
            path+"/script/src/main/resources/ontology/COP.owl",
            "http://www.ontologies.khaos.uma.es/COP");
    this.addImport(
            path+"/script/src/main/resources/ontology/bigowlv6.owl",
            "http://www.ontologies.khaos.uma.es/bigowl");
    this.addImport(
            path+"/script/src/main/resources/ontology/electricity.owl",
            "http://www.ontologies.khaos.uma.es/electricity");
    this.addImport(
            path+"/script/src/main/resources/ontology/knapsack.owl",
            "http://www.ontologies.khaos.uma.es/knapsack");
    this.loadOntology();

  }

  public void addImport(String pathFile, String uri) {
    manager
            .getIRIMappers()
            .add(new SimpleIRIMapper(IRI.create(uri), IRI.create("file://" + pathFile)));
  }

  public void loadOntology() {
    try {
      File file = new File(pathOWL);
      ontology = manager.loadOntologyFromOntologyDocument(file);
      reasoner = reasonerFactory.createReasoner(ontology);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void generateJSON(String path, List<Cliente> clienteList){

    if(clienteList!=null && !clienteList.isEmpty()) {
      List<Cliente> misClientes = new ArrayList<>();
      for (Cliente clienteContrato:clienteList) {
      String clienteCode =clienteContrato.getCups();

      OWLNamedIndividual cliente =
              factory.getOWLNamedIndividual(
                      IRI.create("http://www.ontologies.khaos.uma.es/electricity-problem/Client"));
      if (cliente != null) {
        NodeSet<OWLNamedIndividual> contractClients = reasoner.getObjectPropertyValues(cliente, hasContract);
        if (contractClients != null && !contractClients.isEmpty()) {
          List<OWLNamedIndividual> contractClientsList = new ArrayList<>(contractClients.getFlattened());
          NodeSet<OWLNamedIndividual> constraints =
                  reasoner.getObjectPropertyValues(cop, hasConstraint);
          if (constraints != null && !constraints.isEmpty()) {
            List<OWLNamedIndividual> constraintsList = new ArrayList<>(constraints.getFlattened());
            List<Contract> contratos = new ArrayList<>();
            for (OWLNamedIndividual contractClient : contractClientsList) {
              Contract contrato = new Contract();
              List<OWLNamedIndividual> contractsPower = getIndividualsByObjectProperty(reasoner, contractClient, hasContractedPower);
              List<OWLNamedIndividual> constraintsClient = getConstrainsByClientPowerContracted(reasoner, constraintsList, contractsPower, appliesTo);
              for (OWLNamedIndividual constraintClient : constraintsClient) {
                Constraint restriccion = new Constraint();
                List<OWLNamedIndividual> consumptionMaxList = getIndividualsByObjectProperty(reasoner, constraintClient, greaterThan);
                List<OWLNamedIndividual> consumptionMinList = getIndividualsByObjectProperty(reasoner, constraintClient, smallerThan);
                List<OWLNamedIndividual> consumptionEqualList = getIndividualsByObjectProperty(reasoner, constraintClient, equalThan);

                List<OWLNamedIndividual> constraintTypeList = getIndividualsByObjectProperty(reasoner, constraintClient, hasConstraintType);
                if(constraintTypeList!=null && !constraintTypeList.isEmpty()){
                  String miTipo =constraintTypeList.get(0).toString();
                  restriccion.setConstraintType(getTipo(miTipo));
                }
                int valueMax = 0;
                int valueMin = 0;
                if (consumptionMaxList != null && !consumptionMaxList.isEmpty()) {
                  valueMax = getValueFromIndividual(reasoner, consumptionMaxList.get(0), hasValueConsumption);
                  restriccion.setSmaller(valueMin);
                  restriccion.setGreater(valueMax);
                }
                if (consumptionMinList != null && !consumptionMinList.isEmpty()) {
                  valueMin = getValueFromIndividual(reasoner, consumptionMinList.get(0), hasValueConsumption);
                  restriccion.setSmaller(valueMin);
                  restriccion.setGreater(valueMax);
                }
                if (consumptionEqualList != null && !consumptionEqualList.isEmpty()) {
                  valueMin = getValueFromIndividual(reasoner, consumptionEqualList.get(0), hasValueConsumption);
                  restriccion.setSmaller(valueMin);
                  restriccion.setGreater(valueMax);
                }

                contrato.setConstraint(restriccion);
                //comprobar que no es vacía y solo traerá un elemnto, meter los vlores en la clase cliente
                //generar json
              }
              contratos.add(contrato);
            }
            //writingJSON(cliente.toString(), contratos, path);
            clienteContrato.setContractList(contratos);

          }
        }
      }
        misClientes.add(clienteContrato);
      }

      writingJSON(misClientes, path);
    }


//https://www.baeldung.com/java-org-json
  }
  private List<OWLNamedIndividual> getConstrainsByClientPowerContracted(OWLReasoner reasoner,List<OWLNamedIndividual> constraints, List<OWLNamedIndividual> powerContracts,OWLObjectProperty objectProperty){
    List<OWLNamedIndividual> result =new ArrayList<>();
    for (OWLNamedIndividual constraint: constraints) {
      List<OWLNamedIndividual> aux = getIndividualsByObjectProperty(reasoner,constraint,objectProperty);
      if(!aux.stream().filter(indv -> powerContracts.contains(indv)).collect(Collectors.toList()).isEmpty()){
        result.add(constraint);
      }
    }
    if(!result.isEmpty()){
      result= result.stream().distinct().collect(Collectors.toList());
    }
    return result;
  }

  public List<Function<Double, Double>> getConstrainsConsumptions(double pm,double pf) {
    List<Function<Double, Double>> result = null;
    if (ontology != null) {
      result = new ArrayList<>();
      NodeSet<OWLNamedIndividual> constrains =
              reasoner.getObjectPropertyValues(cop, hasConstraint);

     /* List<OWLLiteral> minimumCoverageList =
              getDataPropertyByIndividual(reasoner, newYorkGridConstraint, hasMinimumCoverage);
      if (minimumCoverageList != null && !minimumCoverageList.isEmpty()) {
        double coverage = Double.valueOf(minimumCoverageList.get(0).getLiteral());
        result.add(createSolutionByCoverage(coverage));
      }*/
      if (constrains != null && !constrains.isEmpty()) {
        Iterator<Node<OWLNamedIndividual>> it = constrains.iterator();
        double value=0;
        double valueGreater=0;
        double valueSmaller=0;
        while (it.hasNext()) {
          OWLNamedIndividual constraint = it.next().iterator().next();

          List<OWLNamedIndividual> lowerList = null;
          List<OWLNamedIndividual> greaterList = null;
          List<OWLNamedIndividual> smallerList = null;

          List<OWLLiteral> isMax =
                  getDataPropertyByIndividual(reasoner, constraint, this.isMax);

          lowerList = getIndividualsByObjectProperty(reasoner, constraint, lowerThan);
          greaterList = getIndividualsByObjectProperty(reasoner, constraint, greaterThan);
          smallerList = getIndividualsByObjectProperty(reasoner, constraint, smallerThan);

          if (lowerList != null && !lowerList.isEmpty()) {
            for (OWLNamedIndividual ind : lowerList) {
                 value = getValueFromIndividual(reasoner, ind, hasValueConsumption)/100.0;
                //result.add(createLowerPM(pm,pf,value));

            }
          }
          if (greaterList != null && !greaterList.isEmpty() && smallerList!=null && !greaterList.isEmpty() && greaterList.size() == smallerList.size()) {
            int i = 0;
            while (i<greaterList.size()) {
              OWLNamedIndividual indvGreater = greaterList.get(i);
               valueGreater= getValueFromIndividual(reasoner, indvGreater, hasValueConsumption)/100.0;
              OWLNamedIndividual indvSmaller = smallerList.get(i);
               valueSmaller= getValueFromIndividual(reasoner, indvSmaller, hasValueConsumption)/100.0;
              i++;

              if (isMax != null && !isMax.isEmpty()) {
                //result.add(createMaxPM(pm,pf,valueSmaller,valueGreater));
              } else {
                //result.add(createBetweenPM(pm,pf,valueSmaller,valueGreater));
              }
            }
          }

        }
        result.add(createPM(pm,value,valueSmaller,valueGreater));
      } // enfif
    }
    return result;
  }

  public List<Function<IntegerSolution, Integer>> getConstrainsKnapsack() {
    List<Function<IntegerSolution, Integer>> result = null;
    if (ontology != null) {
      result = new ArrayList<>();
      NodeSet<OWLNamedIndividual> constraints =
              reasoner.getObjectPropertyValues(knapsackProblem, hasConstraint);
      if(constraints!=null) {
        List<OWLNamedIndividual> constraintsList = new ArrayList<>(constraints.getFlattened());
        for (OWLNamedIndividual constraint: constraintsList) {

          NodeSet<OWLNamedIndividual> itemsNotIncludedNode =  reasoner.getObjectPropertyValues(constraint, isNotIncluded);
          if(itemsNotIncludedNode!=null && !itemsNotIncludedNode.isEmpty()) {
            List<OWLNamedIndividual> itemsNotIncludedList = itemsNotIncludedNode.isEmpty() ? new ArrayList<>() : new ArrayList<>(itemsNotIncludedNode.getFlattened());
            result.addAll(getConstrainsItems(false,itemsNotIncludedList));
          }

          NodeSet<OWLNamedIndividual> itemsIncludedNode =  reasoner.getObjectPropertyValues(constraint, isIncluded);
          if(itemsIncludedNode!=null && !itemsIncludedNode.isEmpty()) {
            List<OWLNamedIndividual> itemsIncludedList = itemsIncludedNode.isEmpty() ? new ArrayList<>() : new ArrayList<>(itemsIncludedNode.getFlattened());
            result.addAll(getConstrainsItems(true, itemsIncludedList));
          }

        }
      }
      NodeSet<OWLNamedIndividual> knap =
              reasoner.getObjectPropertyValues(knapsackProblem, relates);
      if(knap!=null){
        List<OWLNamedIndividual> knapList = new ArrayList<>(knap.getFlattened());
        if(knapList!=null && !knapList.isEmpty()){
          OWLNamedIndividual knapInd =knapList.get(0);
          result.add(getConstraintCapacity(knapInd));
        }
      }
    }
    return result;
  }

  public void generateJSONConstrainsKnapsack(String path) {
    KnapsackConstraint result = null;
    if (ontology != null) {
      result = new KnapsackConstraint();
      NodeSet<OWLNamedIndividual> constraints =
              reasoner.getObjectPropertyValues(knapsackProblem, hasConstraint);
      if(constraints!=null) {
        List<OWLNamedIndividual> constraintsList = new ArrayList<>(constraints.getFlattened());
        for (OWLNamedIndividual constraint: constraintsList) {

          NodeSet<OWLNamedIndividual> itemsNotIncludedNode =  reasoner.getObjectPropertyValues(constraint, isNotIncluded);
          if(itemsNotIncludedNode!=null && !itemsNotIncludedNode.isEmpty()) {
            List<OWLNamedIndividual> itemsNotIncludedList = itemsNotIncludedNode.isEmpty() ? new ArrayList<>() : new ArrayList<>(itemsNotIncludedNode.getFlattened());
            result.getOffList().addAll(getItems(itemsNotIncludedList));
          }

          NodeSet<OWLNamedIndividual> itemsIncludedNode =  reasoner.getObjectPropertyValues(constraint, isIncluded);
          if(itemsIncludedNode!=null && !itemsIncludedNode.isEmpty()) {
            List<OWLNamedIndividual> itemsIncludedList = itemsIncludedNode.isEmpty() ? new ArrayList<>() : new ArrayList<>(itemsIncludedNode.getFlattened());
            result.getInList().addAll(getItems(itemsIncludedList));
          }

        }
      }
      NodeSet<OWLNamedIndividual> knap =
              reasoner.getObjectPropertyValues(knapsackProblem, relates);
      if(knap!=null){
        List<OWLNamedIndividual> knapList = new ArrayList<>(knap.getFlattened());
        if(knapList!=null && !knapList.isEmpty()){
          OWLNamedIndividual knapInd =knapList.get(0);
          int capacity = getValueFromIndividual(reasoner,knapInd,hasCapacity);
          result.setCapacity(capacity);
        }
      }
    }
    writingJSON(result,path);
  }

  private List<ItemKnapSack> getItems( List<OWLNamedIndividual> items){
    List<ItemKnapSack> result = null;
    if(items!=null && !items.isEmpty()){
      result = new ArrayList<>();
      for (OWLNamedIndividual indv:items) {
        int position = getValueFromIndividual(reasoner,indv,hasPosition);
        int profit = getValueFromIndividual(reasoner,indv,hasProfit);
        int weight = getValueFromIndividual(reasoner,indv,hasWeight);
        ItemKnapSack itemKnapSack = new ItemKnapSack(position, profit, weight);
        result.add(itemKnapSack);
      }
    }
    return result;
  }

  private Function<IntegerSolution, Integer> getConstraintCapacity(OWLNamedIndividual knapInd){
    Function<IntegerSolution, Integer> result = null;
    if(knapInd!=null){
      int capacity = getValueFromIndividual(reasoner,knapInd,hasCapacity);
      result = createConstraintCapacity(capacity);
    }
    return result;
  }
  private List<Function<IntegerSolution, Integer>> getConstrainsItems(boolean included, List<OWLNamedIndividual> items){
    List<Function<IntegerSolution, Integer>> result = null;
    if(items!=null && !items.isEmpty()){
      result = new ArrayList<>();
      for (OWLNamedIndividual indv:items) {
        int position = getValueFromIndividual(reasoner,indv,hasPosition);
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




  private int getValueFromIndividual(
          OWLReasoner reasoner, OWLNamedIndividual individual, OWLDataProperty dataProperty) {
    int result = 0;
    List<OWLLiteral> aux = getDataPropertyByIndividual(reasoner, individual, dataProperty);
    if (aux != null && !aux.isEmpty()) {
      result = Integer.valueOf(aux.get(0).getLiteral());
    }
    return result;
  }


  private List<OWLNamedIndividual> getIndividualsByObjectProperty(
          OWLReasoner reasoner, OWLNamedIndividual individual, OWLObjectProperty objectProperty) {
    List<OWLNamedIndividual> result = new ArrayList<>();
    NodeSet<OWLNamedIndividual> nodes =
            reasoner.getObjectPropertyValues(individual, objectProperty);
    if (nodes != null) {
      nodes.forEach(nodeIndiv -> nodeIndiv.forEach(indiv -> result.add(indiv)));
    }
    return result;
  }

  private List<OWLLiteral> getDataPropertyByIndividual(
          OWLReasoner reasoner, OWLNamedIndividual individual, OWLDataProperty dataProperty) {
    List<OWLLiteral> result = new ArrayList<>();
    Set<OWLLiteral> nodes = reasoner.getDataPropertyValues(individual, dataProperty);
    if (nodes != null) {
      nodes.stream().forEach(owlLiteral -> result.add(owlLiteral));
    }
    return result;
  }


  private Function<Double, Double> createPM(
          double pm,double value, double valueSmaller,double valueGreater) {
    Function<Double, Double> constraint =
            potenciaContratada ->
            {
              double result =pm;
              if(pm <=  value*potenciaContratada){
                result =  value*potenciaContratada;
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

  private Function<Double, Double> createLowerPM(
          double pm,double pf, double value) {
    Function<Double, Double> constraint =
            potenciaContratada -> pm <=  value*potenciaContratada
                 ?  value*potenciaContratada : pf;

    return constraint;
  }

  private Function<Double, Double> createBetweenPM(
          double pm,double pf, double valueSmaller,double valueGreater) {
    Function<Double, Double> constraint =
            potenciaContratada ->
                    pm > potenciaContratada * valueSmaller && pm < potenciaContratada * valueGreater ?  pm : pf;

    return constraint;
  }

  private Function<Double, Double> createMaxPM(
          double pm,double pf, double valueSmaller,double valueGreater) {
    Function<Double, Double> constraint =
            potenciaContratada ->
                    pm > valueSmaller * potenciaContratada && !(pm > potenciaContratada * valueSmaller
                            && pm < potenciaContratada * valueGreater) ?  potenciaContratada +
                            (2 * (pm - potenciaContratada)) : pf;

    return constraint;
  }
//https://www.baeldung.com/java-org-json

  private void writingJSON(List<Cliente> clienteList, String path){
    if(clienteList!=null && !clienteList.isEmpty()){
      JSONArray ja = new JSONArray();
      for (Cliente client:clienteList) {
        JSONObject jo = new JSONObject();
        jo.put("client",client.getCups().replaceAll("[\\s|\\u00A0]+", ""));
        jo.put("contract",client.getContractList());
        ja.put(jo);
      }
      try {
        FileWriter file = new FileWriter(path);
        file.write(ja.toString(3));
        file.close();
      } catch (IOException e) {
        // Auto-generated catch block
        e.printStackTrace();
      }
      System.out.println("JSON file created: "+ja);
    }




    /*JSONArray ja = new JSONArray();
    ja.put(Boolean.TRUE);
    ja.put("lorem ipsum");

    JSONObject jo = new JSONObject();
    jo.put("name", "jon doe");
    jo.put("age", "22");
    jo.put("city", "chicago");

    ja.put(jo);*/
  }

  private void writingJSON(KnapsackConstraint knapsack, String path){
    if(knapsack!=null){
      JSONObject jo = new JSONObject();
      jo.put("capacity",knapsack.getCapacity());
      if(knapsack.getInList()!=null && !knapsack.getInList().isEmpty()){
        JSONArray ja = new JSONArray();
        for (ItemKnapSack item:knapsack.getInList()) {
          JSONObject joItem = new JSONObject();
          joItem.put("position",item.getPosition());
          joItem.put("profit",item.getProfit());
          joItem.put("weight",item.getWeight());
          ja.put(joItem);
        }
        jo.put("include",ja);
      }
      if(knapsack.getOffList()!=null && !knapsack.getOffList().isEmpty()){
        JSONArray ja = new JSONArray();
        for (ItemKnapSack item:knapsack.getOffList()) {
          JSONObject joItem = new JSONObject();
          joItem.put("position",item.getPosition());
          joItem.put("profit",item.getProfit());
          joItem.put("weight",item.getWeight());
          ja.put(joItem);
        }
        jo.put("exclude",ja);
      }

      try {
        FileWriter file = new FileWriter(path);
        file.write(jo.toString(3));
        file.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      System.out.println("JSON file created: "+jo);
    }

  }

  private Constraint.Tipo getTipo(String text){
    Constraint.Tipo tipo = Constraint.Tipo.undefined;
    if(text.toLowerCase().contains("low")){
      tipo = Constraint.Tipo.low;
    }else if(text.toLowerCase().contains("between")){
      tipo = Constraint.Tipo.between;
    } else if (text.toLowerCase().contains("high")) {
      tipo = Constraint.Tipo.high;
    }else if (text.toLowerCase().contains("equal")) {
      tipo = Constraint.Tipo.equal;
    }
    return tipo;
  }


}
