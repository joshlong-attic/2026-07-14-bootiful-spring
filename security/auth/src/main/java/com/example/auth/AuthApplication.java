package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@EnableMultiFactorAuthentication(authorities = {})
@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setEnableUpdatePassword(true);
        return jdbcUserDetailsManager;
    }

//    @Bean
//    Customizer<HttpSecurity> httpSecurityCustomizer() {
//        var amf = AuthorizationManagerFactories.multiFactor()
//                .requireFactors(FactorGrantedAuthority.PASSWORD_AUTHORITY,
//                        FactorGrantedAuthority.OTT_AUTHORITY)
//                .build();
//        return http -> http
//                .oauth2AuthorizationServer(a -> a.oidc(Customizer.withDefaults()))
//                .authorizeHttpRequests(a -> a
//                        .requestMatchers("/admin")
//                        .access(amf.authenticated())
//                )
//                .webAuthn(a -> a
//                        .rpName("uberconf")
//                        .rpId("localhost")
//                        .allowedOrigins("http://localhost:8080")
//                )
//                .oneTimeTokenLogin(a -> a
//                        .tokenGenerationSuccessHandler((request, response, oneTimeToken) -> {
//                                    response.getWriter().println("you've got console mail!");
//                                    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
//                                    IO.println("please go to http://localhost:8080/login/ott?token=" +
//                                            oneTimeToken.getTokenValue());
////                            var successHandler = new RedirectOneTimeTokenGenerationSuccessHandler("/token");
////                            successHandler.handle(request, response, oneTimeToken);
//                                }
//                        ));
//    }

//   @Bean
//    SecurityFilterChain securityFilterChain (HttpSecurity security) {
//        return security
//                .authorizeHttpRequests(a ->a.anyRequest().authenticated() )
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults())
//                .csrf(Customizer.withDefaults())
//                .build() ;
//    }


}
//
//
//@Controller
//@ResponseBody
//class MeController {
/// /
/// /    @GetMapping("/admin")
/// /    Map<String, String> admin(Principal principal) {
/// /        return Map.of("admin", principal.getName());
/// /    }
//
//    @GetMapping("/")
//    Map<String, String> me(Principal principal) {
//        return Map.of("name", principal.getName());
//    }
//}

// authentication (who is making the request)
// authorization (what the request is trying to do and do they have rights to do it)