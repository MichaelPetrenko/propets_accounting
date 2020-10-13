package telran.accounting.api;

public class RegistrationDto {

	public String name;
	public String email;
	public String password;

	public RegistrationDto(String name, String email, String password) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public RegistrationDto() {
		super();
	}

}