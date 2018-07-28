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

	public static final String RELEASE_LOCK_SCRIPT = "releaseLockScript";

	@Bean(name = RELEASE_LOCK_SCRIPT)
	public RedisScript<Boolean> releaseLockScript() {
		DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/release_lock.lua")));
		redisScript.setResultType(Boolean.class);
		return redisScript;
	}
}
