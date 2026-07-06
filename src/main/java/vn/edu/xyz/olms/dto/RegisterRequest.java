package vn.edu.xyz.olms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    private String phone;

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}
