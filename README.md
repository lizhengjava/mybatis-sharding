# mybatis-sharding

[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cc.iliz.mybatis.shading/mybatis-sharding/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cc.iliz.mybatis.shading/mybatis-sharding)


mybatis-sharding 是一个基于mybatis的分库分表插件，用户只需要在mybatis配置文件中增加上plugin配置属性，并根据自己的分表策略实现TableStrategy即可。

- 实现分表策略
根据自己的分表策略实现接口TableStrategy的getShadeTableName方法，这个方法传入的参数包括本次操作的数据库表名,本次sql对应的参数param,以及这个参数param对应的mybatis配置，用户根据自定义策略实现后返回相应的表名即可。参考代码如下：
```java
	if(param != null){
			MetaObject object = SystemMetaObject.forObject(param);
			int id = Integer.parseInt(object.getValue("id").toString());
			StringBuffer sb = new StringBuffer();
			sb.append(tableName).append("_").append(id%5);
			return sb.toString();
		}
		return tableName;
```

- 自动实现分库（一条sql中的数据表须对应同一数据源）
在系统中可以配置多个数据库，在系统初始化时会自动查询这些数据源中存储的表名，在进行数据库操作时会查询分表后对应的数据库是在哪一个数据库中（不同库有同一表优化级设置目前未实现），然后使用对应的数据源连接。

### 使用配置

1. 插件配置，
插件配置需要使用ShardingSqlSessionFactoryBean代替mybatis-spring的MapperScannerConfigurer即可。
   ```xml
   <!-- MyBatis配置 -->
    <bean id="sqlSessionFactory" class="cc.iliz.mybatis.shading.spring.ShardingSqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource" />
        <property name="configLocation" value="classpath:/mybatis/mybatis-config.xml" />
        <property name="mapperLocations" value="classpath:mybatis/*/*.xml" />
    </bean>
   ```

2. 实现分表策略

分表策略需要实现TableStrategy接口。分表策略注入有两种方式，一种是在类上加上spring注解@Component自动完成注入，另外一种是在插件配置中配置属性shardingScanPackage，推荐注解方式。，配置策略针对的数据库表名可以是多个表名使用同一策略，需使用逗号（,）隔开
- xml方式配置（不建议使用这种方式）
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE shardingConfig PUBLIC "-//mybatisSharding.iliz.cc//DTD mybatisSharding 1.0//EN"
  "http://mybatisSharding.iliz.cc/dtd/mybatis-sharding-config.dtd">
<shardingConfig>
        <strategy tableName="test_table1" strategyClass="cc.iliz.mybatis.shading.strategy.TestTable1TableStrategy"/>
</shardingConfig>
```
- 注解配置
```
@Strategy(tableName="表名")
```

3. 配置多个数据源


---

### 各版本说明及配置变化

- V2.1 优化V2.0，增加多分表表名支持
- V2.0 优化V1.X，并增加了自动分库功能。
- V1.1 整合mybatis-spring
- V1.0 实现分表功能

### 未来实现计划

- 实现自定义分库路由
- 实现数据源优化级

如有问题或建设请邮件 lizhengjava@126.com。
