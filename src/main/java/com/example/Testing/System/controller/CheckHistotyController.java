package com.example.Testing.System.controller;

import com.example.Testing.System.dto.check.CheckTicketRequestDto;
import com.example.Testing.System.dto.check.CheckhistoryResultDto;
import com.example.Testing.System.dto.check.GroupRequestDto;
import com.example.Testing.System.dto.check.GroupResultRowDto;
import com.example.Testing.System.service.impl.CheckHistotyServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
public class CheckHistotyController {

    private final CheckHistotyServiceImpl checkHistotyServiceImpl;

    @PostMapping("/check")
    public ResponseEntity<CheckhistoryResultDto> checkTicket(@RequestBody CheckTicketRequestDto dto,
                                                             Principal principal) {
        CheckhistoryResultDto result = checkHistotyServiceImpl.checkTicket(dto, principal.getName());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/group-results/table")
    public List<GroupResultRowDto> getGroupResultsTable(@RequestBody GroupRequestDto request) {
        return checkHistotyServiceImpl.getGroupResultsTable(request.getGroup());
    }

    @PostMapping("/group-results/pdf")
    public ResponseEntity<byte[]> getGroupResultsPdf(@RequestBody GroupRequestDto request) throws IOException {
        List<GroupResultRowDto> results = checkHistotyServiceImpl.getGroupResultsTable(request.getGroup());
        byte[] pdfBytes = checkHistotyServiceImpl.generateGroupResultsPdf(results, request.getGroup());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=group-results-" + request.getGroup() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
