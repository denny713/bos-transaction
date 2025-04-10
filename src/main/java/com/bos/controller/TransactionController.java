package com.bos.controller;

import com.bos.model.dto.ResponseDto;
import com.bos.model.dto.TransReqDto;
import com.bos.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bos")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PutMapping("/setor")
    public ResponseEntity<ResponseDto> setor(@Valid @RequestBody TransReqDto dto) {
        return ResponseEntity.ok(transactionService.setor(dto));
    }

    @PutMapping("/tarik")
    public ResponseEntity<ResponseDto> tarik(@Valid @RequestBody TransReqDto dto) {
        return ResponseEntity.ok(transactionService.tarik(dto));
    }

    @PutMapping("/transfer")
    public ResponseEntity<ResponseDto> transfer(@Valid @RequestBody TransReqDto dto) {
        return ResponseEntity.ok(transactionService.transfer(dto));
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseDto> history(
            @RequestParam String accountId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        return ResponseEntity.ok(transactionService.history(accountId, from, to));
    }
}
