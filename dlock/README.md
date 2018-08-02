# Distribute Lock

可使用简单的**Java编码模式**或基于**注解模式**快速实现分布式锁

目前有三种实现（也可以说是两种）

* 基于`Redisson`
* 基于`Spring Data Redis`
* 基于`Zookeeper`

## 注解模式

```
@Component
public class TestService {

	public static int i = 0;

	@Lock(prefixClass = TestService.class, key = "#args[0]") // key = "#id" 也是可以的
	public void lockTest(Long id) {
		i++;
	}
}
```

## 编码模式

```
// 提供key以及action
public void redissonLockTest() {
		dLock.tryLockAndAction(() -> "redisson-lockInner", () -> success ++);
	}
```

**配置请看`test`目录下的`application.yml`结合`DLockConfigProperty`这个类**



## 并发量

压测使用JUnit结合contiperf，代码请看test

Redisson > Spring Data > Zookeeper

可靠性方面 Zookeeper > Redisson = Spring Data