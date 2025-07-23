package com.databasemeta.ahn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/*
	JPA 부트 스타터를 사용했지만 이는 히카리와 트랜잭션 관련 어노테이션을 사용하기 위한 것이며, 결국 JPA 관련 데아타 소스를 자동생성 하지 않는다.
	즉 연결정보가 없어도 에러가 나지 않는다 
	현재의 이 프로그램 특성상 각각의 사람이 별도의 디비 커넥션을 생성해야 하지만 , 이 프로그램에서 사용하는 자체 DB는 없기 때문임
	그럼에도 JPA 를 의존성으로 추가한 것은 관련 어노테시연을 사용하기 위한 것일 쁜, 그로 인해 에러가 나지 않게 아래와 같이 설정함으로써 관련 자동 구성을 하지 않도록 함

	아래와 같이 자동설정에서 제외한다고 하더라도 프로퍼티스 파일에는 잘못된 접속정보라도 형식에 맞게 있어야 함
	그래야 스프링이 접속한 후 연결에 실패하고 난 후에 설정을 안하는 것이지, 접속 정보 자체가 없으면 연결조자 할 수가 없어서 에러가 남
	따라서 틀린 접속정보다 하더라도 속성이름에 맞게 있어야 함
*/

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class AhnApplication {

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			//System.out.println("The JVM is about to shut dow");
		}));
		SpringApplication.run(AhnApplication.class, args);
	}


}
