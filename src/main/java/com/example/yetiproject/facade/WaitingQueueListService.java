package com.example.yetiproject.facade;

import com.example.yetiproject.auth.security.UserDetailsImpl;
import com.example.yetiproject.dto.ticket.TicketRequestDto;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.exception.entity.TicketInfo.TicketInfoNotFoundException;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.UserRepository;
import com.example.yetiproject.service.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingQueueListService {
    private final RedisTemplate<String, String> redisTemplate;
    private final TicketService ticketService;
    private final TicketInfoRepository ticketInfoRepository;
    private final UserRepository userRepository;

    private static final long FIRST_ELEMENT = 0;
    private static final long LAST_ELEMENT = -1;
    private static final long PUBLISH_SIZE = 100;
    private static final String KEY = "ticket";
    private static final String COUNT_KEY = "ticket_count";

    private QueueObject scheduledQueueObject;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Queue에 추가
    public void addQueue(UserDetailsImpl userDetails, TicketRequestDto requestDto) throws JsonProcessingException {
        final double now = System.currentTimeMillis();
        // DTO 객체를 JSON 문자열로 변환
        requestDto.setUserId(userDetails.getUser().getUserId());
        requestDto.setNow(now);
        String jsonString = objectMapper.writeValueAsString(requestDto);
        String redisKey = KEY + requestDto.getTicketInfoId();

        // redis에 저장
//        redisTemplate.opsForZSet().add(redisKey, jsonString, now);
        redisTemplate.opsForList().rightPush(redisKey, jsonString);
        log.info("대기열에 추가 - Key : {}  Value : {} ({}초)", redisKey, jsonString, now);

        // JSON 문자열을 QueueObject 객체로 변환
        ObjectMapper objectmapper = new ObjectMapper();
        QueueObject queueObject = objectmapper.readValue(jsonString, QueueObject.class);

        // 대기열로 이동
        setScheduledQueueObject(queueObject);
    }

    // scheduledQueueObject 설정하는 메서드 추가
    public void setScheduledQueueObject(QueueObject scheduledQueueObject) {
        this.scheduledQueueObject = scheduledQueueObject;
    }

    @Scheduled(fixedDelay = 1000) // 1초마다 반복
    public void reserveTicket() throws JsonProcessingException {
        if (scheduledQueueObject == null) {
            // log.info("==== scheduledQueueObject NULL ====");
            return;
        }

        publish(scheduledQueueObject);
        getOrder(scheduledQueueObject);
    }

    // 대기열 생성
    public void getOrder(QueueObject queueObject){
        // Redis Sorted Set에서 가져올 범위 설정
        final long start = FIRST_ELEMENT;
        final long end = LAST_ELEMENT;

        // RedisQueue에 등록된 Key
        String key = queueObject.getTicketInfoId().toString();
        // Redis Sorted Set에서 범위 내의 멤버들을 가져옴
//        Set<String> queue = redisTemplate.opsForZSet().range(KEY+key, start, end);
        List<String> queue = redisTemplate.opsForList().range(KEY+key, start, end);

        // 대기열 상황
        for (String data : queue) {
//            Long rank = redisTemplate.opsForZSet().rank(KEY+key, data);
            Long rank = redisTemplate.opsForList().indexOf(KEY+key, data);
//            log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", data, rank);
        }
    }

    // ticket 발급
    public void publish(QueueObject queueObject) throws JsonProcessingException {
        // Redis Sorted Set에서 가져올 범위 설정
        final long start = FIRST_ELEMENT;
        final long end = PUBLISH_SIZE - 1;

        // RedisQueue에 등록된 Key
        String key = KEY + queueObject.getTicketInfoId().toString();
        // Redis Sorted Set에서 범위 내의 멤버들을 가져옴
//        Set<String> queues = redisTemplate.opsForZSet().range(key, start, end);
        List<String> queues = redisTemplate.opsForList().range(key, start, end);
        log.info("queue : {}", queues);

        // 발급 시작
        for (String queue : queues) {
            // ticketInfo의 정보 가져오기
            TicketInfo ticketInfo = ticketInfoRepository.findById(scheduledQueueObject.getTicketInfoId())
                    .orElseThrow(() -> new TicketInfoNotFoundException("티켓 정보를 찾을 수 없습니다."));

            // 해당 티켓 정보에 속한 대기열의 크기 가져오기
            Long ticketCount = getTicketCounter(COUNT_KEY+ticketInfo.getTicketInfoId());
//            log.info("ticketCount : {}", ticketCount);

            if (ticketCount >= ticketInfo.getStock()) {
                log.info("==== 티켓이 매진되었습니다. ====");
                log.info("queue end : {}", System.currentTimeMillis());
                return;
            }

            // JSON 문자열을 QueueObject 객체로 변환
            ObjectMapper objectmapper = new ObjectMapper();
            QueueObject queueData = objectmapper.readValue(queue, QueueObject.class);

            // 티켓 발급을 위한 TicketRequestDto 생성
            TicketRequestDto ticketRequestDto = TicketRequestDto.builder()
                    .ticketInfoId(queueData.getTicketInfoId())
                    .posX(queueData.getPosX())
                    .posY(queueData.getPosY())
                    .build();

            // 티켓 발급을 위한 User build
            User user = User.builder().userId(queueData.getUserId()).build();

            // 티켓 발급
            ticketService.reserveTicketQueue(user, ticketRequestDto);
            // 티켓 개수 증가
            incrementTicketCounter(COUNT_KEY + queueObject.getTicketInfoId().toString());
            /*log.info("'{}'님의 {}번 티켓이 발급되었습니다 (좌석 : {}, {})",
                    user.getUserId(),
                    queueData.getTicketInfoId(),
                    queueData.getPosX(),
                    queueData.getPosY());
            */
            // 대기열 제거
//            redisTemplate.opsForZSet().remove(key, queue);
            redisTemplate.opsForList().leftPop(key);
        }
    }

    // 예매한 티켓 개수 증가
    public void incrementTicketCounter(String key) {
        // ValueOperations를 이용하여 INCR 명령어 실행
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.increment(key);
    }

    // 예매한 티켓 개수 GET
    public Long getTicketCounter(String key) {
        // ValueOperations를 이용하여 GET 명령어 실행
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        // GET 명령어 실행 후 값을 가져오기
        String stringValue = valueOps.get(key);

        Long ticketCount;
        if (stringValue == null) {
            // 키가 없을 경우 초기값 설정 (예: "0")
            ticketCount = 0L;
        } else {
            ticketCount = Long.parseLong(stringValue);
        }
        return ticketCount;
    }
}
