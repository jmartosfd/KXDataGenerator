package telconomics.rdg.daos;

import telconomics.rdg.model.Customer;

import java.util.List;

public interface CustomersDAOInterface {

    void saveCustomer(Customer customer);

    void batchSaveCustomers(List<Customer> customers);

    List<Customer> batchReadCustomers();


}
