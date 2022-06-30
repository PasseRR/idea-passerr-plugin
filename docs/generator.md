# 代码模板设置介绍

## 模板配置

模板使用[velocity](http://velocity.apache.org/engine/devel/user-guide.html#Velocity_Template_Language_VTL:_An_Introduction)配置，
内置的velocity参数介绍如下

### 非对象参数

|参数名|含义|类型|
|:---|:---|:---|
|date|代码生成日期时间|字符串|
|author|模板中配置的代码作者|字符串|

### entity对象参数

|参数名|含义|类型|
|:---|:---|:---|
|packageName|entity所在包名|字符串|
|className|entity类名|字符串|
|fieldName|entity作为参数时的参数名，类名的驼峰命名|字符串|
|imports|entity需要额外引入的非`java.lang`的包|字符串|
|fullClassName|entity类的全名(packageName.className)|字符串|
|tableName|表名称|字符串|
|tableComment|表注释|字符串|
|desc|表描述(表注释/表名称，注释为空时则为名称)|字符串|
|kebabName|类名的小写串行命名(kebab-name)，常用于controller路径|字符串|
|snakeName|类名的蛇形命名(snake_name)，常用于controller路径|字符串|
|fields|表字段数组|字段数组|
|pk|表主键字段|字段|

字段参数(表列信息)

|参数名|含义|类型|
|:---|:---|:---|
|packageName|列对应java类型所在包名，若是`java.lang`包下的类型，会省略包名|字符串|
|className|列对应java类型类名|字符串|
|fieldName|字段作为参数时的参数名，列名的驼峰命名|字符串|
|columnName|列名称|字符串|
|columnComment|列注释|字符串|
|desc|列描述(列注释/列名称，注释为空时则为名称)|字符串|
|jdbcType|列对应的jdbc类型|字符串|
|primaryKey|是否是主键|布尔|

### mapper对象参数
|参数名|含义|类型|
|:---|:---|:---|
|packageName|mapper所在包名|字符串|
|className|mapper类名|字符串|
|fieldName|mapper作为参数时的参数名，类名的驼峰命名|字符串|
|imports|mapper需要额外引入的包|
|fullClassName|mapper类的全名(packageName.className)|字符串|

### service对象参数
|参数名|含义|类型|
|:---|:---|:---|
|packageName|service所在包名|字符串|
|className|service类名|字符串|
|fieldName|service作为参数时的参数名，类名的驼峰命名|字符串|
|imports|service需要额外引入的包|
|fullClassName|service类的全名(packageName.className)|字符串|

### serviceImpl参数
|参数名|含义|类型|
|:---|:---|:---|
|packageName|serviceImpl所在包名|字符串|
|className|serviceImpl类名|字符串|
|fieldName|serviceImpl作为参数时的参数名，类名的驼峰命名|字符串|
|imports|serviceImpl需要额外引入的包|
|fullClassName|serviceImpl类的全名(packageName.className)|字符串|

### controller参数
|参数名|含义|类型|
|:---|:---|:---|
|packageName|controller所在包名|字符串|
|className|controller类名|字符串|
|fieldName|controller作为参数时的参数名，类名的驼峰命名|字符串|
|imports|controller需要额外引入的包|
|fullClassName|controller类的全名(packageName.className)|字符串|

### 类型映射
jdbc类型(参考`java.sql.JDBCType`)与java类型映射关系，默认映射列表如下，可根据需要修改类型映射

|jdbc类型|java类型|
|:---|:---|
|BIT|java.lang.Boolean|
|BOOLEAN|java.lang.Boolean|
|TINYINT|java.lang.Integer|
|SMALLINT|java.lang.Integer|
|INTEGER|java.lang.Integer|
|BIGINT|java.math.BigInteger|
|FLOAT|java.lang.Float|
|REAL|java.lang.Float|
|DOUBLE|java.lang.Double|
|NUMERIC|java.lang.Double|
|DECIMAL|java.math.BigDecimal|
|CHAR|java.lang.String|
|VARCHAR|java.lang.String|
|LONGVARCHAR|java.lang.String|
|NCHAR|java.lang.String|
|NVARCHAR|java.lang.String|
|LONGNVARCHAR|java.lang.String|
|DATE|java.time.LocalDateTime|
|TIMESTAMP|java.time.LocalDateTime|
|TIMESTAMP_WITH_TIMEZONE|java.time.LocalDateTime|
|TIME|java.time.LocalTime|
|TIME_WITH_TIMEZONE|java.time.LocalTime|
|BINARY|byte[]|
|VARBINARY|byte[]|
|LONGVARBINARY|byte[]|
|CLOB|byte[]|
|NCLOB|byte[]|
|BLOB|byte[]|
|OTHER|java.lang.String|

### 代码生成配置
|字段|描述|默认值|
|:---|:---|:---|
|作者|代码作者，用于代码注释|generator|
|基础包|代码基础包，会在基础包之下继续生成代码|无|
|entity包名|entity相对基础包的名称|entity|
|entity后缀|entity类名后缀|无|
|mapper包名|mapper相对基础包的名称|mapper|
|mapper后缀|mapper类名后缀|Mapper|
|是否生成xml|是否需要生成mapper xml|是|
|生成xml到资源目录|是则生成到resources目录，否则生成到source目录|是|
|mapper xml路径|mapper xml文件路径，使用java包名形式|mapper|
|service包名|service相对基础包的名称|service|
|service后缀|service类名后缀|Service|
|service实现包名|service实现相对基础包名称|service.impl|
|service实现后缀|service实现后缀(在service名称后追加)|Impl|
|controller包名|controller相对基础包名称|controller|
|controller后缀|controller类名后缀|Controller|

## 模板同步
选中一条模板记录，模板同步存在主配置文件以及代码模板文件，[主配置文件](templates/template.json)全配置如下：

```js
{
  // 是否使用service实现
  "useServiceImpl": true,
  // entity代码模板文件相对主文件路径
  "entity": "../../src/main/resources/generator/entity.vm",
  // mapper代码模板文件，若使用http/https则使用http直接访问，配置git上的地址需要转raw
  "mapper": "https://gitee.com/PasseRR/idea-passerr-plugin/raw/feature/src/main/resources/generator/mapper.vm",
  // mapper xml代码模板
  "mapperXml": "../../src/main/resources/generator/mapper-xml.vm",
  // service 代码模板
  "service": "../../src/main/resources/generator/service-impl/service.vm",
  // service实现代码模板
  "serviceImpl": "../../src/main/resources/generator/service-impl/service-impl.vm",
  // controller代码模板
  "controller": "../../src/main/resources/generator/controller.vm",
  // 自定义类型映射，会在默认的类型映射基础上修改或增加类型映射，非合法的jdbc类型会被忽略
  "types": [
    {
      "jdbcType": "BIT",
      "javaType": "int"
    },
    {
      "jdbcType": "BOOLEAN",
      "javaType": "String"
    },
    // 新增的jdbc类型
    {
      "jdbcType": "ARRAY",
      "javaType": "int[]"
    },
    // 错误的jdbc类型
    {
      "jdbcType": "abc",
      "javaType": "unknow"
    }
  ],
  // 参考 SettingsPo属性设置
  "settings": {
      "needMapperXml": false
  }
}
```

同步完成后记得点apply!
