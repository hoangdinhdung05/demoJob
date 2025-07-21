package base_webSocket_demo.controller;

import base_webSocket_demo.dto.UserExportDTO;
import base_webSocket_demo.entity.Role;
import base_webSocket_demo.entity.User;
import base_webSocket_demo.repository.UserRepository;
import base_webSocket_demo.util.excel.BaseExport;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class ExcelController {

    private final UserRepository userRepository;

    @GetMapping("/export/users")
    public void exportUsers(HttpServletResponse response) throws IOException {

        List<User> users = userRepository.findAll();

        List<UserExportDTO> listUser = users.stream()
                .map(this::toExportDTO)
                .toList();

        String[] headers = {"ID", "First Name", "Last Name", "Email", "Username", "Role"};
        String[] fields = {"id", "firstName", "lastName", "email", "username", "roles"};

        new BaseExport<>(listUser)
                .writeHeaderLine(headers)
                .writeDataLines(fields, UserExportDTO.class)
                .export(response, "danhsachUser");
    }

    private UserExportDTO toExportDTO(User user) {
        return UserExportDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .roles(user.getUserHasRoles().stream()
                        .map(userHasRole -> userHasRole.getRole().getName())
                        .collect(Collectors.toSet()))
                .build();
    }

}
