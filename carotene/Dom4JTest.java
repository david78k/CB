import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class Dom4JTest {

    // DATA SET ORDERED BY INSTITUTE, COURSE (COULD BE THE ID)

    // 3 Asia Pacific Institute Of Mangement 167 1136 Post Graduate Diploma in Management
    // (International Business) New Delhi Delhi India
    // 4 Asia Pacific Institute Of Mangement 167 1138 Post Graduate Diploma in Management (Banking &
    // Financial Services) New Delhi Delhi India
    // 5 University of Delhi 182 4283 Bachelor of Business Studies New Delhi Delhi India

    private static List<String[]> data = new ArrayList<String[]>();
    static {
        data.add(new String[] { "3", "Asia Pacific Institute Of Mangement", "167", "1136",
                "Post Graduate Diploma in Management (International Business)", "New Delhi", "Delhi", "India" });
        data.add(new String[] { "4", "Asia Pacific Institute Of Mangement", "167", "1138",
                "Post Graduate Diploma in Management (Banking & Financial Services)", "New Delhi", "Delhi", "India" });
        data.add(new String[] { "5", "University of Delhi", "182", "4283", "Bachelor of Business Studies", "New Delhi", "Delhi", "India" });

    }

    public static void main(String[] args) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("institute");

        String id = null;
        Element coursesElement = null;

            // here the iteration over dataset, of course the result will not be just string but the idea will be the same...
        for (String[] strs : data) {
            if (id == null || !id.equals(strs[2])) {
                id = strs[2];
                root.addElement("id").addText(id);
                root.addElement("name").addText(strs[1]);
                coursesElement = root.addElement("courses");
            }
            Element course = coursesElement.addElement("course");
            course.addElement("id").setText(strs[3]);
            course.addElement("name").setText(strs[4]);
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(System.out, format);
        writer.write(document);
    }

}

