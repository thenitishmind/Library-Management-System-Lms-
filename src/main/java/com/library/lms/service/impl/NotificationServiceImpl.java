package com.library.lms.service.impl;

import com.library.lms.entity.Loan;
import com.library.lms.entity.Member;
import com.library.lms.entity.Notification;
import com.library.lms.entity.enums.NotificationChannel;
import com.library.lms.entity.enums.NotificationType;
import com.library.lms.repository.LoanRepository;
import com.library.lms.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl {

    private final LoanRepository loanRepository;
    private final NotificationRepository notificationRepository;

    @Value("${library.loan.reminder-days-before-due:2}")
    private int reminderDaysBefore;

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendDueReminders() {
        LocalDate reminderDate = LocalDate.now().plusDays(reminderDaysBefore);
        List<Loan> loans = loanRepository.findLoansDueOn(reminderDate);
        for (Loan loan : loans) {
            createNotification(
                loan.getMember(),
                NotificationType.DUE_REMINDER,
                "Book Due Soon",
                String.format("Your book '%s' is due on %s.", loan.getBookCopy().getBook().getTitle(), loan.getDueDate())
            );
        }
        log.info("Sent {} due reminders", loans.size());
    }

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendOverdueAlerts() {
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());
        for (Loan loan : overdueLoans) {
            createNotification(
                loan.getMember(),
                NotificationType.OVERDUE,
                "Overdue Book",
                String.format("Your book '%s' is %d days overdue. Please return it.",
                    loan.getBookCopy().getBook().getTitle(), loan.overdueDays())
            );
        }
        log.info("Sent {} overdue alerts", overdueLoans.size());
    }

    @Transactional
    public void sendReservationReadyAlert(Member member, String bookTitle) {
        createNotification(
            member,
            NotificationType.RESERVATION_READY,
            "Reserved Book Available",
            String.format("Your reserved book '%s' is now available for pickup. Please collect within 3 days.", bookTitle)
        );
    }

    private void createNotification(Member recipient, NotificationType type, String subject, String body) {
        Notification notification = Notification.builder()
            .recipient(recipient)
            .type(type)
            .channel(NotificationChannel.IN_APP)
            .subject(subject)
            .body(body)
            .isRead(false)
            .sentAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
        notificationRepository.save(notification);
    }
}
