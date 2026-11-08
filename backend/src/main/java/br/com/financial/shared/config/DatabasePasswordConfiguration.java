package br.com.financial.shared.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
class DatabasePasswordConfiguration {

  @Bean
  static BeanFactoryPostProcessor validateDatabasePassword(Environment environment) {
    return beanFactory -> {
      String password = environment.getRequiredProperty("spring.datasource.password");
      if (password.isBlank()) {
        throw new IllegalStateException(
            "Required configuration property 'spring.datasource.password' must not be blank");
      }
    };
  }
}
