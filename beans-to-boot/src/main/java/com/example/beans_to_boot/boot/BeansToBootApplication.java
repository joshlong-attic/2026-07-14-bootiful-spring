package com.example.beans_to_boot.boot;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;


// COMPILATION
// 0 ingest: xml, java config, component scanning, BeanRegistrar
// -1 BeanDefinitions
// > BeanFactoryInitializationAotProcessor
// RUNTIME
// 0 ingest: xml, java config, component scanning, BeanRegistrar
// BeanDefinition
// > BeanFactoryPostProcessor
// beans
// > BeanPostProcessor
//  >> beforeInit
// > setters
// > InitializingBean / @PostConstruct
//  >> afterInit
// > destroy

@Component
class Cart implements Serializable {
}

@ImportRuntimeHints(BeansToBootApplication.MyRuntimeHintsRegistrar.class)
@SpringBootApplication
public class BeansToBootApplication {

    @Bean
    static MyBeanFactoryInitializationAotProcessor myBeanFactoryInitializationAotProcessor() {
        return new MyBeanFactoryInitializationAotProcessor();
    }

    static class MyBeanFactoryInitializationAotProcessor implements
            BeanFactoryInitializationAotProcessor {

        @Override
        public @Nullable BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
            var serializable = new ArrayList<TypeReference>();
            for (var beanName : beanFactory.getBeanDefinitionNames()) {
                var beanClass = beanFactory.getType(beanName);
                // ln -s link source
                if (Serializable.class.isAssignableFrom(beanClass) ||
                        beanClass.isAssignableFrom(Serializable.class)
                ) {
                    serializable.add(TypeReference.of(beanClass));
                }
            }

            return (generationContext,
                    code) -> {
                var runtimeHints = generationContext.getRuntimeHints();
                for (var s : serializable) {
                    IO.println("registering serialization hint for " + s);
                    runtimeHints.serialization().registerType(s);
                }

                code.getMethods()
                        .add("helloUberconf",
                                m -> m.addStatement("""
                                        IO.println("hello Uberconf");
                                        """));
            };
        }
    }

    static class MyRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
            hints.serialization().registerType(Cart.class);
            hints.resources().registerResource(MESSAGE);
//            hints.reflection()
//                    .registerType(Cart.class);
        }
    }


    static final Resource MESSAGE = new ClassPathResource("message");

    @Bean
    ApplicationRunner message() {
        return _ -> IO.println(MESSAGE.getContentAsString(Charset.defaultCharset()));
    }


    public static void main(String[] args) {
        SpringApplication.run(BeansToBootApplication.class, args);
    }

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    JdbcPostgresDialect jdbcPostgresDialect() {
        return JdbcPostgresDialect.INSTANCE;
    }

    @Bean
    ApplicationRunner dogApplicationRunner(DogRepository dogRepository) {
        return _ -> test(dogRepository);
    }

    // Project Leyden

    static void test(DogRepository repository) {
        repository.findAll().forEach(IO::println);
    }

    // resources
    // reflection
    // jdk proxies
    // cglib proxies
    // serialization
    // jni
}

@Controller
@ResponseBody
class DogsController {

    private final DogRepository repository;

    DogsController(DogRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    Collection<Dog> index(Principal principal) {
        return repository.findByOwner(principal.getName());
    }
}

interface DogRepository extends ListCrudRepository<Dog, Integer> {

    Collection<Dog> findByOwner(String name);
}

record Dog(@Id int id, String name, String owner) {
}











/*
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Conditional(MyCondition.class)
class MySadBean {

    private final UUID uuid;

    MySadBean() {
        IO.println("sad bean");
        this.uuid = UUID.randomUUID();
    }

    public void doSomething() {
        IO.println("doing something new " + uuid);
    }
}

class MyCondition implements Condition {

    private final Instant start = Instant.now();
    private final Instant end = start.plusSeconds(30);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var shouldCreateBean = !Instant.now().isAfter(end);
        IO.println("condition " + shouldCreateBean);
        return shouldCreateBean;
    }
}

@EnableScheduling
@Component
class BeanPoker {

    private MySadBean mySadBean(ApplicationContext applicationContext) {
        return applicationContext.getBean(MySadBean.class);
    }

    BeanPoker(TaskScheduler executor, ApplicationContext applicationContext) {
        executor.scheduleAtFixedRate(
                () -> mySadBean(applicationContext).doSomething(), Duration.ofSeconds(3));
    }
}
*/