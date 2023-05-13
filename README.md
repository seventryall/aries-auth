### 说明
基于spring security oauth2+jwt的身份认证，通过cookie携带jwt，并支持自动刷新token（需要满足自动刷新的条件）;<br/>
支持授权码模式和密码模式；<br/>
只做了身份认证。

### 模块
aries-auth-server：既做认证服务器，又做资源服务器；<br/>
aries-auth-facade：客户端jar包，客户端依赖此包，通过添加注解@EnableAriesAuthClient,即可支持身份认证功能，
不过它需要依赖spring-security-oauth2，是为了使用TokenStore，方便解析jwt。<br/>
aries-auth-implicit-facade：简化版客户端jar包，不需要依赖spring-security-oauth2，使用第三方类解析jwt。

### 测试
密码模式：
http://localhost:8088/auth/oauth2/welcome

授权码模式：
http://localhost:8088/auth/oauth2/welcome2
