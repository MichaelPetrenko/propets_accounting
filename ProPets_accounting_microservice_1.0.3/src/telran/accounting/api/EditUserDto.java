package telran.accounting.api;

public class EditUserDto {

	public String name;
	public String avatar;
	public String phone;

	public EditUserDto() {
		super();
	}

	public EditUserDto(String name, String avatar, String phone) {
		super();
		this.name = name;
		this.avatar = avatar;
		this.phone = phone;
	}
}