package com.example.gout_backend.booking.service;

import java.time.Instant;
import java.util.Objects;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gout_backend.booking.dto.BookingInfoDto;
import com.example.gout_backend.booking.dto.CancelBookingDto;
import com.example.gout_backend.booking.dto.RequestBookingDto;
import com.example.gout_backend.booking.model.Booking;
import com.example.gout_backend.booking.repository.BookingRepository;
import com.example.gout_backend.common.enumeration.BookingStatusEnum;
import com.example.gout_backend.common.exception.BookingExistsException;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.common.exception.UserIdMismatchException;
import com.example.gout_backend.payment.PaymentService;
import com.example.gout_backend.qrcode.QrCodeService;
import com.example.gout_backend.tour.service.TourCountService;

@Service
public class BookingServiceImpl implements BookingService {

        private final BookingRepository bookingRepository;
        private final TourCountService tourCountService;
        private final QrCodeService qrCodeService;
        private final PaymentService paymentService;

        public BookingServiceImpl(BookingRepository bookingRepository, PaymentService paymentService,
                        QrCodeService qrCodeService, TourCountService tourCountService) {
                this.bookingRepository = bookingRepository;
                this.paymentService = paymentService;
                this.qrCodeService = qrCodeService;
                this.tourCountService = tourCountService;
        }

        @Transactional
        @Override
        public BookingInfoDto bookTour(Authentication authentication, RequestBookingDto body) {
                var jwt = (Jwt) authentication.getPrincipal();
                var userId = jwt.getClaimAsString("sub");
                if (!Objects.equals(Integer.valueOf(userId), body.userId())) {
                        throw new UserIdMismatchException("User id mismatch between credential and payload");
                }
                var idempotentKey = body.idempotentKey();
                var existingBooking = bookingRepository.findOneByUserIdAndTourId(
                                AggregateReference.to(body.userId()),
                                AggregateReference.to(body.tourId()));
                if (existingBooking.isPresent()) {
                        var data = existingBooking.get();
                        if (data.state().equals(BookingStatusEnum.COMPLETED.name())) {
                                throw new BookingExistsException(
                                                String.format("UserId: %d already booked TourId: %d",
                                                                data.userId().getId(), data.tourId().getId()));
                        }
                        return new BookingInfoDto(
                                        data.id(),
                                        data.userId().getId(),
                                        data.tourId().getId(),
                                        data.state(),
                                        null);
                }
                var now = Instant.now();
                var newBooking = new Booking(
                                null,
                                AggregateReference.to(body.userId()),
                                AggregateReference.to(body.tourId()),
                                BookingStatusEnum.PENDING.name(),
                                now,
                                now,
                                idempotentKey);
                var entity = bookingRepository.save(newBooking);
                // Generate QR Code
                var qrCodeForReference = qrCodeService.generateQrForBooking(entity.id());
                return new BookingInfoDto(
                                entity.id(),
                                entity.userId().getId(),
                                entity.tourId().getId(),
                                entity.state(),
                                qrCodeForReference.id());
        }

        @Transactional
        @Override
        public BookingInfoDto cancelTour(Authentication authentication, CancelBookingDto body) {
                var jwt = (Jwt) authentication.getPrincipal();
                var userId = jwt.getClaimAsString("sub");
                if (!Objects.equals(Integer.valueOf(userId), body.userId())) {
                        throw new UserIdMismatchException("User id mismatch between credential and payload");
                }
                var existsBooking = bookingRepository.findById(body.bookingId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                String.format("BookingId: %d not found", body.bookingId())));
                // Update tour count
                tourCountService.decrementTourCount(existsBooking.tourId().getId());
                // Refund payment
                paymentService.refundOnBooking(body.idempotentKey(), body.bookingId());
                // Delete booking for the user
                bookingRepository.deleteById(body.bookingId());
                return new BookingInfoDto(
                                existsBooking.id(),
                                existsBooking.userId().getId(),
                                existsBooking.tourId().getId(),
                                BookingStatusEnum.CANCELED.name(),
                                null);
        }
}