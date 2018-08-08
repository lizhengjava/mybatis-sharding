# mybatis-sharding


[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

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
- 使用配置
mybatis-sharding支持xml和注解两种配置方式：
1. xml配置方式，在configuration配置中增加plugin配置
```xml
	<!-- 插件配置 -->
	<plugins>
		<plugin interceptor="cc.iliz.mybatis.shading.plugin.TableShardPlugin">
			<!-- 基于XML和注解两种配置，可以只使用一种配置即可，如果不配置，系统会使用扫描默认配置的包 ，如com,org,edu,cn,gov,io,cc-->
			<property name="shardingConfig" value="sharding_config1.xml"/>
			<property name="packageNames" value="cc.iliz.mybatis.shading"/>
		</plugin>
	</plugins> 
```
shardingConfig的配置方式为
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE shardingConfig PUBLIC "-//mybatisSharding.iliz.cc//DTD mybatisSharding 1.0//EN"
  "http://mybatisSharding.iliz.cc/dtd/mybatis-sharding-config.dtd">
<shardingConfig>
        <strategy tableName="test_table1" strategyClass="cc.iliz.mybatis.shading.strategy.TestTable1TableStrategy"/>
</shardingConfig>
```
2. 注解配置，在类中增加注解
```
@Strategy(tableName="表名")
```


mybatis-sharding计划是实现分库分表，目前阶段只实现在分表，未来将增加spring整合，分库等功能，计划如下：
1. 整合mybatis-spring实现自动注解plugin
2. 实现一次单库操作的分库
3. 实现多库联合的分库操作


如有问题或建设请邮件 lizhengjava@126.com 或微信:28281850，备注：mybatis-sharding。
