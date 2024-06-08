package org.jgayoso.ncomplo;


import jakarta.servlet.Filter;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan
@EnableAutoConfiguration
@EnableJpaRepositories
@EnableScheduling
@EnableGlobalMethodSecurity(securedEnabled = true)
public class Application implements WebMvcConfigurer {

    @Bean
    public ConfigurablePasswordEncryptor configurablePasswordEncryptor() {
        return new ConfigurablePasswordEncryptor();
    }

    @Bean
    public Filter characterEncodingFilter() {
        final CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }


    public static void main(final String[] args) {

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }
        System.setProperty("server.port", webPort);

        SpringApplication.run(Application.class, args);
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public SecurityFilterChain authenticationSecurity(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
            http.authenticationProvider(authenticationProvider);
            return http.build();
        }

        @Bean
        @Order(Ordered.LOWEST_PRECEDENCE - 8)
        public SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/admin/**").authenticated()
                            .requestMatchers("/admin/**").hasAuthority("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/resetpassword*").permitAll()
                            .requestMatchers(HttpMethod.GET, "/forgot-password*").permitAll()
                            .requestMatchers(HttpMethod.POST, "/forgot-password*").permitAll()
                            .requestMatchers("/joinLeague*").permitAll()
                            .requestMatchers("/invitation*").permitAll()
                            .requestMatchers("/register").permitAll()
                            .requestMatchers("/login").permitAll()
                            .requestMatchers("/css/ncomplo.css").permitAll()
                            .requestMatchers("/js/ncomplo.js").permitAll()
                            .requestMatchers("/*").fullyAuthenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .failureUrl("/login?error")
                    )
                    .logout(logout -> logout
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    )
                    .exceptionHandling(exception -> exception
                            .accessDeniedPage("/login?error")
                    );
            return http.build();
        }


        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
        }

        @Bean
        public UserDetailsService userDetailsService() {
            InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
            manager.createUser(User.withUsername("user").password("{noop}password").roles("USER").build());
            return manager;
        }
    }


}
