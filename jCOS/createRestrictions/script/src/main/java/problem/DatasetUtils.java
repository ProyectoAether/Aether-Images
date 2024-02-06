package problem;

import dao.Cliente;
import dao.Recommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings({"unused"})
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


}
