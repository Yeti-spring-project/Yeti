package com.example.yetiproject.repository;

import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.yetiproject.facade.repository.RedisRepository;

@SpringBootTest
public class RedisRepositoryTest {
	@MockBean
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private RedisRepository redisRepository;

	private static final String KEY = "zset_test_key";
	private static final String LIST_KEY = "list_key";

	@Test
	@DisplayName("zAdd 중복 value는 distinct된다")
	void test(){
		//given
		String value = "test value";
		Double score = .1;

		//when
		redisRepository.zAdd(KEY, value, score);
		redisRepository.zAdd(KEY, value, score);

		//then
		Assertions.assertThat(redisRepository.zSize(KEY)).isEqualTo(1);
	}

	@Test
	@DisplayName("zAdd score에 따라서 순서가 결정된다(오름차순)")
	void test1(){
	    //given
		String value2 = "test value2";
		double score2 = .1;
		String value3 = "test value3";
		double score3 = .15;
		String value1 = "test value1";
		double score1 = .2;
	    //when
		redisRepository.zAdd(KEY, value1, score1);
		redisRepository.zAdd(KEY, value2, score2);
		redisRepository.zAdd(KEY, value3, score3);
	    //then
		Assertions.assertThat(redisRepository.zRank(KEY, value2)).isEqualTo(0);
		Assertions.assertThat(redisRepository.zRank(KEY, value3)).isEqualTo(1);
		Assertions.assertThat(redisRepository.zRank(KEY, value1)).isEqualTo(2);
	}

	@Test
	@DisplayName("zAdd 같은 value, 다른 score라면 score가 최신으로 업데이트 된다.")
	void test2(){
	    //given
		String value = "test value";
		double score1 = .2;
		double score2 = .1;

	    //when
		redisRepository.zAdd(KEY, value, score1);
		redisRepository.zAdd(KEY, value, score2);

	    //then
		Assertions.assertThat(redisRepository.zScore(KEY, value)).isEqualTo(score2);
	}

	@Test
	@DisplayName("zAddIfAbsent 같은 value, 다른 score라면 업데이트 하지않고 무시된다.")
	void test3(){
	    //given
		String value = "test value";
		double score1 = .2;
		double score2 = .1;
	    //when
		redisRepository.zAddIfAbsent(KEY, value, score1);
		redisRepository.zAddIfAbsent(KEY, value, score2);

	    //then
		Assertions.assertThat(redisRepository.zScore(KEY, value)).isEqualTo(score1);
	}

	@Test
	@DisplayName("zRange 집합에서 원소의 startRank 이상 endRank 이하의 값을 출력된다.")
	void test4(){
		//given
		String value1 = "test value1";
		String value2 = "test value2";
		String value3 = "test value3";
		redisRepository.zAdd(KEY, value1, 0.1);
		redisRepository.zAdd(KEY, value2, 0.2);
		redisRepository.zAdd(KEY, value3, 0.3);
		//when
		Set<String> result = redisRepository.zRange(KEY, 1L, 2L);
		//then
		Assertions.assertThat(result.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("redis list 순서를 보장하는가?")
	void test5(){
		//given
		for (int i = 1; i < 10; i++) {
			String value = "test %s".formatted(i);
			redisRepository.listRightPush(LIST_KEY, value);
		}

		//when
		List<String> result = redisRepository.listRange(LIST_KEY, 0, -1);

		for(String data : result){
			System.out.println(data);
		}
	}
}
