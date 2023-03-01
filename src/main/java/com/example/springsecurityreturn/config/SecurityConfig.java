package com.example.springsecurityreturn.config;

import com.example.springsecurityreturn.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity //указывает на то, что конфигурационный класс SpringSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PersonDetailsService personDetailsService;
    private final String[] allowedPages = new String[] {
            "/auth/login",
            "/error",
            "/auth/registration",
            "/css/**"
    };

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    //настройка формы для логина
    @Override //переопределяется из WebSecurityConfigurerAdapter
    protected void configure(HttpSecurity http) throws Exception {
        //конфигурация страницы входа, выхода, ошибки и т.д.
        //конфигурация авторизации (доступ по роли к страницам)
        //работает с http
        http
                //попытка отправки злоумышленииком формы, для каких-то злоумышленных
                //дел, доджится токеном на каждой thymeleaf странице
                /*.csrf().disable()*/ //отключение защиты от межсайтовой подделки запросов

                .authorizeHttpRequests()
                //страницы доступные админу
                //"ADMIN_ROLE" понимается как "ADMIN" автоматически SpSec
                //возможна работа не с ролью, а с Authorities
                //list of actions which user can do
                .antMatchers("/admin").hasRole("ADMIN")
                //страницы доступные всем
                .antMatchers(allowedPages).permitAll()
                //для получения остальных страниц и пользователем и админом
                .anyRequest().hasAnyRole("USER", "ADMIN")
                //остальные запросы недоступны
                /*.anyRequest().authenticated()*/ //при отсутствии admin ограничений
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
                .failureForwardUrl("/auth/login?error")
                .and()
                //удаление пользователя из сессии, удаление кукиз у пользователя
                .logout().logoutUrl("/logout")
                //redirect to this page after logout
                .logoutSuccessUrl("/auth/login");

    }

    //настраивает логику аутентификации
    //даем понять SpringSecurity что для аутентификации используется
    //именно этот AuthProviderImpl
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personDetailsService)//упрощение, есть другая версия
                //прогоняет пароль через BCryptPasswordEncoder
                //при аутентификации
                .passwordEncoder(getPasswordEncoder());
    }

    @Bean //возвращается используемый алгоритм шифрования
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
