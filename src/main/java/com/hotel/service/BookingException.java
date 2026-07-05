package com.hotel.service;

/** 预订过程中的业务异常，消息直接给用户看 */
public class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}
