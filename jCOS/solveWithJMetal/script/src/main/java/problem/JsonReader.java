package problem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonReader {

    public static List<String> readNLines(String path, int n) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get(path));
        Stream<String> stream = reader.lines();

        if(n>0) stream = stream.limit(n);

        return stream.collect(Collectors.toList());
    }

    public static List<JsonNode> listString2Json(List<String> jsonStr) {
        ObjectMapper mapper = new ObjectMapper();
        return jsonStr.stream().map(str -> {
            try {
                return mapper.readTree(str);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public static List<JsonNode> loadJsonObjects(String path, int n) throws IOException {
        return listString2Json(readNLines(path, n));
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = readNLines("input/conquense_output_split.json", 10);
        List<JsonNode> json = listString2Json(lines);
        json.forEach(System.out::println);
    }



}
