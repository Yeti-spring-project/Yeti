package com.example.yetiproject.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.example.yetiproject.exception.entity.TicketInfo.TicketInfoNotFoundException;
import com.example.yetiproject.exception.entity.user.UserNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.yetiproject.entity.Bookmark;
import com.example.yetiproject.entity.TicketInfo;
import com.example.yetiproject.entity.User;
import com.example.yetiproject.repository.BookmarkRepository;
import com.example.yetiproject.repository.TicketInfoRepository;
import com.example.yetiproject.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "북마크 서비스")
public class BookmarkService {

	private final NotificationService notificationService;
	private final BookmarkRepository bookmarkRepository;
	private final TicketInfoRepository ticketInfoRepository;
	private final UserRepository userRepository;

	public String bookmark(Long ticketInfoId, Long userId) {
		TicketInfo ticketInfo = ticketInfoRepository.findById(ticketInfoId).orElseThrow(
			() -> new TicketInfoNotFoundException("티켓 정보가 없습니다."));

		User user = userRepository.findById(userId).orElseThrow(
			() -> new UserNotFoundException("유저 정보가 없습니다."));

		// 북마크 검색
		Bookmark bookmark = bookmarkRepository.findByTicketInfoIdAndUserIdJpql(ticketInfoId, userId).orElse(null);

		// 존재하면 삭제, 존재하지 않으면 생성
		if (Objects.isNull(bookmark)) {
			bookmarkRepository.save(new Bookmark(ticketInfo, user));
			return "찜하기 완료";
		} else {
			bookmarkRepository.delete(bookmark);
			return "찜하기 해제";
		}
	}

	//@Scheduled(cron = "0 0/15 * * * *")
	@Transactional
	public void notificationScheduler() {
		log.info("스케쥴러에 의해 동작");
		List<Bookmark> bookmarkList = bookmarkRepository.findByOpenDateJpql(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
		for (Bookmark bookmark : bookmarkList) {
			Long bookmarkId = bookmark.getBookmarkId();
			Long userId = bookmark.getUser().getUserId();
			notificationToUser(bookmarkId, userId);
		}
	}

	private void notificationToUser(Long bookmarkId, Long userId) {
		Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElseThrow(
			() -> new EntityNotFoundException("찜 내역이 없습니다."));

		User user = userRepository.findById(userId).orElseThrow(
			() -> new UserNotFoundException("회원정보가 없습니다."));

		String sportName = bookmark.getTicketInfo().getSports().getSportName();
		String gameDate = bookmark.getTicketInfo().getSports().getMatchDate();
		LocalDateTime openDate = bookmark.getTicketInfo().getOpenDate();
		Duration diff = Duration.between(LocalDateTime.now(), openDate);
		Long diffMins = diff.toMinutes();
		String content = diffMins + "분 뒤 " + "[" + gameDate + " " + sportName + "] 경기의 예매가 시작됩니다.";
		notificationService.send(content, user);
	}
}
