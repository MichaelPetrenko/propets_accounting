package telran.accounting.api;

public class Address {
	
	public String country;
	public String city;
	public String street;
	public String building;
	
	public Address(String country, String city, String street, String building) {
		super();
		this.country = country;
		this.city = city;
		this.street = street;
		this.building = building;
	}

	public Address() {
		super();
	}

	@Override
	public String toString() {
		return street.replace(" ", "+")+","+building.replace(" ", "+")+","+city.replace(" ", "+")+","+country.replace(" ", "+");
	}

}
