package com.example.gout_backend.qrcode;

import java.awt.image.BufferedImage;

import org.springframework.stereotype.Service;

import static com.example.gout_backend.common.Constants.PAYMENT_PATH;
import com.example.gout_backend.common.enumeration.QrCodeStatus;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.common.helper.QrCodeHelper;


@Service
public class QrCodeService {

    
        private final QrCodeReferenceRepository qrCodeReferenceRepository;
    
        public QrCodeService(QrCodeReferenceRepository qrCodeReferenceRepository) {
            this.qrCodeReferenceRepository = qrCodeReferenceRepository;
        }
    
        public BufferedImage generateQrById(int id) throws Exception {
            var optinalQrCodeRef = qrCodeReferenceRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("QR Id: %d not found", id)));
            return QrCodeHelper.generateQRCodeImage(optinalQrCodeRef.content());
        }
    
        public QrCodeReference generateQrForBooking(int bookingId) {
            var optinalQrCodeRef = qrCodeReferenceRepository.findOneByBookingId(bookingId);
            if (optinalQrCodeRef.isPresent()) {
                // Return existing to prevent regenerate QR
                return optinalQrCodeRef.get();
            }
            var paymentApiPath = String.format("%s/%d", PAYMENT_PATH, bookingId);
        var qrCodeEntity = new QrCodeReference(null, bookingId, paymentApiPath, QrCodeStatus.ACTIVATED.name());
        return qrCodeReferenceRepository.save(qrCodeEntity);
    }

    public QrCodeReference updatedQrStatus(int bookingId, QrCodeStatus status) {
        var optinalQrCodeRef = qrCodeReferenceRepository.findOneByBookingId(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("QR for bookingId: %d not found", bookingId)));
        var qrCodeEntity = new QrCodeReference(
                optinalQrCodeRef.id(),
                optinalQrCodeRef.bookingId(),
                optinalQrCodeRef.content(),
                status.name());
        return qrCodeReferenceRepository.save(qrCodeEntity);
    }
}
