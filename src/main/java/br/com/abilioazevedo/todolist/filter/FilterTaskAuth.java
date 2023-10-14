package br.com.abilioazevedo.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.abilioazevedo.todolist.user.IUserRepository;
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

      var servletPath = request.getServletPath();
      if(!servletPath.startsWith("/task")){
        filterChain.doFilter(request, response);
        return;
      }
      var authorization = request.getHeader("Authorization");
      var authEncoded = authorization.substring("Basic".length()).trim();
      byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
      var authString = new String(authDecoded);
      String[] credentials = authString.split(":");
      String username = credentials[0];
      String password = credentials[1];
      var user = this.userRepository.findByUsername(username);
      if (user == null) {
        response.sendError(401, "Usuário não encontrado");
      }else{
        var passwordHashed = user.getPassword();
        var passwordMatch = BCrypt.verifyer().verify(password.toCharArray(), passwordHashed).verified;
        if (!passwordMatch) {
          response.sendError(401, "Senha inválida");
        }else{
          request.setAttribute("userId", user.getId());
          filterChain.doFilter(request, response);
        }
      }
  }



}
