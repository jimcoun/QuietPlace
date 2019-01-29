package messages.example.user.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class Message {

    public String toJSON(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try {
            // Convert object to JSON string
            return mapper.writeValueAsString(this);

        } catch (JsonGenerationException e) {
            return e.toString();
        } catch (JsonMappingException e) {
            return e.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }

    public Message fromJSON(String json){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try {

            Message message1 = mapper.readValue(json, Message.class); // Returns object created from JSON
            return message1;

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
