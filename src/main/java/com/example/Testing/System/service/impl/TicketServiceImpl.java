package com.example.Testing.System.service.impl;

import com.example.Testing.System.dto.ticket.TicketRequestDto;
import com.example.Testing.System.model.*;
import com.example.Testing.System.repository.*;
import com.example.Testing.System.service.TicketService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepo;
    private final CourseRepository courseRepo;
    private final QuestionRepository questionRepo;
    private final TicketQuestionRepository ticketquestionRepo;
    private final UserRepository userRepo;
    private final StudentRepository studentRepo;

    @Override
    public Ticket create(Integer courseId, TicketRequestDto dto, String username) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Створення або пошук студента
        Student student = studentRepo.findByFullNameAndGroupName(dto.getStudentFullName(), dto.getStudentGroup())
                .orElseGet(() -> {
                    Student newStudent = new Student();
                    newStudent.setFullName(dto.getStudentFullName());
                    newStudent.setGroupName(dto.getStudentGroup());
                    newStudent.setCreatedAt(Instant.now());
                    return studentRepo.save(newStudent);
                });

        Ticket ticket = new Ticket();
        ticket.setCourse(course);
        ticket.setCreatedAt(Instant.now());
        ticket.setStudent(student);

        Ticket savedTicket = ticketRepo.save(ticket);
        savedTicket.setTicketNumber(savedTicket.getId().intValue());
        savedTicket = ticketRepo.save(savedTicket);

        List<Question> questions = questionRepo.findAllById(dto.getQuestionIds());
        List<Ticketquestion> ticketquestions = new ArrayList<>();
        for (Question question : questions) {
            Ticketquestion tq = new Ticketquestion();
            TicketquestionId id = new TicketquestionId();
            id.setTicketId(savedTicket.getId());
            id.setQuestionId(question.getId());

            tq.setId(id);
            tq.setTicket(savedTicket);
            tq.setQuestion(question);
            ticketquestions.add(tq);
        }

        ticketquestionRepo.saveAll(ticketquestions);
        return savedTicket;
    }

    @Override
    public List<Ticket> getAll(Integer courseId, String username) {
        return ticketRepo.findAll().stream()
                .filter(t -> t.getCourse().getId().equals(courseId))
                .collect(Collectors.toList());
    }

    @Override
    public Ticket getById(Integer id, String username) {
        return ticketRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
    }

    @Override
    @Transactional
    public Ticket update(Integer id, TicketRequestDto dto, String username) {
        Ticket ticket = ticketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));


        Student student = studentRepo.findByFullNameAndGroupName(dto.getStudentFullName(), dto.getStudentGroup())
                .orElseGet(() -> {
                    Student newStudent = new Student();
                    newStudent.setFullName(dto.getStudentFullName());
                    newStudent.setGroupName(dto.getStudentGroup());
                    newStudent.setCreatedAt(Instant.now());
                    return studentRepo.save(newStudent);
                });

        ticket.setStudent(student);

        ticketquestionRepo.deleteByTicketId(Long.valueOf(ticket.getId()));
        ticket.getTicketquestions().clear();

        List<Question> questions = questionRepo.findAllById(dto.getQuestionIds());
        for (Question question : questions) {
            Ticketquestion tq = new Ticketquestion();
            TicketquestionId tqId = new TicketquestionId(ticket.getId(), question.getId());
            tq.setId(tqId);
            tq.setTicket(ticket);
            tq.setQuestion(question);
            ticket.getTicketquestions().add(tq);
        }

        return ticketRepo.save(ticket);
    }

    @Override
    public void delete(Integer id, String username) {
        ticketRepo.deleteById(id);
    }

    @Override
    public byte[] generatePdf(Integer ticketId, String username) {
        Ticket ticket = getTicketById(ticketId, username);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(20, 40, 20, 40); // нижчі верх/низ відступи

        PdfFont font;
        try {
            font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        } catch (IOException e) {
            throw new RuntimeException("Font error", e);
        }
        document.setFont(font);

        float fontSize = 10f;

        // Заголовок
        document.add(new Paragraph("Ticket # " + ticket.getTicketNumber()).setFont(font).setFontSize(fontSize));
        document.add(new Paragraph("Course Name: " + ticket.getCourse().getTitle()).setFont(font).setFontSize(fontSize));

        List<Ticketquestion> ticketQuestions = ticket.getTicketquestions();
        int index = 1;

        for (Ticketquestion tq : ticketQuestions) {
            if (index > 10) break;

            Question q = tq.getQuestion();

            document.add(new Paragraph(index + ". " + q.getQuestionText())
                    .setFont(font)
                    .setFontSize(fontSize)
                    .setBold()
                    .setMarginBottom(2));

            document.add(new Paragraph(
                    "A) " + q.getOptionA() + "    " +
                            "B) " + q.getOptionB() + "    " +
                            "C) " + q.getOptionC() + "    " +
                            "D) " + q.getOptionD())
                    .setFont(font)
                    .setFontSize(fontSize)
                    .setMarginBottom(7));

            index++;
        }

        // Друга сторінка
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        document.add(new Paragraph("Name: " + ticket.getStudent().getFullName()).setFont(font).setFontSize(fontSize));
        document.add(new Paragraph("Group: " + ticket.getStudent().getGroupName()).setFont(font).setFontSize(fontSize));
        document.add(new Paragraph("Ticket # " + ticket.getTicketNumber()).setFont(font).setFontSize(fontSize));
        document.add(new Paragraph("Course Name: " + ticket.getCourse().getTitle()).setFont(font).setFontSize(fontSize));
        document.add(new Paragraph("\n"));

        // Таблиця відповідей
        Table answerTable = new Table(new float[]{1, 1, 1, 1, 1});
        answerTable.setWidth(150);

        for (int i = 1; i <= ticketQuestions.size(); i++) {
            answerTable.addCell(new Cell().add(new Paragraph(i + ")")).setFont(font).setFontSize(fontSize).setBold().setTextAlignment(TextAlignment.CENTER));
            answerTable.addCell(new Cell().add(new Paragraph("A")).setFont(font).setFontSize(fontSize).setTextAlignment(TextAlignment.CENTER));
            answerTable.addCell(new Cell().add(new Paragraph("B")).setFont(font).setFontSize(fontSize).setTextAlignment(TextAlignment.CENTER));
            answerTable.addCell(new Cell().add(new Paragraph("C")).setFont(font).setFontSize(fontSize).setTextAlignment(TextAlignment.CENTER));
            answerTable.addCell(new Cell().add(new Paragraph("D")).setFont(font).setFontSize(fontSize).setTextAlignment(TextAlignment.CENTER));
        }

        document.add(answerTable);
        document.close();
        return baos.toByteArray();
    }

    private Ticket getTicketById(Integer ticketId, String username) {
        return ticketRepo.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Квиток не знайдено з ID: " + ticketId));
    }
}
