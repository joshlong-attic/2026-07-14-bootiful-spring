package com.example.beans_to_boot.framework;

import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.lang.annotation.*;
import java.util.Collection;

class DogBeanRegistrar implements BeanRegistrar {

    @Override
    public void register(BeanRegistry registry, Environment env) {

//        registry.registerBean(LifecyleThing.class,
//                a -> a.supplier(supplierContext -> {
//                    var lt = new LifecyleThing(supplierContext.bean(JdbcClient.class));
//                    lt.setCount(10);
//                    return lt;
//                }));

        registry.registerBean(DataSourceTransactionManager.class,
                a -> a.supplier(
                        supplierContext -> new DataSourceTransactionManager(supplierContext.bean(DataSource.class))));

        registry.registerBean(TransactionTemplate.class, a -> a
                .supplier(supplierContext -> new TransactionTemplate(supplierContext.bean(PlatformTransactionManager.class))));

        registry.registerBean(JdbcClient.class, i -> i
                .supplier(supplierContext -> JdbcClient.create(supplierContext.bean(DataSource.class))));
    }
}

@ComponentScan
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@Import(DogBeanRegistrar.class)
@Configuration
class DogConfiguration {

    @Bean
    DriverManagerDataSource driverManagerDataSource(
            Environment environment,
            @Value("${spring.datasource.url}") String url) {
        var user = environment.getProperty("spring.datasource.username");
        var pw = environment.getProperty("spring.datasource.password");
        return new DriverManagerDataSource(url, user, pw);
    }

//    @Bean
//    DogRepository dogRepository(JdbcClient jdbcClient) {
//        return new DogRepository(jdbcClient);
//    }
//
}

public class BeansToBootApplication {

    public static void main(String[] args) {
        var applicationContext = new AnnotationConfigApplicationContext(DogConfiguration.class);
        applicationContext.start();
        var txDogRepository = applicationContext.getBean(DogRepository.class);
        test(txDogRepository);
    }

    static void test(DogRepository repository) {
        repository.findAll().forEach(IO::println);
    }
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@interface UberComponent {

    @AliasFor(annotation = Component.class)
    String value() default "";

}

@Repository // stereotype UML
@Transactional
class DogRepository {

    private final JdbcClient jdbcClient;

    DogRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Collection<Dog> findAll() {
        return this.jdbcClient.sql("select * from dog").query((rs, _) -> new Dog(rs.getInt("id"), rs.getString("name"))).list();
    }
}

record Dog(int id, String name) {
}
//
//interface Tx {
//}

// SPRING FRAMEWORK
// - dependency injection
// - portable service abstractions
// - aspect-oriented programming


// spring data
// spring security
// micrometer
// spring integration
// spring batch
// spring framework (resilient methods, transactions, caching, async, ...)

//
//class Transactions {
//
//    static Object transactional(Object target, TransactionTemplate transactionTemplate) {
//        // defines new class in classloader that implements all the interfaces specified
//        // creates a single instance of that class
/// /        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
/// /                (proxy1, method, args) -> doInTransaction(target, transactionTemplate, method, args));
//
//        // cglib
//
//        var pfb = new ProxyFactoryBean();
//        pfb.setTarget(target);
//        pfb.addAdvice((MethodInterceptor) invocation -> doInTransaction(target, transactionTemplate,
//                invocation.getMethod(), invocation.getArguments()));
//        pfb.setProxyTargetClass(true);
//        return pfb.getObject();
//    }
//
//    static Object doInTransaction(Object target,
//                                  TransactionTemplate tt,
//                                  Method method, Object... args) {
//        return tt.execute(status -> {
//            try {
//                IO.println("before " + method.getName() + " wth arguments " + Arrays.toString(args));
//                var result = method.invoke(target, args);
//                IO.println("after " + method.getName() + " wth arguments " + Arrays.toString(args));
//                return result;
//            }//
//            catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }
//}

//
//interface DogRepository {
//    Collection<Dog> findAll();
//}


// TRANSACTIONS
// start tx
// - work 1
// - work 2
// - work 3
// commit tx  || rollback tx


// mongodb
// cassandra
// redis
// neo4j
// jms
// kafka
// rabbitmq
// jdbc (jpa, jdo, hibernate , jooq, mybatis, exposed, jdbi, ...)
// jta (jms, jdbc, cca)

// DRY do not repeat yourself
// SRP single responsibility principle


// transactions
// auditing
// security
// observability
//
//
//class TransactionalDogRepository implements DogRepository {
//
//    private final TransactionTemplate tx;
//    private final DogRepository delegate;
//
//    TransactionalDogRepository(TransactionTemplate tx, DogRepository delegate) {
//        this.tx = tx;
//        this.delegate = delegate;
//    }
//
//    @Override
//    public Collection<Dog> findAll() {
//        return this.tx.execute(_ -> delegate.findAll());
//    }
//}

// composition
// specialization


// ingest: xml, java config, component scanning, BeanRegistrar
// BeanDefinition
// > BeanFactoryPostProcessor
// beans
// > BeanPostProcessor
//  >> beforeInit
// > setters
// > InitializingBean / @PostConstruct
//  >> afterInit


//
//class TxBeanPostProcessor implements BeanPostProcessor {
//
//    private final BeanFactory factory;
//
//    TxBeanPostProcessor(BeanFactory factory) {
//        this.factory = factory;
//    }
//
//    @Override
//    public @Nullable Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//
//        IO.println("inspecting " + beanName + " for Tx");
//
//        if (bean instanceof Tx) {
//            return Transactions.transactional(bean, this.factory.getBean(TransactionTemplate.class));
//        }
//
//        return bean;
//    }
//}
//
//class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//
//        for (var beanName : beanFactory.getBeanDefinitionNames()) {
//            var beanDefinition = beanFactory.getBeanDefinition(beanName);
//            var type = beanFactory.getType(beanName);
//            IO.println("inspecting " + beanDefinition.getBeanClassName()
//                    + " with class " + type + "!");
//        }
//
//    }
//}
//
//class LifecyleThing
//        implements InitializingBean {
//
//    private final JdbcClient dataSource;
//
//    private int count = 10;
//
//    LifecyleThing(JdbcClient dataSource) {
//        Assert.notNull(dataSource, "the db must be set");
//        this.dataSource = dataSource;
//    }
//
//    public void setCount(int count) {
//        this.count = count;
//    }
//
//    @PostConstruct
//    void post() {
//        IO.println("malone");
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        Assert.state(this.count > 0, "count must be positive");
//    }
//}

//    @Bean
//    static MyBeanFactoryPostProcessor myBeanFactoryPostProcessor() {
//        return new MyBeanFactoryPostProcessor();
//    }
//
//    @Bean
//    static TxBeanPostProcessor txBeanPostProcessor(BeanFactory factory) {
//        return new TxBeanPostProcessor(factory);
//    }


// RAII
// resource acquisition is initialization
