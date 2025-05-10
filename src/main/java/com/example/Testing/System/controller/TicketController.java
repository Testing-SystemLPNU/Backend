package com.example.Testing.System.controller;

import com.example.Testing.System.constant.HttpStatuses;
import com.example.Testing.System.dto.ticket.TicketCreateResponseDTO;
import com.example.Testing.System.dto.ticket.TicketRequestDto;
import com.example.Testing.System.dto.ticket.TicketResponseDto;
import com.example.Testing.System.mapping.TicketMapper;
import com.example.Testing.System.model.Ticket;
import com.example.Testing.System.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Ticket", description = "Endpoints for managing tickets within a course ")
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Create a new ticket for the specified course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "500", description = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping
    public ResponseEntity<TicketCreateResponseDTO> create(
            @Parameter(description = "Course ID") @PathVariable Integer courseId,
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

    @Operation(summary = "Get all tickets for a specific course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping
    public ResponseEntity<List<TicketResponseDto>> getAll(
            @Parameter(description = "Course ID") @PathVariable Integer courseId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(ticketService.getAll(courseId, userDetails.getUsername())
                .stream()
                .map(TicketMapper::toDto)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Get a ticket by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDto> getById(
            @Parameter(description = "Ticket ID") @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Ticket ticket = ticketService.getById(id, userDetails.getUsername());
        return ResponseEntity.ok(TicketMapper.toDto(ticket));
    }

    @Operation(summary = "Update an existing ticket by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TicketCreateResponseDTO> update(
            @Parameter(description = "Ticket ID") @PathVariable Integer id,
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

    @Operation(summary = "Delete a ticket by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = HttpStatuses.NO_CONTENT),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Ticket ID") @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ticketService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Download a ticket as a PDF file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @Parameter(description = "Ticket ID") @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        byte[] pdf = ticketService.generatePdf(id, userDetails.getUsername());
        Ticket ticket = ticketService.getById(id, userDetails.getUsername());

        String studentName = ticket.getStudent() != null
                ? ticket.getStudent().getFullName().trim().replaceAll("\\s+", "_")
                : "student";

        String groupName = ticket.getStudent() != null && ticket.getStudent().getGroupName() != null
                ? ticket.getStudent().getGroupName().trim().replaceAll("\\s+", "_")
                : "group";

        String ticketNumber = ticket.getTicketNumber() != null
                ? String.valueOf(ticket.getTicketNumber())
                : String.valueOf(ticket.getId());

        String fileName = studentName + "_" + groupName + "_ticket_" + ticketNumber + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}