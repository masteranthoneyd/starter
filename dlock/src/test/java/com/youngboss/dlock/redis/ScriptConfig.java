package com.youngboss.dlock.redis;

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
public class ScriptConfig {

	@Bean
	public RedisScript<Long> script() {
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/release_lock.lua")));
		redisScript.setResultType(Long.class);
		return redisScript;
	}
}
