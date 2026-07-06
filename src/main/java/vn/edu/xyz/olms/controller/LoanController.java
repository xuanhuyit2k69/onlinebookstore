package vn.edu.xyz.olms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.xyz.olms.dto.CreateLoanRequest;
import vn.edu.xyz.olms.dto.LoanDTO;
import vn.edu.xyz.olms.dto.ReturnBookRequest;
import vn.edu.xyz.olms.dto.ReturnResult;
import vn.edu.xyz.olms.service.LoanService;
import vn.edu.xyz.olms.service.ReturnService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final ReturnService returnService;

    @GetMapping
    public ResponseEntity<List<LoanDTO>> getAll() {
        return ResponseEntity.ok(loanService.findAll());
    }

    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@RequestBody CreateLoanRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(req));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<LoanDTO> confirmLoan(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.confirmLoan(id));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<ReturnResult> returnBook(
            @PathVariable UUID id,
            @RequestBody(required = false) ReturnBookRequest req) {
        boolean damaged = req != null && req.isDamaged();
        return ResponseEntity.ok(returnService.processReturn(id, damaged));
    }
}
