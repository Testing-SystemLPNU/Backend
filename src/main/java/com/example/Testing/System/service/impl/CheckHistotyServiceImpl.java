package com.example.Testing.System.service.impl;


import com.example.Testing.System.dto.check.CheckTicketRequestDto;
import com.example.Testing.System.dto.check.CheckhistoryResultDto;
import com.example.Testing.System.dto.check.GroupResultRowDto;
import com.example.Testing.System.model.*;
import com.example.Testing.System.repository.*;
import com.example.Testing.System.service.CheckHistotyService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CheckHistotyServiceImpl implements CheckHistotyService {

    private final TicketRepository ticketRepo;
    private final UserRepository userRepo;
    private final CheckhistoryRepository checkhistoryRepo;
    private final StudentRepository studentRepository;

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
        resultDto.setCorrectAnswers(correctAnswers);
        resultDto.setYourAnswers(new HashMap<>(recognized));
        resultDto.setCreatedAt(history.getCreatedAt());

        return resultDto;
    }

    @Override
    public List<GroupResultRowDto> getGroupResultsTable(String groupName) {
        List<Student> students = studentRepository.findAllByGroupNameIgnoreCase(groupName);
        List<GroupResultRowDto> results = new ArrayList<>();

        for (Student student : students) {
            List<Ticket> tickets = student.getTickets();

            for (Ticket ticket : tickets) {
                Optional<Checkhistory> latestCheck = checkhistoryRepo.findTopByTicketOrderByCreatedAtDesc(ticket);
                if (latestCheck.isPresent()) {
                    Checkhistory history = latestCheck.get();
                    int maxScore = ticket.getTicketquestions().size();

                    results.add(new GroupResultRowDto(
                            student.getFullName(),
                            ticket.getId(),
                            history.getScore(),
                            maxScore
                    ));
                }
            }
        }

        return results;
    }

@Override
    public byte[] generateGroupResultsPdf(List<GroupResultRowDto> results, String groupName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Testing results - group: " + groupName).setBold().setFontSize(12).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph(" ")); // відступ

        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2, 2})).useAllAvailableWidth();
        table.addHeaderCell("Student");
        table.addHeaderCell("Ticket");
        table.addHeaderCell("Score");
        table.addHeaderCell("Max. score");

        for (GroupResultRowDto row : results) {
            table.addCell(row.getStudentName());
            table.addCell(String.valueOf(row.getTicketId()));
            table.addCell(String.valueOf(row.getScore()));
            table.addCell(String.valueOf(row.getMaxScore()));
        }

        doc.add(table);
        doc.close();

        return baos.toByteArray();
    }


}
