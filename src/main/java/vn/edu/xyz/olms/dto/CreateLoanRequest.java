package vn.edu.xyz.olms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateLoanRequest {
    private UUID memberId;
    private UUID bookId;
}
