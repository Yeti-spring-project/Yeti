package com.example.yetiproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.yetiproject.dto.ticket.TicketInterface;
import com.example.yetiproject.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
	//@Query(value="select * from tickets where user_id = ? ", nativeQuery = true)
	@Query(value = "select t.*, s.sport_name, s.match_date\n"
		+"from tickets as t left join ticket_info as ti on t.ticket_info_id = ti.ticket_info_id\n"
		+"left join sports s on s.sport_id=ti.sports_id where t.user_id = ?", nativeQuery = true)
	List<Object> findUserTicketList(Long userId);

	@Query(value = "select t.*, s.sport_name, s.match_date\n"
		+ "from tickets as t left join ticket_info as ti on t.ticket_info_id = ti.ticket_info_id\n"
		+ "left join sports s on s.sport_id=ti.sports_id where t.user_id = ? and t.ticket_id = ?;", nativeQuery = true)
	Object findUserTicketDetail(Long userId, Long ticketId);

	@Query(value="select * from tickets where user_id = ? and ticket_id = ? ", nativeQuery = true)
	Optional<Ticket> findUserTicket(Long userId, Long ticketId);

	@Query(value="select * from tickets where ticket_info_id = ? and posx = ? and posy = ?", nativeQuery = true)
	Optional<Ticket> findByTicketPosition(Long ticketInfoId,Long posX, Long posY);
}
