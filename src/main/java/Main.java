import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        List<Employee> employees;
        String dir = "src/main/resources/";
        String fileNameCsv = dir + "data.csv";
        String fileNameXml = dir + "data.xml";
        String fileNameJsonCsv = dir + "data.json";
        String fileNameJsonXml = dir + "data2.json";
        String fileNameJson = dir + "new_data.json";
        String json;
        //Первое задание
        System.out.println("Первое задание");
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        employees = parseCSV(columnMapping, fileNameCsv);
        json = listToJson(employees);
        writeString(json, fileNameJsonCsv);
        employees.clear();
        System.out.println("---------------------");

        //Второе задание
        System.out.println("Второе задание");
        employees = parseXML(fileNameXml);
        json = listToJson(employees);
        writeString(json, fileNameJsonXml);
        System.out.println("---------------------");

        //Третье задание
        System.out.println("Третье задание");
        String jsonString = readString(fileNameJson);
        List<Employee> list = jsonToList(jsonString);
        list.forEach(System.out::println);
    }

    private static List<Employee> jsonToList(String jsonString) {
        List<Employee> employees = new ArrayList<>();
        JSONParser parser = new JSONParser();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            Object obj = parser.parse(jsonString);
            JSONArray jsons = (JSONArray) obj;
            for (Object json : jsons) {
                Employee employee = gson.fromJson(json.toString(), Employee.class);
                employees.add(employee);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private static String readString(String fileName) throws FileNotFoundException {
        String line;
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                result.append(line + "\n");
            }
            reader.readLine();
        } catch (Exception e) {
            e.getMessage();
        }
        return result.toString();
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) throws IOException {
        File file = new File(fileName);
        if (file.exists())
            file.delete();

        if (file.createNewFile()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
                writer.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        return read(root);
    }

    private static List<Employee> read(Node node) {
        List<Employee> employees = new ArrayList<>();
        List<String> attributesText = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType() &&
                    "employee".equals(node_.getNodeName())) {
                NodeList nodeEmployees = node_.getChildNodes();
                for (int a = 0; a < nodeEmployees.getLength(); a++) {
                    Node nodeEmployee = nodeEmployees.item(a);
                    if (Node.ELEMENT_NODE == nodeEmployee.getNodeType()) {
                        attributesText.add(nodeEmployee.getTextContent());
                    }
                }
                employees.add(
                        new Employee(
                                Long.parseLong(attributesText.get(0)),
                                attributesText.get(1),
                                attributesText.get(2),
                                attributesText.get(3),
                                Integer.parseInt(attributesText.get(4))
                        )
                );
                attributesText.clear();
            }
            read(node_);
        }
        return employees;
    }
}
