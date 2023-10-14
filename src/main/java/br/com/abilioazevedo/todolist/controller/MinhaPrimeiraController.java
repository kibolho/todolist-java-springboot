package br.com.abilioazevedo.todolist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/primeiraRota")
public class MinhaPrimeiraController {

  /**
   * GET - Buscar uma informação
   * POST - Adicionar um dado/informação
   * PUT - Altera um dado/info
   * DELETE - Remover um dado
   * PATCH -  Alterar somente uma parte da info/dado
   * @return
   */
  @GetMapping("/primeiroMetodo")
  public String primeiraMensagem() {
    return "Minha primeira mensagem";
  }
}