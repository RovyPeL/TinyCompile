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
grammar.txt 文法
GlobalVar   编译时各部分之间的静态变量(输出文件所需变量)
~~~
<big>公共变量</big>
~~~
GlobalVar instance         公共变量类的单例变量(通过该变量获取公共变量)

List<Error> errors   错误数组(存放编译过程所有错误信息)

List<Token> tokens   单词数组(测试用例按顺序识别到的单词)
>TokenType type token类型
>>  enum  END,KEYWORD,TYPE,SYMBOL,INTEGER,FLOAT,CHAR,STRING,IDENTIFIER;
>String value 单词具体值
>int num 单词对应类型的编号

List<Integer> tokenLineNum 单词对应行号数组(与单词数组个个单词一一对应)

Node rootNode AST的根节点 (语法分析,语义分析)
>name 节点名
>father 父节点
>List<Node> sons 子节点
>Map<String,String> attributeMap 属性表
Map<String, Func> funcsMap 函数

List<FourItem> fourItemList 四元式数组
~~~
~~~
如要调用errors数组
GlobalVar.instance.errors.要进行的操作
~~~
<big>文法(对应node结点的儿子结点数组顺序)</big>
~~~
generate -> func funcs
funcs -> func funcs
funcs -> EMPTY
func -> TYPE IDENTIFIER ( arglist ) { definelist statements }
arglist -> TYPE IDENTIFIER args
arglist -> EMPTY
args -> , TYPE IDENTIFIER args
args -> EMPTY
definelist -> definestatement definelist
definelist -> EMPTY
definestatement -> TYPE IDENTIFIER init conse ;
init -> = expression
init -> EMPTY
conse -> , IDENTIFIER init conse
conse -> EMPTY
statements -> statement statements
statements -> EMPTY
statement -> expression ;
statement -> ifstatement
statement -> whilestatement
statement -> forstatement
statement -> dowhilestatement
statement -> returnstatement
ifstatement -> if ( expression ) { statements } elsestate
elsestate -> else { statements }
elsestate -> EMPTY
whilestatement -> while ( expression ) { statements }
forstatement -> for ( expression ; expression ; expression ) { statements }
dowhilestatement -> do { statements } while ( expression ) ;
returnstatement -> return ret ;
ret -> expression 
ret -> EMPTY
expression -> item expr
expr -> op2 expression
expr -> EMPTY
item -> IDENTIFIER callfunc
item -> INTEGER
item -> FLOAT
item -> ' CHAR '
item -> " STRING "
item -> ( expression )
item -> op1 item
callfunc -> ( parameters )
callfunc -> EMPTY
parameters -> expression param
parameters -> EMPTY
param -> , expression param
param -> EMPTY
op1 -> +
op1 -> -
op1 -> ~
op1 -> !
op1 -> ++
op1 -> --
op2 -> +
op2 -> -
op2 -> *
op2 -> /
op2 -> %
op2 -> &
op2 -> |
op2 -> ^
op2 -> <
op2 -> >
op2 -> =
op2 -> <=
op2 -> >=
op2 -> !=
op2 -> ==
op2 -> &&
op2 -> ||
~~~
 <big>attributeMap(属性表)对应不同类型的node结点的具体用途</big>
~~~
func       : "name" 函数名称
expression : "value" 表达式的值或临时变量的下标
           : "type" 表达式结果类型
           : "flag" 表达式是否有临时变量
expr       : "op2" 表达式进行的操作
item       : "name" 标识符的名字
           : "value" 临时变量的下标    
           : "flag" 单元运算是否有临时变量
           : "type" 处理函数 -> 函数返回值类型  处理常数 -> 常数类型  处理变量操作 -> 变量类型             
IDENTIFIER : "name" 标识符的名字
op1        : "value" 值
op2        : "value" 值
INTEGER    : "value" 值
FLOAT      : "value" 值     
CHAR       : "value" 值
STRING     : "value" 值
~~~