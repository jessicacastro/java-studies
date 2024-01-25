package dev.jessicacastro.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import dev.jessicacastro.todolist.user.IUserRepository;
import dev.jessicacastro.todolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String path = request.getServletPath();

    if (!path.startsWith("/tasks")) {
      filterChain.doFilter(request, response);
      return;
    }

    // Pegar a autenticação do header
    String authorization = request.getHeader("Authorization");

    if (authorization == null || !authorization.startsWith("Basic ")) {
      response.sendError(401);
      return;
    }

    String encodedAuth = authorization.substring("Basic ".length()).trim();

    String decodedAuth = new String(Base64.getDecoder().decode(encodedAuth));
    String[] credentials = decodedAuth.split(":");
    String username = credentials[0];
    String password = credentials[1];

    UserModel user = this.userRepository.findByUsername(username);

    if (user == null) {
      response.sendError(401);
      return;
    }

    Result isPasswordValid = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword().toCharArray());

    if (!isPasswordValid.verified) {
      response.sendError(401);
      return;
    }

    request.setAttribute("userId", user.getId());
    filterChain.doFilter(request, response);
  }
}