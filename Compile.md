# 编译原理
---
### 包管理
<big>主目录:src.main.java.cn.rovy</big>
~~~
ds              存放项目数据结构
out             存放输出文件
test            存放测试用例
token           词法分析
parser          语法分析
semantic        语义分析
intermediate    中间代码生成与优化
assembly        生成汇编语言
~~~
<big>ds软件包(公共部分)</big>
~~~
Error       测试用例分析过程中的错误信息
GlobalVar   编译时各部分之间的静态变量(输出文件所需变量)
~~~
<big>公共变量</big>
>GlobalVar instance         公共变量类的单例变量(通过该变量获取公共变量)
List&lt;Error&gt; errors   错误数组(存放编译过程所有错误信息)
>
>List&lt;Token&gt; tokens   单词数组(测试用例按顺序识别到的单词)
>>TokenType type token类型
>>>  enum  END,KEYWORD,TYPE,SYMBOL,INTEGER,FLOAT,CHAR,STRING,IDENTIFIER;
>>>
>>String value 单词具体值
int num 单词对应类型的编号
>>
>List&lt;Integer&gt; tokenLineNum 单词对应行号数组(与单词数组个个单词一一对应)
> 
>Node rootNode AST的根节点 (语法分析,语义分析)
>>name 节点名 (语法分析)
>>father 父节点 (语法分析)
>>List&lt;Node&gt; sons 子节点 (语法分析)
>>Map&lt;String,String&gt; attributeMap 属性表(语义分析)
~~~
如要调用errors数组
GlobalVar.instance.errors.要进行的操作
~~~