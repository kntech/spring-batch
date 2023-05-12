package com.batchDB.processrec.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "employee")
public class Employee {

    @Id
    private String id;
    private String name;
    private String address;
    private String dateofbirth;
    private List<String> hobbies;

    public Employee(String id, String name, String address, String dateofbirth, List<String> hobbies) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.dateofbirth = dateofbirth;
        this.hobbies = hobbies;
    }

    public Employee() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }
}
