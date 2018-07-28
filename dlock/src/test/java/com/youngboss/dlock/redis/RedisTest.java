package com.youngboss.dlock.redis;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;
import static org.springframework.data.redis.connection.RedisStringCommands.SetOption.SET_IF_ABSENT;

/**
 * @author ybd
 * @date 18-7-27
 * @contact yangbingdong1994@gmail.com
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Resource(name = ScriptConfig.RELEASE_LOCK_SCRIPT)
	private RedisScript<Boolean> script;

	@Test
	public void setKv() {
		ValueOperations<String, String> vOp = stringRedisTemplate.opsForValue();
		String key1 = "key1";
		String value1 = "value1";
		vOp.set(key1, value1, 1L, TimeUnit.MINUTES);
		String key = vOp.get(key1);
		Assertions.assertThat(key)
				  .isEqualTo(value1);
	}

	@Test
	public void setNx() {
		Boolean execute1 = setNxEx("key2", "value2");
		Boolean execute2 = setNxEx("key2", "value2");
		Assertions.assertThat(execute1)
				  .isTrue();
		Assertions.assertThat(execute2)
				  .isFalse();
	}

	@Test
	public void delKey() {
		String delKey = "delKey";
		String delValue = "delValue";
		stringRedisTemplate.opsForValue().set(delKey, delValue);
		Assertions.assertThat(stringRedisTemplate.opsForValue().get(delKey))
				  .isEqualTo(delValue);
		stringRedisTemplate.delete(delKey);
		Assertions.assertThat(stringRedisTemplate.opsForValue().get(delKey))
				  .isNull();
	}

	private Boolean setNxEx(String key, String value) {
		return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
			StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
			return stringRedisConn.set(key, value, Expiration.from(1L, TimeUnit.MINUTES), SET_IF_ABSENT);
		});
	}

	@Test
	public void executeScript() throws IOException {
		String key = "scriptKey";
		String value = UUID.randomUUID().toString();
		Boolean setNxEx = setNxEx(key, value);
		Assertions.assertThat(setNxEx)
				  .isTrue();
		Boolean execute = stringRedisTemplate.execute(script, singletonList(key), value);
		Assertions.assertThat(execute)
				  .isEqualTo(true);
		execute = stringRedisTemplate.execute(script, singletonList(key), value);
		Assertions.assertThat(execute)
				  .isEqualTo(false);
	}
}
