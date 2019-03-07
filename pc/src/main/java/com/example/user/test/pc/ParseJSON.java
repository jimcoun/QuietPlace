package com.example.user.test.pc;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class ParseJSON {

    // private


    public String toJSON(Object obj){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try {

            // Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(obj);
            return jsonInString;

        } catch (JsonGenerationException e) {
            e.printStackTrace();
            return "Error";
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return "Error";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public Object fromJSON(String jsonString, Class classType) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // try {

            return mapper.readValue(jsonString, classType); // Returns object created from JSON

        /*
        } catch (JsonGenerationException e) {
            e.printStackTrace();
            return new classType.getConstructor();
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return new Object();
        } catch (IOException e) {
            e.printStackTrace();
            return new Object();
        }
        */

    }

    public ProtocolConfig fromJSON(String jsonString){
        try {
            return (ProtocolConfig) fromJSON(jsonString, ProtocolConfig.class);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ProtocolConfig();

        }
}


}
