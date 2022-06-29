# API接口文档设置介绍

## Api模板

模板使用[velocity](http://velocity.apache.org/engine/devel/user-guide.html#Velocity_Template_Language_VTL:_An_Introduction)配置，关于rest接口内置的velocity参数，主要包括url、http方法、查询参数、路径参数、报文体、应答报文。
其中，查询参数及路径参数均为List类型，具体介绍如下。

### http方法及请求路径

|参数名|含义|类型|
|:---|:---|:---|
|method|http请求方法，若没设置则为`UNKNOWN`|字符串|
|url|http请求路径|字符串|

### 查询参数及路径参数

|参数名|含义|类型|
|:---|:---|:---|
|hasQueryParams|是否存在查询参数|布尔|
|queryParams|查询参数数组|数组|
|hasPathVariables|是否存在路径参数|布尔|
|pathVariables|路径参数数组|数组|

参数对象属性说明

|参数名|含义|类型|
|:---|:---|:---|
|name|参数名称|字符串|
|alias|类型别名|字符串|
|desc|参数描述(注释)，若存在代码注释则为注释，否则同参数名|字符串|
|type|java类型全名|字符串|

### 请求及响应

|参数名|含义|类型|
|:---|:---|:---|
|hasBody|是否存在请求报文体|布尔|
|body|请求报文json5|字符串|
|hasResponse|是否存在应答报文体|布尔|
|response|应答报文json5|字符串|

### 模板示例(Markdown Api文档)

````velocity
## 默认api模版 markdown模版
## 这是一行模版注释
## ##[[这个中间可以是任何内容 不会被转义]]##

**请求路径**

`$method $url`

#if ($hasQueryParams)
**查询参数**

|参数名|类型|说明|
|:---|:---|:---|
#foreach($p in $queryParams)
|$p.name|$p.alias|$p.desc|
#end
#end

#if ($hasPathVariables)
**路径参数**

|参数名|类型|说明|
|:---|:---|:---|---|
#foreach($p in $pathVariables)
|$p.name|$p.alias|$p.desc|
#end
#end

#if ($hasBody)
**请求示例**

```json5
$body
```

#end

#if ($hasResponse)
**应答示例**

```json5
$response
```
#end
````

## 路径参数

只解析controller参数上存在@PathVariable注解的参数

## 查询参数配置

### 忽略类型

controller方法参数类型在配置列表中不做查询参数解析

### 忽略注解

controller方法参数上存在配置的注解不做查询参数解析

## 报文体

忽略请求对象及响应对象字段上存在列表配置注解的字段

## 序列化

请求对象及响应对象按照类型匹配序列化json5的值
