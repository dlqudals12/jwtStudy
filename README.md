# JWT 로그인(With. Spring Security)
## 1. 개요

- 개발언어: Java 17
- FrontEnd: Thymeleaf, Javascript, Bootstrap, Html5
- BackEnd: Spring Boot(2.6.7), Spring Security, OAuth2.0, JPA(SpringData Jpa), Gradle, QueryDSL
- 데이터베이스: H2DataBase
- 형상관리퉁: Github
- IDE: Intellij Ultimate

## 2. 학습개요

- 로그인 기능은 대부분의 어플리케이션에서 기본적으로 사용됩니다. 추가로 요즘은
웹이 아닌 모바일에서도 사용 가능하다는 장점과 Stateless한 서버 구현을 위해서
JWT를 사용하는 경우를 많이 볼 수 있다.
- Stateless란 Rest API와 HTTP의 공통점은 stateless(무상태)이다.
 stateless는 클라이언트와 서버 관계에서 서버가 클라이언트의 상태를 보존하지 
않음을 의미한다. 
+ 세션기반의 인증 시스템과 서버기반 인증시스템의 차이
+ 기존의 인증 시스템의 방식은 서버 기반의 인증 방식이다. 서버측에서
사용자의 정보를 기억하고 있어야 한다. 사용자들의 정보를 기억하기 위해서는
세션을 유지해야 하는데, 메모리나 디스크 또는 DB를 통해서 관리한다.
서버 기반의 인증 시스템은 클라이언트로부터 요청을 받으면, 클라이언트의 상태를 
계속해서 유지하고 이 정보를 서비스에 이용하는데, 이러한 서버를 Sateful 서버라고 한다. 
예를 들어 사용자가 로그인을 하면, 세션에 사용자 정보를 저장해두고 서비스를 제공할 때 
사용하곤 한다. 이러한 인증 방식은 소규모 시스템에서는 많이 사용되지만
웹/앱 어플리케이션이 발탈하면서 서버를 확장하기 어렵다는 문제접을 가지게 되면서
토큰 기반의 인증방식이 발달되었다.
+ 토큰 기반의 인증 시스템은 인증받은 사용자들에게 토큰을 발급하고, 서버에 요청을 할 때
헤더에 토큰을 함께 보내도록 하여 유효성 검사를 한다. 이러한 시스템에서는 더이상
사용자의 인증 정보를 서버나 세션에 유지하지 않고 클라이언트측에서 들어오는
요청만으로 작업을 처리한다. 즉, 서버 기나의 인증 시스템과 달리 상태를 유지하지 않으므로
stateless한 구조를 갖는다. 따라서 토큰 기반의 인증 방식은 시스템 확장에 용이하다.
- 토큰 기반 인증방식
1. 사용자가 아이디와 비밀번호를 로그인 한다.
2. 서버 측에서 해당 정보를 검증한다.
3. 정보가 정확하다면 서버 측에서 사용자에게 Signed 토큰을 발급한다.
4. 클라이언트 측에서 전달받은 토큰을 저장해두고, 서버에 요청을 할 때마다 해당 토큰을 서버에 함께 전달한다. 이때 Http 요청 헤더에 토큰을 포함시킨다.
5. 서버는 토큰을 검증하고, 요청에 응답한다.
![](../../Users/이병민/Desktop/다운로드.jpg)
- 토큰 방식의 장단점
- 토큰은 클라이언트 측에 저장되기 때문에 서버는 완전히 Stateless하며, 클라이언트와 서버의 연결고리가 없기 때문에 확장하기에 매우 적합하다. 만약 사용자 정보가 서버 측 세션에 저장된 경우에 서버를 확장하여 분산처리 한다면, 해당 사용자는 처음 로그인 했었던 서버에만 요청을 받도록 설정을 해주어야 한다. 하지만 토큰을 사용한다면 어떠한 서버로 요청이 와도 상관이 없다.
- 클라이언트가 서버로 요청을 보낼 때 더 이상 쿠키를 전달하지 않으므로, 쿠키 사용에 의한 취약점이 사라지게 된다. 하지만 토큰 환경의 취약점이 존재할 수 있으므로 이에 대비해야 한다.
- 시스템의 확장성을 의미하는 Scalability와 달리 Extensibility는 로그인 정보가 사용되는 분야의 확정을 의미한다. 토큰 기반의 인증 시스템에서는 토큰에 선택적인 권한만 부여하여 발급할 수 있으며 OAuth의 경우 Facebook, Google 등과 같은 소셜 계정을 이용하여 다른 웹서비스에서도 로그인을 할 수 있다.
- 서버 기반 인증 시스템의 문제점 중 하나인 CORS를 해결할 수 있는데, 애플리케이션과 서비스의 규모가 커지면 여러 디바이스를 호환시키고 더 많은 종류의 서비스를 제공하게 된다. 토큰을 사용한다면 어떤 디바이스, 어떤 도메인에서도 토큰의 유효성 검사를 진행한 후에 요청을 처리할 수 있다. 이런 구조를 통해 assests 파일(Image, html, css, js 등)은 모두 CDN에서 제공하고, 서버 측에서는 API만 다루도록 설게할 수 있다.

## 3. JWT와 Spring Security

### JWT
- JwtAuthenticationFilter <br>
 : 클라이언트 요청시 JWT 인증을 하기 위해서는 Custom Filter로 UsernamePasswordAuthenticationFilter이전에 실행됩니다.
- JwtTokenProvider
<br> : JWT 토큰 생성, 토큰 복호화 및 정보 추출, 토큰 유효성 검증의 기능이 구현된 클래스

### Security
- WebSecurityConfig <br>
: Security 설정을 위한 class로 WebSecurityConfigurerAdapter를 상송받습니다.
- CustomUserDetailService
<br> : 인증에 필요한 UserDetailService interface의 loadUserByUsername 메서드를 구현하는 클래스로 loadUserByUsername메서드를 통해 Database에 접근하여 정보를 가지고 옵니다.
- SecurityUtil
<br> : 클라이언트 요청 시 JwtAuthenticationFilter에서 인증되어 SecurityContextHolder에 저장된 Authentication 객체 정보를 가져오기 위한 클래스

## 4. 출처

- https://mangkyu.tistory.com/55
- https://wildeveloperetrain.tistory.com/57