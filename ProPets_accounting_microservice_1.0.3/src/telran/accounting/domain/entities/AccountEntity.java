package telran.accounting.domain.entities;

import java.util.HashSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import telran.accounting.domain.entities.AccountingRoles;

@Document(collection = "accounts")
public class AccountEntity {
	
	@Id
	String email;
	String name;
	String avatar;
	String phone;
	String fblink;
	HashSet<String> services;
	Activ activities;
	Activ favorites;
	HashSet<AccountingRoles> roles;
	String password;
	boolean revoked;
	
	public AccountEntity(String email, String name, String tokenPass) {
		super();
		this.email = email;
		this.name = name;
		this.password = tokenPass;
		this.services = new HashSet<String>();
		this.activities = new Activ();
		this.favorites = new Activ();
		this.roles = new HashSet<AccountingRoles>();	
		this.roles.add(AccountingRoles.USER);
		this.avatar = "http://gravatar.com/avatar/0?d=mp";
		this.phone = "";
		this.fblink = "";
		this.revoked = false;
	}
	
	public AccountEntity() {}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getPhone() {
		return phone;
	}

	public String getFblink() {
		return fblink;
	}

	public HashSet<String> getServices() {
		return services;
	}

	public Activ getActivities() {
		return activities;
	}

	public Activ getFavorites() {
		return favorites;
	}

	public HashSet<AccountingRoles> getRoles() {
		return roles;
	}

	public String getPass() {
		return password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setFblink(String fblink) {
		this.fblink = fblink;
	}

	public void setServices(HashSet<String> services) {
		this.services = services;
	}

	public void setActivities(Activ activ) {
		this.activities = activ;
	}

	public void setFavorites(Activ activ) {
		this.favorites = activ;
	}

	public void setRoles(HashSet<AccountingRoles> roles) {
		this.roles = roles;
	}

	public void setPass(String tokenPass) {
		this.password = tokenPass;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}
}
