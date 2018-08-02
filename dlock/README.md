# Distribute Lock

可使用简单的**Java编码模式**或基于**注解模式**快速实现分布式锁

目前有三种实现（也可以说是两种）

* 基于`Redisson`
* 基于`Spring Data Redis`
* 基于`Zookeeper`

## 配置

```
spring:
  application:
    name: lock-test
  redis:
    host: 127.0.0.1
    port: 6379
    password:
    timeout: 10s
    lettuce:
      pool:
        max-active: 64
        max-wait: -1s
        max-idle: 10
        min-idle: 0

youngboss:
  dlock:
    host: 127.0.0.1
    port: 6379
    wait-time: 5
    lease-time: 5
    time-unit: seconds
    dLockType: redisson
    zookeeper:
      host: 127.0.0.1
      port: 2181
      lock-path: "/lock"
```



## 注解模式

