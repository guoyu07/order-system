commons-lang要注意， 3的版本的包名和2的版本的不一样，有兼容性问题.

commons-lang-2.6.jar这个jar是为了和json-lib-2.4-jdk15.jar一起配套使用才引入的，commons-lang3-3.4.jar版本和json-lib-2.4-jdk15.jar不兼容，弃用.

json-lib-2.4-jdk15.jar和
jackson-core-2.2.3.jar
jackson-databind-2.2.3.jar
javassist-3.15.0-GA.jar
感觉是两套不同的JSON实现. 后者在使用@ResponseBody的时候要加入， 前者是以前JSONArray jsonObj = JSONArray.fromObject(list);这么用的

fastjson-1.2.6.jar 是阿里巴巴开发的


jdbctemplatetool用到了三个jar包:persistence-api-1.0.jar jdbctemplatetool-1.0.4-RELEASE.jar camel-name-utils-1.0.0-RELEASE.jar
