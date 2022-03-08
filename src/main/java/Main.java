import dto.Student;
import dto.Subject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (!checkCorrectArgumainAndPrintThem(args)) System.exit(1);

        Document document = getDocument(args[0]);
        NodeList list = document.getElementsByTagName("student");

        Document newDocument = getNewDocument();
        List<Student> studentList = getStudentsFromXML(list);

        Document newBildedDocument = createXMLStructure(list, newDocument, studentList);
        writeXMLtoFile(args[1], newBildedDocument);
    }

    private static boolean checkCorrectArgumainAndPrintThem(String[] args) {
        if (args.length == 2) {
            Arrays.stream(args).forEach(System.out::println);
            return true;
        }
        return false;
    }

    private static Document getDocument(String arg) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder builder = null;

            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(arg));

            document.getDocumentElement().normalize();

            return document;
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Student> getStudentsFromXML(NodeList list) {
        List<Student> studentList = new ArrayList<>();

        for (int i = 0; i < list.getLength(); i++) {
            Node studentNode = list.item(i);

            String firstName = studentNode.getAttributes().getNamedItem("firstName").getNodeValue();
            String secondName = studentNode.getAttributes().getNamedItem("lastName").getNodeValue();
            int average = Integer.parseInt(studentNode.getAttributes().getNamedItem("average").getNodeValue());

            Element element = (Element) studentNode;
            NodeList subjectList =  element.getElementsByTagName("subject");

            List<Subject> studentSubjectList =  getStudentSubjectList(subjectList);

            studentList.add(correctStudentsData(firstName, secondName, average, studentSubjectList));
        }

        return studentList;
    }

    private static  List<Subject> getStudentSubjectList(NodeList subjectList) {
        List<Subject> studentSubjectList = new ArrayList<>();

        for (int j = 0; j < subjectList.getLength(); j++) {
            String title = subjectList.item(j).getAttributes().getNamedItem("title").getNodeValue();
            int mark = Integer.parseInt(subjectList.item(j).getAttributes().getNamedItem("mark").getNodeValue());

            studentSubjectList.add( new Subject(title, mark));
        }

        return studentSubjectList;
    }

    private static Student correctStudentsData(String fistname, String secondName, int average, List<Subject> studentSubjectList) {
        Student studentFromXML = new Student(fistname, secondName, studentSubjectList, average);
        double correctAverage = studentFromXML.getCorrectAverage(studentFromXML);
        if (studentFromXML.getAverage() != correctAverage)
            studentFromXML.setAverage(correctAverage);

        return studentFromXML;
    }

    private static Document getNewDocument() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return docBuilder.newDocument();
    }

    private static Document createXMLStructure(NodeList list, Document newDocument, List<Student> studentList) {
        Element studentsElement = newDocument.createElement("students");

        studentList.forEach(student -> {
            Element studentElement = newDocument.createElement("student");

            studentElement.setAttribute("firstName", student.getFirstName());
            studentElement.setAttribute("lastName", student.getLastName());
            studentElement.setAttribute("average", String.valueOf(student.getAverage()));

            Element subjectListElement = newDocument.createElement("subjectList");

            student.getSubjectList().forEach(subject -> {
                Element subjectElement = newDocument.createElement("subject");
                subjectElement.setAttribute("title", subject.getTitle());
                subjectElement.setAttribute("mark", String.valueOf(subject.getMark()));

                subjectListElement.appendChild(subjectElement);
            });

            studentElement.appendChild(subjectListElement);
            studentsElement.appendChild(studentElement);
        });

        newDocument.appendChild(studentsElement);
        return newDocument;
    }

    private static void writeXMLtoFile(String arg, Document newDocument) {
        TransformerFactory transformerFactory =  TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(newDocument);

        StreamResult result =  new StreamResult(new File(arg));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    }
}
