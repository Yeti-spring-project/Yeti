package com.example.yetiproject.entity;
import com.example.yetiproject.dto.user.UserSignupRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Builder
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	private String phoneNumber;

	private String address;

	public User(UserSignupRequestDto requestDto, String encodedPassword) {
		this.email = requestDto.getEmail();
		this.password = encodedPassword;
		this.username = requestDto.getUsername();
		this.phoneNumber = requestDto.getPhoneNumber();
		this.address = requestDto.getAddress();
	}

//	public void updatePassword(String encodedPassword) {
//		this.password = encodedPassword;
//	}
}
