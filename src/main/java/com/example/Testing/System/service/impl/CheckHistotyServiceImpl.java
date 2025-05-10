package com.example.Testing.System.service.impl;


import com.example.Testing.System.dto.check.CheckTicketRequestDto;
import com.example.Testing.System.dto.check.CheckhistoryResultDto;
import com.example.Testing.System.model.*;
import com.example.Testing.System.repository.*;
import com.example.Testing.System.service.CheckHistotyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckHistotyServiceImpl implements CheckHistotyService {

    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;
    private final CheckhistoryRepository checkhistoryRepo;

    @Override
    public CheckhistoryResultDto checkTicket(CheckTicketRequestDto dto, String email) {
        // Знаходимо користувача за email
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Знаходимо білет по ID
        Ticket ticket = ticketRepo.findById(dto.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Витягуємо відповіді користувача з DTO
        Map<String, String> recognized = dto.getAnswers();

        int correct = 0;
        Map<String, Object> correctAnswers = new HashMap<>();

        // Проходимося по питанням білета
        for (Ticketquestion tq : ticket.getTicketquestions()) {
            Question question = tq.getQuestion();
            String correctOption = question.getCorrectOption();
            String userAnswer = recognized.get(String.valueOf(question.getId()));

            correctAnswers.put(String.valueOf(question.getId()), correctOption);

            if (correctOption != null && correctOption.equalsIgnoreCase(userAnswer)) {
                correct++;
            }
        }

        // Зберігаємо історію перевірки
        Checkhistory history = new Checkhistory();
        history.setUser(user);
        history.setTicket(ticket);
        history.setRecognizedAnswers(new HashMap<>(recognized));
        history.setScore(correct);
        checkhistoryRepo.save(history);

        // Готуємо DTO з результатом
        CheckhistoryResultDto resultDto = new CheckhistoryResultDto();
        resultDto.setScore(correct);
        resultDto.setTotal(ticket.getTicketquestions().size());
//        resultDto.setCorrectAnswers(correctAnswers);
//        resultDto.setYourAnswers(new HashMap<>(recognized));
        resultDto.setCreatedAt(history.getCreatedAt());

        return resultDto;
    }


}
