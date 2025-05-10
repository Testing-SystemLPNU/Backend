package com.example.Testing.System.controller;

import com.example.Testing.System.dto.check.CheckTicketRequestDto;
import com.example.Testing.System.dto.check.CheckhistoryResultDto;
import com.example.Testing.System.dto.check.GroupRequestDto;
import com.example.Testing.System.dto.check.GroupResultRowDto;
import com.example.Testing.System.service.impl.CheckHistotyServiceImpl;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Check Ticket", description = "Endpoints for checking tickets ")
public class CheckHistotyController {

    private final CheckHistotyServiceImpl checkHistotyServiceImpl;

    @Operation(summary = "Check answers for a specific ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket checked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/check")
    public ResponseEntity<CheckhistoryResultDto> checkTicket(
            @RequestBody CheckTicketRequestDto dto,
            @Parameter(hidden = true) Principal principal) {
        CheckhistoryResultDto result = checkHistotyServiceImpl.checkTicket(dto, principal.getName());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get group results as a table for a specific group name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Results retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/group-results/table")
    public List<GroupResultRowDto> getGroupResultsTable(
            @RequestBody GroupRequestDto request) {
        return checkHistotyServiceImpl.getGroupResultsTable(request.getGroup());
    }

    @Operation(summary = "Download group results as PDF for a specific group name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Error generating PDF")
    })
    @PostMapping("/group-results/pdf")
    public ResponseEntity<byte[]> getGroupResultsPdf(
            @RequestBody GroupRequestDto request) throws IOException {
        List<GroupResultRowDto> results = checkHistotyServiceImpl.getGroupResultsTable(request.getGroup());
        byte[] pdfBytes = checkHistotyServiceImpl.generateGroupResultsPdf(results, request.getGroup());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=group-results-" + request.getGroup() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}