package dev.jessicacastro.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {
  @Autowired // Gerencia o ciclo de vida do objeto
  private IUserRepository userRepository;

  @PostMapping()
  // Response entity with userModel or error message
  public ResponseEntity<?> create(@RequestBody UserModel userModel) {
    if (userModel.getUsername().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is required");
    }

    var userExists = this.userRepository.findByUsername(userModel.getUsername());

    if (userExists != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
    }

    var hashedPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

    userModel.setPassword(hashedPassword);

    var createdUser = this.userRepository.save(userModel);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }
}
