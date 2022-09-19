<h1 align="center" style="text-align:center;">
  Snack3 for java
</h1>
<p align="center">
	<strong>一个高性能的 JsonPath 框架</strong>
</p>

<p align="center">
    <a target="_blank" href="https://search.maven.org/artifact/org.noear/snack3">
        <img src="https://img.shields.io/maven-central/v/org.noear/snack3.svg?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" alt="jdk-8+" />
	</a>
    <br />
    <a target="_blank" href='https://gitee.com/noear/snack3/stargazers'>
        <img src='https://gitee.com/noear/snack3/badge/star.svg' alt='gitee star'/>
    </a>
    <a target="_blank" href='https://github.com/noear/snack3/stargazers'>
        <img src="https://img.shields.io/github/stars/noear/snack3.svg?logo=github" alt="github star"/>
    </a>
</p>

<br/>
<p align="center">
	<a href="https://jq.qq.com/?_wv=1027&k=kjB5JNiC">
	<img src="https://img.shields.io/badge/QQ交流群-22200020-orange"/></a>
</p>


<hr />


基于jdk8，80kb。支持：序列化反序列化、解析和转换、构建、查找、Json path 查询。

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack3</artifactId>
  <version>3.2.38</version>
</dependency>
```

Snack3 借鉴了 `Javascript` 所有变量由 `var` 申明，及 `Xml dom` 一切都是 `Node` 的设计。其下一切数据都以`ONode`表示，`ONode`也即 `One node` 之意，代表任何类型，也可以转换为任何类型。
* 强调文档树的操控和构建能力
* 高性能`Json path`查询（兼容性和性能很赞）
* 顺带支持`序列化、反序列化`
* 基于 无参构造函数 + 字段 操作实现（因注入而触发动作的风险，不会有）


## 性能测试

每个表达式，跑1000000次的时间：

| Json path 表达式                       | 数据 | fastjson2  | json-path | snack3 |
|-------------------------------------| --- |------------| --- | -- |
| `$..a`                              | A | 764ms | 2633ms | 225ms |
| `$..*`                              | A | (不兼容1)     | 3220ms | 315ms |
| `$.data.list[1,4]`                  | A | 524ms | 782ms | 133ms |
| `$.data.list[1:4]`                  | A | 367ms | 941ms | 145ms |
| `$.data.ary2[1].a`                  | A | 329ms | 663ms | 84ms |
| `$.data.ary2[*].b.c`                | A | 642ms | 1050ms | 237ms |
| `$..b[?(@.c == 12)]`                | B | (不兼容2)     | 5636ms | 535ms |
| `$..c.min()`                        | B | (不兼容2)     | (不兼容2) | 282ms |
| `$[?(@.c =~ /a+/)]`                 | C | (不兼容2)     | 3591ms | 429ms |
| `$..ary2[0].a`                      | A | 735ms | 2551ms | 311ms |
| `$.data.list[?(@ in $..ary2[0].a)]` | A | (不兼容2)     | 5483ms | 674ms |


具体可见：
* [文章_JsonPath_性能测试.md](文章_JsonPath_性能测试.md)
* [文章_JsonPath_性能测试之2022.md](文章_JsonPath_性能测试之2022.md)




## 放几个示例

```java
//demo0::字符串化
String json = ONode.stringify(user); 

//demo1::序列化
// -- 输出带@type
String json = ONode.serialize(user); 

//demo2::反序列化
// -- json 有已带@type
UserModel user = ONode.deserialize(json); 
// -- json 可以不带@type (clz 申明了)
UserModel user = ONode.deserialize(json, UserModel.class); 
// -- json 可以不带@type，泛型方式输出（类型是已知的）
List<UserModel> list = ONode.deserialize(json, (new ArrayList<UserModel>(){}).getClass()); 

//demo3::转为ONode
ONode o = ONode.loadStr(json); //将json String 转为 ONode
ONode o = ONode.loadObj(user); //将java Object 转为 ONode

//demo3.1::转为ONode，取子节点进行序列化
ONode o = ONode.loadStr(json);
UserModel user = o.get("user").toObject(UserModel.class);


//demo4:构建json数据(极光推送的rest api调用)
public static void push(Collection<String> alias_ary, String text)  {
    ONode data = new ONode().build((d)->{
        d.getOrNew("platform").val("all");

        d.getOrNew("audience").getOrNew("alias").addAll(alias_ary);

        d.getOrNew("options")
                .set("apns_production",false);

        d.getOrNew("notification").build(n->{
            n.getOrNew("ios")
                    .set("alert",text)
                    .set("badge",0)
                    .set("sound","happy");
        });
    });

    String message = data.toJson();
    String author = Base64Util.encode(appKey+":"+masterSecret);

    Map<String,String> headers = new HashMap<>();
    headers.put("Content-Type","application/json");
    headers.put("Authorization","Basic "+author);

    HttpUtil.postString(apiUrl, message, headers);
}

//demo5:取值
o.get("name").getString();
o.get("num").getInt();
o.get("list").get(0).get("lev").getInt();

//demo5.1::取值并转换
UserModel user = o.get("user").toObject(UserModel.class); //取user节点，并转为UserModel

//demo5.2::取值或新建并填充
o.getOrNew("list2").fill("[1,2,3,4,5,5,6]");


//demo6::json path //不确定返回数量的，者会返回array类型
//找到所有的187开头的手机号，改为186，最后输出修改后的json
o.select("$..mobile[?(@ =~ /^187/)]").forEach(n->n.val("186")).toJson();
//找到data.list[1]下的的mobile字段，并转为long
o.select("$.data.list[1].mobile").getLong();

//查找所有手机号，并转为List<String> 
List<String> list = o.select("$..mobile").toObject(List.class);
//查询data.list下的所有mobile，并转为List<String>
List<String> list = o.select("$.data.list[*].mobile").toObject(List.class);
//找到187手机号的用户，并输出List<UserModel>
List<UserModel> list = o.select("$.data.list[?(@.mobile =~ /^187/)]")
                        .toObjectList(UserModel.class);
//或
List<UserModel> list = o.select("$.data.list[?(@.mobile =~ /^187/)]")
                        .toObjectList(UserModel.class);


//demo7:遍历
//如果是个Object
o.forEach((k,v)->{
  //...
});
//如果是个Array
o.forEach((v)->{
  //...
});


//demo8:自定义编码
Options options = Options.def();
options.addEncoder(Date.class, (data, node) -> {
    node.val().setString(DateUtil.format(data, "yyyy-MM-dd"));
});

String json = ONode.loadObj(orderModel, options).toJson();
```

## 关于序列化的特点
#### 对象（可以带type）
```json
{"a":1,"b":"2"}
//或
{"@type":"...","a":1,"b":"2"}
```
#### 数组
```json
[1,2,3]
//或
[{"@type":"...","a":1,"b":"2"},{"@type":"...","a":2,"b":"10"}]
```

## 关于Json path的支持
* 字符串使用单引号，例：\['name']
* 过滤操作用空隔号隔开，例：\[?(@.type == 1)]

| 支持操作 |	说明 |
| --- | --- |
| `$`	| 表示根元素 |
| `@`	| 当前节点（做为过滤表达式的谓词使用） |
| `*`	| 通用配配符，可以表示一个名字或数字。 |
| `..`	| 深层扫描。 可以理解为递归搜索。 |
| `.<name>`	| 表示一个子节点 |
| `['<name>' (, '<name>')]` | 表示一个或多个子节点 |
| `[<number> (, <number>)]`	| 表示一个或多个数组下标（负号为倒数） |
| `[start:end]`	| 数组片段，区间为\[start,end),不包含end（负号为倒数） |
| `[?(<expression>)]`	| 过滤表达式。 表达式结果必须是一个布尔值。 |

| 支持过滤操作符(`操作符两边要加空隔`) |	说明 |
| --- | --- |
| `==`	| left等于right（注意1不等于'1'） |
| `!=`	| 不等于 |
| `<`	| 小于 |
| `<=`	| 小于等于 |
| `>`	| 大于 |
| `>=`	| 大于等于 |
| `=~`	| 匹配正则表达式[?(@.name =~ /foo.*?/i)] |
| `in`	| 左边存在于右边 [?(@.size in ['S', 'M'])] |
| `nin`	| 左边不存在于右边 |

| 支持尾部函数 |	说明 |
| --- | --- |
| `min()`	| 计算数字数组的最小值 |
| `max()`	| 计算数字数组的最大值 |
| `avg()`	| 计算数字数组的平均值 |
| `sum()`	| 计算数字数组的汇总值（新加的） |

例：`n.select("$.store.book[0].title")` 或 `n.select("$['store']['book'][0]['title']")`

例：`n.select("$..book.price.min()") //找到最低的价格`



# Snack3 接口字典

* [文章_Snack3_接口字典.md](文章_Snack3_接口字典.md)

