package messages.example.user.test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Obj2Json {

    public static void main(String[] args) {
        Obj2Json obj = new Obj2Json();
        obj.run();
    }

    private void run() {
        ObjectMapper mapper = new ObjectMapper();

        SampleObject sobj = new SampleObject("jimcoun@gmail.com", "Athens", "28jd8nuyq",
                382930, "SecretKey");

        try {
            // Convert object to JSON string and save into a file directly
            mapper.writeValue(new File("./sampleobject.json"), sobj);

            // Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(sobj);
            System.out.println(jsonInString);

            // Convert object to JSON string and pretty print
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sobj);
            System.out.println(jsonInString);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
