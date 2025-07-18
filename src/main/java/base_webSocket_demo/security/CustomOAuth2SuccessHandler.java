package base_webSocket_demo.security;

import base_webSocket_demo.entity.User;
import base_webSocket_demo.service.UserService;
import base_webSocket_demo.util.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userService.findOrCreateUser(email, name);

        String token = jwtTokenProvider.generateAccessToken(user);

//        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("accessToken", token);
//        responseBody.put("tokenType", TokenType.ACCESS_TOKEN);
//        responseBody.put("userId", user.getId());
//        responseBody.put("username", user.getUsername());
//        responseBody.put("roles", user.getUserHasRoles().stream()
//                .map(userHasRole -> new SimpleGrantedAuthority(userHasRole.getRole().getName()))
//                .collect(Collectors.toSet())
//        );
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        String redirectUrl = "http://localhost:3000/oauth2-redirect?token=" + token;

        response.sendRedirect(redirectUrl);
    }
}
