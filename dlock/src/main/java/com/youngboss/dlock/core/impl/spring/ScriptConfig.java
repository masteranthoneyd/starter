package com.youngboss.dlock.core.impl.spring;

import com.youngboss.dlock.config.DLockConfigProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author ybd
 * @date 18-7-27
 * @contact yangbingdong1994@gmail.com
 */
@Configuration
@EnableConfigurationProperties(DLockConfigProperty.class)
public class ScriptConfig {

	@Bean
	public RedisScript<Boolean> releaseLockScript(DLockConfigProperty dLockConfigProperty) {
		DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
		String scriptLocation = dLockConfigProperty.getSpring().getScriptLocation();
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(scriptLocation)));
		redisScript.setResultType(Boolean.class);
		return redisScript;
	}
}
