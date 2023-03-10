package com.gestion.empleados;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //está en el IOC container
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        
        UserDetails usuario1 = User
            .withUsername("pepelu")
            .password("$2a$10$noVooP90E1D7Zt1PRCjblu3QT24aPfsuxNTutnYDvwoRQARzfFba6")
            .roles("USERNAME")
            .build();
        UserDetails usuario2 = User
            .withUsername("hgc88")
            .password("$2a$10$noVooP90E1D7Zt1PRCjblu3QT24aPfsuxNTutnYDvwoRQARzfFba6")
            .roles("ADMIN")
            .build();

            return new InMemoryUserDetailsManager(usuario1, usuario2);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers("/").permitAll()
		    .antMatchers("/form/*","/eliminar/*").hasRole("ADMIN")
		    .anyRequest().authenticated()
		    .and()
		    .formLogin()
		        .loginPage("/login")
		        .permitAll()
		    .and()
		    .logout().permitAll();    
    }
}
