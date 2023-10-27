package br.com.abilioazevedo.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.abilioazevedo.todolist.utils.Utils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/task")
@SecurityRequirement(name = "basicAuth")
public class TaskController {
  
  @Autowired
  private ITaskRepository taskRepository;
  
  @PostMapping()
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    var idUser = request.getAttribute("userId");
    taskModel.setIdUser((UUID) idUser);
    
    var currentDate = LocalDateTime.now();
    if(currentDate.isAfter(taskModel.getStartAt())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de início não pode ser menor que a data atual");
    }

    if(taskModel.getEndAt().isBefore(taskModel.getStartAt())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de fim não pode ser menor que a data de início");
    }

    var taskCreated = this.taskRepository.save(taskModel);
    return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);
  }

  @GetMapping()
  public ResponseEntity findAll(HttpServletRequest request) {
    var idUser = request.getAttribute("userId");
    var tasks = this.taskRepository.findByIdUser((UUID) idUser);
    return ResponseEntity.ok(tasks);
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
    var task = this.taskRepository.findById(id).orElse(null);

    if (task == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
    }

    var idUser = request.getAttribute("userId");

    if (!task.getIdUser().equals(idUser)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para alterar essa tarefa");
    }

    Utils.copyNonNullProperties(taskModel, task);
    var taskUpdated = this.taskRepository.save(task);
    return ResponseEntity.ok().body(taskUpdated);
  }
}
