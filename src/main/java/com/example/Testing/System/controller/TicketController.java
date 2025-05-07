package com.example.Testing.System.controller;

import com.example.Testing.System.dto.ticket.TicketCreateResponseDTO;
import com.example.Testing.System.dto.ticket.TicketRequestDto;
import com.example.Testing.System.dto.ticket.TicketResponseDto;
import com.example.Testing.System.mapping.TicketMapper;
import com.example.Testing.System.model.Ticket;
import com.example.Testing.System.service.TicketService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/courses/{courseId}/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    @PostMapping
    public ResponseEntity<TicketCreateResponseDTO> create(
            @PathVariable Integer courseId,
            @RequestBody TicketRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Ticket ticket = ticketService.create(courseId, dto, userDetails.getUsername());
        TicketCreateResponseDTO responseDto = TicketMapper.toCreateDto(
                ticket,
                ticket != null ? "Ticket successfully created" : "Something went wrong"
        );
        return ResponseEntity.status(ticket != null ? 200 : 500).body(responseDto);
    }


    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> getAll(
            @PathVariable Integer courseId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(ticketService.getAll(courseId, userDetails.getUsername())
                .stream()
                .map(TicketMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Ticket ticket = ticketService.getById(id, userDetails.getUsername());
        return ResponseEntity.ok(TicketMapper.toDto(ticket));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketCreateResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody TicketRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Ticket ticket = ticketService.update(id, dto, userDetails.getUsername());
        TicketCreateResponseDTO responseDto = TicketMapper.toCreateDto(
                ticket,
                ticket != null ? "Ticket successfully updated" : "Something went wrong"
        );
        return ResponseEntity.status(ticket != null ? 200 : 500).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ticketService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        byte[] pdf = ticketService.generatePdf(id, userDetails.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
