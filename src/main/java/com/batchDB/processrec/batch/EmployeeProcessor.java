package com.batchDB.processrec.batch;

import com.batchDB.processrec.model.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {
    @Override
    public Employee process(Employee item) throws Exception {
        if (item.getDateofbirth() != null) {
            String itemDateofbirth = item.getDateofbirth();
            DateFormat inputFormat = new SimpleDateFormat("MMddyyyy");
            SimpleDateFormat newDateFormat = new SimpleDateFormat("MM-dd-yyyy");
            item.setDateofbirth(newDateFormat.format(inputFormat.parse(itemDateofbirth)).toString());
        }
        return item;
    }
}
