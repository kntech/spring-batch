package com.batchDB.processrec.batch;

import com.batchDB.processrec.repository.EmployeeRepository;
import com.batchDB.processrec.model.Employee;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeWriter implements ItemWriter<Employee> {
    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public void write(Chunk<? extends Employee> chunk) throws Exception {
        employeeRepository.saveAll(chunk);
    }
}