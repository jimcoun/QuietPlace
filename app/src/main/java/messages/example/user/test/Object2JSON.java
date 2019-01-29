package messages.example.user.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Object2JSON {

    public static void main(String[] args) {
        Object2JSON obj = new Object2JSON();
        obj.run();
    }

    private void run() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        Staff staff = createDummyObject();

        try {
            // Convert object to JSON string and save into a file directly
            mapper.writeValue(new File("D:\\staff.json"), staff);

            // Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(staff);
            System.out.println(jsonInString);

            // Convert object to JSON string and pretty print
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(staff);
            System.out.println(jsonInString);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Staff createDummyObject() {

        Staff staff = new Staff();

        staff.setName("mkyong");
        staff.setAge(33);
        staff.setPosition("Developer");
        staff.setSalary(1501.23);

        List<String> skills = new ArrayList<>();
        skills.add("java");
        skills.add("python");

        staff.setSkills(skills);


        return staff;

    }

}