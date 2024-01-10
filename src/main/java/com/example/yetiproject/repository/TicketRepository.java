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
	@Query(value = "select t.ticket_id, u.username, tkinfo.open_date, tkinfo.close_date, tkinfo.ticket_price, s.match_date, s.sport_name, st.stadium_name \n "
		+ "from ticket_info as tkinfo, users as u, tickets as t, sports as s, stadium as st\n"
		+ "where t.user_id = ?;", nativeQuery = true)
	List<Object> findUserTicketList(Long userId);

	@Query(value = "select t.ticket_id, u.username, tkinfo.open_date, tkinfo.close_date, tkinfo.ticket_price, s.match_date, s.sport_name, st.stadium_name \n "
		+ "from ticket_info as tkinfo, users as u, tickets as t, sports as s, stadium as st\n"
		+ "where t.user_id = ? and t.ticket_id = ?;", nativeQuery = true)
	Object findUserTicketDetail(Long userId, Long ticketId);

	@Query(value="select * from tickets where user_id = ? and ticket_id = ? ", nativeQuery = true)
	Optional<Ticket> findUserTicket(Long userId, Long ticketId);

	@Query(value="select * from tickets where ticket_info_id = ? and posx = ? and posy = ?", nativeQuery = true)
	Optional<Ticket> findByTicketPosition(Long ticketInfoId,Long posX, Long posY);
}
