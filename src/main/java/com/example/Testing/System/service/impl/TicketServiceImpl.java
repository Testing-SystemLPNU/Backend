package com.example.Testing.System.service.impl;

import com.example.Testing.System.dto.ticket.TicketRequestDto;
import com.example.Testing.System.model.*;
import com.example.Testing.System.repository.*;
import com.example.Testing.System.service.TicketService;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
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

    @Override
    public Ticket create(Integer courseId, TicketRequestDto dto, String username) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Ticket ticket = new Ticket();
        ticket.setCourse(course);
        ticket.setTicketNumber(dto.getTicketNumber());
        ticket.setCreatedAt(Instant.now());
        Ticket savedTicket = ticketRepo.save(ticket);

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

    //
//    @Override
//    public byte[] generatePdf(Integer ticketId, String username) {
//        Ticket ticket = getTicketById(ticketId, username);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        PdfWriter writer = new PdfWriter(baos);
//        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
//        com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);
//
//        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
//
//        // Шрифти
//        PdfFont font = null;
//        try {
//            font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
//        } catch (IOException e) {
//            throw new RuntimeException("Font error", e);
//        }
//
//        // Заголовки
//        document.add(new Paragraph("Name:").setFont(font));
//        document.add(new Paragraph("Ticket # " + ticket.getTicketNumber()).setFont(font));
//        document.add(new Paragraph("Course Name: " + ticket.getCourse().getTitle()).setFont(font));
//        document.add(new Paragraph("\n"));
//
//        List<Ticketquestion> ticketQuestions = ticket.getTicketquestions();
//        float startY = 750;
//        float xStart = 50;
//        float radioSize = 12;
//        float spacing = 70;
//
//        int index = 1;
//        for (Ticketquestion tq : ticketQuestions) {
//            Question q = tq.getQuestion();
//
//            // Питання
//            document.add(new Paragraph(index + ". " + q.getQuestionText()).setBold().setFont(font));
//            Paragraph optionsParagraph = new Paragraph()
//                    .add("A) " + q.getOptionA() + "    ")
//                    .add("B) " + q.getOptionB() + "    ")
//                    .add("C) " + q.getOptionC() + "    ")
//                    .add("D) " + q.getOptionD());
//            document.add(optionsParagraph);
//
//            // Створення групи кнопок
//            String groupName = "question" + index;
//            PdfButtonFormField radioGroup = PdfFormField.createRadioGroup(pdfDoc, groupName, "");
//
//            float x = xStart;
//            float y = startY - index * 50;
//
//            // Варіанти відповіді (A–D)
//            String[] options = {"A", "B", "C", "D"};
//            for (String option : options) {
//                Rectangle rect = new Rectangle(x, y, radioSize * 2, radioSize);
//                PdfFormField radio = PdfFormField.createRadioButton(pdfDoc, rect, radioGroup, option);
//                radioGroup.addKid(radio);
//                x += spacing;
//            }
//
//            form.addField(radioGroup);
//            index++;
//        }
//
//        document.close();
//        return baos.toByteArray();
//    }
    @Override
    public byte[] generatePdf(Integer ticketId, String username) {
        Ticket ticket = getTicketById(ticketId, username);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document document = new Document(pdfDoc, PageSize.A4);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        List<Ticketquestion> ticketQuestions = ticket.getTicketquestions();

        float startX = 70;
        float startY = 750;
        float spacingX = 100;
        float spacingY = 70;
        float radioSize = 12;
        int index = 1;

        for (Ticketquestion tq : ticketQuestions) {
            Question q = tq.getQuestion();

            document.add(new Paragraph(index + ". " + q.getQuestionText()));

            // Показати варіанти
            document.add(new Paragraph("A) " + q.getOptionA() +
                    "   B) " + q.getOptionB() +
                    "   C) " + q.getOptionC() +
                    "   D) " + q.getOptionD()));

            String groupName = "q" + index;
            PdfButtonFormField radioGroup = PdfFormField.createRadioGroup(pdfDoc, groupName, "");

            String[] values = {"A", "B", "C", "D"};
            float x = startX;
            float y = startY - (index * spacingY);

            for (String opt : values) {
                Rectangle rect = new Rectangle(x, y, radioSize, radioSize);
                PdfFormField radio = PdfFormField.createRadioButton(pdfDoc, rect, radioGroup, opt);
                radioGroup.addKid(radio);
                x += spacingX;
            }

            form.addField(radioGroup);
            document.add(new Paragraph("\n"));
            index++;
        }

        document.close();

        return baos.toByteArray();
    }


    private Ticket getTicketById(Integer ticketId, String username) {
        return ticketRepo.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Квиток не знайдено з ID: " + ticketId));
    }


}