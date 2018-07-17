package com.youngboss.mvc.starter;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @author ybd
 * @date 18-5-16
 * @contact yangbingdong1994@gmail.com
 */
@Configuration
public class ValidatorConfiguration {
	@Bean
	public Validator validator() {
		return Validation.byProvider(HibernateValidator.class)
						 .configure()
						 .failFast(true)
						 .buildValidatorFactory()
						 .getValidator();
	}
}
