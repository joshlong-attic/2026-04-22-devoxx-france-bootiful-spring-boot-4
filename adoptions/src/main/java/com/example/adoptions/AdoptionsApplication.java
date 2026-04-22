package com.example.adoptions;

import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.Map;

//@Import(MyBeanRegistrar.class)
@EnableResilientMethods
@SpringBootApplication
public class AdoptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdoptionsApplication.class, args);
    }

}

@Controller
@ResponseBody
class MeController {

    @GetMapping("/moi")
    Map<String, String> me(Principal principal) {
        return Map.of("name", principal.getName());
    }
}

@EnableMultiFactorAuthentication(
        authorities = {
                FactorGrantedAuthority.OTT_AUTHORITY,
                FactorGrantedAuthority.PASSWORD_AUTHORITY
        }
)
@Configuration
class SecurityConfiguration {

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        var u = new JdbcUserDetailsManager(dataSource);
        u.setEnableUpdatePassword(true);
        return u;
    }

    @Bean
    Customizer<HttpSecurity> securityCustomizer() {
        return http -> http
                .webAuthn(a -> a
                        .rpName("devoxxfr")
                        .rpId("localhost")
                        .allowedOrigins("http://localhost:8080")
                )
                .oneTimeTokenLogin(httpSecurityOneTimeTokenLoginConfigurer -> httpSecurityOneTimeTokenLoginConfigurer.tokenGenerationSuccessHandler(
                        (request, response, oneTimeToken) -> {

                            // send a message out of band
                            response.getWriter().println("you've got console mail!");
                            response.setContentType(MediaType.TEXT_PLAIN_VALUE);

                            IO.println("please go to http://localhost:8080/login/ott?token=" +
                                    oneTimeToken.getTokenValue());

                        }
                ));
    }
}


class MyBeanRegistrar implements BeanRegistrar {

    @Override
    public void register(BeanRegistry registry, Environment env) {

        for (var i = 0; i < 5; i++) {
            var nom = "tout le monde #" + i;
            registry.registerBean(MyRunner.class,
                    spec -> spec.supplier(_ -> new MyRunner(nom)));
        }
    }
}

@Configuration
class MyConfig {

    //    @Bean
//    MyRunner runner() {
//        return new MyRunner();
//    }
}


class MyRunner implements ApplicationRunner {

    private final String nom;

    MyRunner(String nom) {
        this.nom = nom;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        IO.println("salut " + this.nom + "!");
    }
}

// implicit <- automatic
// explicit <- you tell us everything manually

