package com.example.gout_backend.payment;

import java.awt.image.BufferedImage;

import com.example.gout_backend.booking.dto.BookingInfoDto;


public interface PaymentService {
    BufferedImage generatePaymentQr(int id) throws Exception;

    BookingInfoDto paymentOnBooking(String idempotentKey, int bookingId);

    void refundOnBooking(String idempotentKey, int bookingId);
}