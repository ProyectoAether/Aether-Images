package problem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.binarySet.BinarySet;


import dao.Cliente;
import dao.Recommendation;

public class DatasetUtils {

    public static List<Cliente> loadClientsConquense(String path, int n) throws IOException {
        return JsonReader.loadJsonObjects(path, n).stream().map(obj -> {
            String cups = obj.get("localizacion").get("cups").asText().trim();
            double[][] consumos = StreamSupport.stream(obj.get("consumos").spliterator(), false).map(c -> {
                double p1 = c.get("potencia_maxima_p1").asDouble(0.0);
                double p2 = c.get("potencia_maxima_p2").asDouble(0.0);
                double p3 = c.get("potencia_maxima_p3").asDouble(0.0);
                return new double[]{p1, p2, p3};
            }).toArray(double[][]::new);

            double[] potenciaContratada = new double[]{
                    obj.get("datos_contrato").get("potencia_p1").asDouble(0.0),
                    obj.get("datos_contrato").get("potencia_p2").asDouble(0.0),
                    obj.get("datos_contrato").get("potencia_p3").asDouble(0.0)
            };

            return new Cliente(consumos, potenciaContratada, cups, Cliente.Company.Conquense);
        }).collect(Collectors.toList());
    }

    public static void writeRecommendationsToCsv(List<Recommendation> recommendations, String csvPath) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvPath));

        //List<String> toWrite = new ArrayList<>();
        //toWrite.add();

        String header = "cups,potencia_contratada_p1,potencia_contratada_p2,potencia_contratada_p3,tp_actual,potencia_recomendada_p1,potencia_recomendada_p2,potencia_recomendada_p3,tp_recomendado";

        writer.write(header);
        writer.newLine();
        recommendations.stream().map(r ->
            String.format(
                    Locale.ENGLISH,
                "%s,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f",
                    r.getCliente().getCups(),
                    r.getCliente().getPotenciaContratada()[0],
                    r.getCliente().getPotenciaContratada()[1],
                    r.getCliente().getPotenciaContratada()[2],
                    r.getTpActual(),
                    r.getPotenciaRecomendada()[0],
                    r.getPotenciaRecomendada()[1],
                    r.getPotenciaRecomendada()[2],
                    r.getTpRecomendado())
        ).forEach(s -> {
            try {
                writer.write(s);
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        writer.close();
    }

    public static void KnapsackCSV(BinarySolution result, String csvPath) throws IOException{
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvPath));

        String[] headers = {};
        for (int i = 0; i < result.objectives().length; i++) {
            headers = Arrays.copyOf(headers, (headers.length+1));
            headers[headers.length-1] = String.format("FUN%d", i+1);
        }
        for (int i = 0; i < result.variables().get(0).getBinarySetLength(); i++) {
            headers = Arrays.copyOf(headers, (headers.length+1));
            headers[headers.length-1] = String.format("VAR%d", i+1);
        }
        String headers_str = Arrays.toString(headers).replace("[", "").replace("]", "").replace(" ", "\t"); 

        writer.write(headers_str);
        writer.newLine();

        try {
            BinarySet variables = result.variables().get(0);
            double[] objectives = result.objectives();
            writeCSV_binary(writer, variables, objectives);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }

        writer.close();

    }

    public static void MOKnapsackCSV(List<IntegerSolution> result, String csvPath) throws IOException{
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvPath));

        String[] headers = {};
        for (int i = 0; i < result.get(0).objectives().length; i++) {
            headers = Arrays.copyOf(headers, (headers.length+1));
            headers[headers.length-1] = String.format("FUN%d", i+1);
        }
        for (int i = 0; i < result.get(0).variables().size(); i++) {
            headers = Arrays.copyOf(headers, (headers.length+1));
            headers[headers.length-1] = String.format("VAR%d", i+1);
        }
        String headers_str = Arrays.toString(headers).replace("[", "").replace("]", "").replace(" ", "\t"); 

        writer.write(headers_str);
        writer.newLine();

        try {
            for(IntegerSolution sol:result) {
                List<Integer> variables = sol.variables();
                double[] objectives = sol.objectives();
                writeCSV_list(writer, variables, objectives);
            }
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }

        writer.close();

    }

    private static void writeCSV_binary(BufferedWriter writer, BinarySet variables, double[] objectives) throws IOException {
        Number[] bitArray = {};
        for (int i = 0; i < objectives.length; i++) {
            bitArray = Arrays.copyOf(bitArray, (bitArray.length+1));
            bitArray[bitArray.length-1] = objectives[i];
        }
        for(int i = 0; i < variables.getBinarySetLength(); i++) {
            bitArray = Arrays.copyOf(bitArray, (bitArray.length+1));
            if(variables.get(i)) {
                bitArray[bitArray.length-1] = 1;
            } else {
                bitArray[bitArray.length-1] = 0;
            }
        }

        String bitArray_str = Arrays.toString(bitArray).replace("[", "").replace("]", "").replace(" ", "\t");
        writer.write(bitArray_str);
        writer.newLine();
    }

    private static void writeCSV_list(BufferedWriter writer, List<Integer> variables, double[] objectives) throws IOException {
        Number[] bitArray = {};
        for (int i = 0; i < objectives.length; i++) {
            bitArray = Arrays.copyOf(bitArray, (bitArray.length+1));
            bitArray[bitArray.length-1] = objectives[i];
        }
        for(int i = 0; i < variables.size(); i++) {
            bitArray = Arrays.copyOf(bitArray, (bitArray.length+1));
            bitArray[bitArray.length-1] = variables.get(i);
        }

        String bitArray_str = Arrays.toString(bitArray).replace("[", "").replace("]", "").replace(" ", "\t");
        writer.write(bitArray_str);
        writer.newLine();
    }
}
