package com.example.yetiproject.exception.entity.TicketInfo;

public class OutOfStockException extends RuntimeException{
	public OutOfStockException(String message) {
		super(message);
	}
}
