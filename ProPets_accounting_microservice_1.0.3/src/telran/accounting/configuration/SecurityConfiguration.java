package telran.accounting.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//	Этот класс нужен для создания бина для Security.

@Configuration
public class SecurityConfiguration {
	
	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
		// Самый современный алгоритм кодировки, работает на базе Base64.
		// Метод matches позволяет сравнить два хеш-кода.
	}
}
