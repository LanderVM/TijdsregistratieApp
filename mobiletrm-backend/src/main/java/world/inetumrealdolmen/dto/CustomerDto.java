package world.inetumrealdolmen.dto;

import world.inetumrealdolmen.domain.Customer;

/**
 * A collection of Data Transfer Objects (DTO) for {@link Customer} entities to be sent to clients.
 * Use the static subclasses' constructors to create DTO's.
 */
public class CustomerDto {

  /**
   * Represents an index view of a customer, containing information for a project's details.
   */
  public static class Index {

    /**
     * The ID of the customer.
     */
    public long id;

    /**
     * The name of the customer.
     */
    public String name;

    /**
     * The name of the company the customer is associated with.
     */
    public String companyName;

    /**
     * Default constructor for Jackson serializing.
     * Protected to prevent instantiation without parameters.
     */
    protected Index() {
    }

    /**
     * Constructor to convert a Customer object into a DTO.
     *
     * @param customer The Customer object to convert.
     */
    public Index(Customer customer) {
      this.id = customer.id;
      this.name = customer.name;
      this.companyName = customer.companyName;
    }
  }
}
