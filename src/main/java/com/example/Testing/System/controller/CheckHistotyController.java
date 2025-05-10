package com.example.Testing.System.controller;

import com.example.Testing.System.dto.check.CheckTicketRequestDto;
import com.example.Testing.System.dto.check.CheckhistoryResultDto;
import com.example.Testing.System.service.CheckHistotyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/check")
@RequiredArgsConstructor
public class CheckHistotyController {

    private final CheckHistotyService checkService;

    @PostMapping
    public ResponseEntity<CheckhistoryResultDto> checkTicket(@RequestBody CheckTicketRequestDto dto,
                                                             Principal principal) {
        CheckhistoryResultDto result = checkService.checkTicket(dto, principal.getName());
        return ResponseEntity.ok(result);
    }
}
