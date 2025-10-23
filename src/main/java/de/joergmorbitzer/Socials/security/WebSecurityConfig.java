package de.joergmorbitzer.Socials.security;

import de.joergmorbitzer.Socials.entities.SocialUser;
import de.joergmorbitzer.Socials.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Bean
    protected PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    protected AuthenticationManager authManager()
    {
        DaoAuthenticationProvider provi = new DaoAuthenticationProvider();
        provi.setUserDetailsService(userDetailsService);
        provi.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provi);
    }

    @Configuration
    @Order(1)
    public static class Config1Adapter
    {

    }

    AntPathRequestMatcher startseite = new AntPathRequestMatcher("/");
    AntPathRequestMatcher join = new AntPathRequestMatcher("/join");
    AntPathRequestMatcher login = new AntPathRequestMatcher("/loginnow");
    AntPathRequestMatcher restauth = new AntPathRequestMatcher("/api/authenticate");
    AntPathRequestMatcher controlauth = new AntPathRequestMatcher("/api/control");
    AntPathRequestMatcher subjauth = new AntPathRequestMatcher("/api/subject");
    AntPathRequestMatcher gruppen = new AntPathRequestMatcher("/groups/**");
    AntPathRequestMatcher backend = new AntPathRequestMatcher("/backend/**");
    AntPathRequestMatcher benutzer = new AntPathRequestMatcher("/users");
    AntPathRequestMatcher blogs = new AntPathRequestMatcher("/blogs");
    AntPathRequestMatcher neuegruppe = new AntPathRequestMatcher("/groups/new-group");
    AntPathRequestMatcher bootstrapcss = new AntPathRequestMatcher("/css/bootstrap.css");
    AntPathRequestMatcher bootstrapmap = new AntPathRequestMatcher("/css/bootstrap.css.map");
    AntPathRequestMatcher socialcss = new AntPathRequestMatcher("/css/social.css");
    AntPathRequestMatcher images = new AntPathRequestMatcher("/images/**");
    AntPathRequestMatcher h2console = new AntPathRequestMatcher("/h2-console/**");

    @Bean
    public SecurityFilterChain secChain(HttpSecurity http) throws Exception
    {
        http
                .authorizeHttpRequests((authy) -> authy
                        .requestMatchers(h2console).permitAll()
                        .requestMatchers(startseite).permitAll()
                        .requestMatchers(join).permitAll()
                        .requestMatchers(login).permitAll()
                        .requestMatchers(restauth).permitAll()
                        .requestMatchers(controlauth).permitAll()
                        .requestMatchers(subjauth).permitAll()
                        .requestMatchers(bootstrapcss).permitAll()
                        .requestMatchers(bootstrapmap).permitAll()
                        .requestMatchers(socialcss).permitAll()
                        .requestMatchers(images).permitAll()
                        .requestMatchers(gruppen).permitAll()
                        .requestMatchers(benutzer).permitAll()
                        .requestMatchers(blogs).permitAll()
                        .requestMatchers(neuegruppe).hasAnyRole("ADMIN", "EXP1")
                        .requestMatchers(backend).hasRole("ADMIN")
                        .anyRequest().authenticated())
//                .formLogin((form) -> form.permitAll().successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        SocialUser user = userRepo.findByUsername(authentication.getName())
//                                        .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden"));
//                        user.setOnline(true);
//                        userRepo.save(user);
//                        System.out.println(request.getRequestURI());
//                        response.sendRedirect(String.valueOf(request.getRequestURL()));
//                    }
//                }))
                .formLogin((form) -> form.loginPage("/loginnow").permitAll().successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        SocialUser user = userRepo.findByUsername(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden"));
                        user.setOnline(true);
                        userRepo.save(user);
                        response.sendRedirect("/users/" + user.getUid());
                    }
                }))
                .logout((logout) -> logout.permitAll().addLogoutHandler(new LogoutHandler() {
                    @Override
                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        SocialUser user = userRepo.findByUsername(authentication.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden"));
                        user.setOnline(false);
                        userRepo.save(user);
                        try {
                            response.sendRedirect(request.getContextPath());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }))
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
