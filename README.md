# idea-passerr-plugin
## 1.命名切换
在CamelCase, camelCase, snake_case and SNAKE_CASE之间 默认快捷键`Alt + Shift + U`  
![命名](https://raw.githubusercontent.com/PasseRR/idea-passerr-plugin/images/camel.gif)

## 2.格式化窗口
右侧的ToolWindow有一个叫PTools的窗口   
分为上下两个文本域，上边为输入文本域，下边为输出文本域，中间为工具菜单及转换按钮   
点开可以进行json格式化、mybatis日志格式化为可执行sql、url编码解码、base64加解密、md5加密   
转换在输入文本域中快捷键为`Ctrl + Enter`
* Json格式化  
![Json](https://raw.githubusercontent.com/PasseRR/idea-passerr-plugin/images/json.gif)

* Mybatis日志格式化  
![Mybatis](https://raw.githubusercontent.com/PasseRR/idea-passerr-plugin/images/log.gif)

* 编码解码/加密解密
![Encode](https://raw.githubusercontent.com/PasseRR/idea-passerr-plugin/images/encode.gif)

## 3.Console栏直接复制mybatis日志为可执行的sql
![log](https://raw.githubusercontent.com/PasseRR/idea-passerr-plugin/images/log_copy.gif)

注：复制的mybatis日志必须包含`Preparing:`及`Parameters:`