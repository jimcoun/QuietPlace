package com.example.user.test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonToObject {

    public static void main(String[] args) {
        JsonToObject obj = new JsonToObject();
        // obj.run();
        obj.test();
    }

    private void run() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        ParseJSON parser = new ParseJSON();
        try {

            // Convert JSON string to Object
            String jsonInString = "{\"name\":\"mkyong\",\"salary\":7500,\"skills\":[\"java\",\"python\"]}";
            Staff staff1 = mapper.readValue(jsonInString, Staff.class);
            System.out.println(staff1);

            //Pretty print
            String prettyStaff1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(staff1);
            System.out.println(prettyStaff1);

            try {
                Staff staff2 = (Staff) parser.fromJSON(jsonInString, Staff.class);
                System.out.println(staff1);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void test(){
        ParseJSON parser = new ParseJSON();

        Staff staffobj = new Staff();
        staffobj.setName("Dimitris");
        staffobj.setAge(19);
        staffobj.setPosition("Lol");
        staffobj.setSalary(65.87);

        String staffString = parser.toJSON(staffobj);
        System.out.println(staffString);
    }

}