package com.batchDB.processrec.repository;

import com.batchDB.processrec.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {

}
