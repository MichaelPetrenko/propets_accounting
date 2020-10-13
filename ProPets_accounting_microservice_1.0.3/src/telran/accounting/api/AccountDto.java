package telran.accounting.api;

import java.util.HashSet;
import telran.accounting.domain.entities.AccountingRoles;

public class AccountDto {
	
		public String email;
		public String name;
		public String avatar;
		public String phone;
		public String fblink;
		public HashSet<String> services;
		public HashSet<String> activities;
		public HashSet<String> favorites;
		public HashSet<AccountingRoles> roles;
		public String tokenPass;
		
		public AccountDto(String email, String name, String avatar, String phone, String fblink,
				HashSet<String> services, HashSet<String> activities, HashSet<String> favorites,
				HashSet<AccountingRoles> roles, String tokenPass) {
			super();
			this.email = email;
			this.name = name;
			this.avatar = avatar;
			this.phone = phone;
			this.fblink = fblink;
			this.services = services;
			this.activities = activities;
			this.favorites = favorites;
			this.roles = roles;
			this.tokenPass = tokenPass;
		}

		public AccountDto() {
			super();
		}
		
}
