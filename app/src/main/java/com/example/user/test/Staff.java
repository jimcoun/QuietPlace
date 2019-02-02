package com.example.user.test;

import java.util.List;

public class Staff {

    private String name;
    private int age;
    private String position;
    private double salary;
    private List<String> skills;

    public void setName(String name){
        this.name = name;
    }

    public void setAge(int age){
        this.age = age;
    }

    public void setPosition(String position){
        this.position = position;
    }

    public void setSalary(double salary){
        this.salary = salary;
    }

    public void setSkills(List<String> skills){
        this.skills = skills;
    }

    @Override
    public String toString(){
        return String.format("Name: %s%nAge: %d%nPosition: %s%nSalary: %.2f%nSkills: %s%n", name, age, position, salary, skills);
    }

}
