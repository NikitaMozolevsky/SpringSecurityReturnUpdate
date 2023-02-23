package com.example.springsecurityreturn.config;

import com.example.springsecurityreturn.services.PersonDetailsService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity //указывает на то, что конфигурационный класс SpringSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PersonDetailsService personDetailsService;
    private final String[] allowedPages = new String[] {
            "/auth/login",
            "/error",
            "/auth/registration"
    };

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    //настраивает логику аутентификации
    //даем понять SpringSecurity что для аутентификации используется
    //именно этот AuthProviderImpl
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personDetailsService);//упрощение
    }

    //настройка формы для логина
    @Override //переопределяется из WebSecurityConfigurerAdapter
    protected void configure(HttpSecurity http) throws Exception {
        //конфигурация страницы входа, выхода, ошибки и т.д.
        //конфигурация авторизации (доступ по роли к страницам)
        //работает с http
        http
                .csrf().disable() //че-то с токеном
                .authorizeHttpRequests()
                //страницы доступные всем
                .antMatchers(allowedPages).permitAll()
                //остальные запросы недоступны
                .anyRequest().authenticated()
                .and() //and - объединитель разных настроек, настройка авторизации
                .formLogin()
                .loginPage("/auth/login") //метод захода в систему\
                //SpringSecurity ожидает что сюда придут логин и пароль
                //SpringSecurity сам обрабатывает данные
                .loginProcessingUrl("/process_login")
                //что происходит при успешной аутентификации
                //перенаправление на /hello, true - всегда
                .defaultSuccessUrl("/hello", true)
                //unsuccessful with key error (located in view (th) show message)
                .failureForwardUrl("/auth/login?error");

    }

    @Bean //возвращается используемый алгоритм шифрования
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
