import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        List<Employee> employees;
        String fileNameCsv = "data.csv";
        String fileNameXml = "data.xml";
        String fileNameJsonCsv = "data.json";
        String fileNameJsonXml = "data2.json";
        String json;
        //Первое задание
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        employees = parseCSV(columnMapping, fileNameCsv);
        json = listToJson(employees);
        writeString(json, fileNameJsonCsv);
        System.out.println(json);
        employees.clear();
        //Второе задание
        System.out.println("---------------------");

        employees = parseXML("data.xml");
        employees.forEach(System.out::println);
        json = listToJson(employees);
        writeString(json, fileNameJsonXml);
        System.out.println(json);
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
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return  gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) throws IOException {
        File file = new File(fileName);
        if (file.createNewFile()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
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
        System.out.println(root.getNodeName());
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
