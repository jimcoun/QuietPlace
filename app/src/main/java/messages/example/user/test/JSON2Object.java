package messages.example.user.test;

import com.example.user.test.Staff;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JSON2Object {

    public static void main(String[] args) {
        JSON2Object obj = new JSON2Object();
        obj.run();
    }

    private void run() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try {

            // Convert JSON string from file to Object
//            Staff staff = mapper.readValue(new File("D:\\staff.json"), Staff.class);
//            System.out.println(staff);

            // Convert JSON string to Object
            String jsonInString = "{\"name\":\"mkyong\",\"salary\":1501.23,\"skills\":[\"java\",\"python\"]}";

            Staff staff1 = mapper.readValue(jsonInString, Staff.class); // Returns object created from JSON
            System.out.println(staff1);

            //Pretty print
            String prettyStaff1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(staff1);
            System.out.println(prettyStaff1);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
