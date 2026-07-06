package vn.edu.xyz.olms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReturnResult {
    private long fineAmount;
    private boolean hasFine;
    private String message;
}
