package br.uff.graduatesapi.error

import org.springframework.http.HttpStatus

enum class Errors(val message: String, val responseMessage: String, val errorCode: HttpStatus) {
  CANT_UPDATE_EMAIL(
    "Error updating email",
    "Erro ao atualizar email. Tente novamente.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  EMAIL_IN_USE("Email already in use", "Novo email já está sendo utilizado.", HttpStatus.UNPROCESSABLE_ENTITY),

  USER_NOT_FOUND("User not found", "Usuário não encontrado.", HttpStatus.NOT_FOUND),
  CANT_RETRIEVE_USERS("Cant retrieve users", "Erro ao retorar usuários. Tente novamente.", HttpStatus.INTERNAL_SERVER_ERROR),
  UNAUTHORIZED(
    "Username or password is wrong.",
    "Usuário ou senha incorretos. Tente novamente.",
    HttpStatus.UNAUTHORIZED
  ),
  FORBIDDEN("Forbidden.", "Não autorizado.", HttpStatus.FORBIDDEN),

  USER_NOT_ALLOWED("User is not allowed", "Usuário não é permitido.", HttpStatus.FORBIDDEN),


  CANT_RETRIEVE_GRADUATES(
    "Cant retrieve graduates",
    "Erro ao retornar os egressos. Tente novamente",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  GRADUATE_NOT_FOUND("Graduate not found", "Egresso não encontrado", HttpStatus.NOT_FOUND),
  CANT_CREATE_GRADUATE(
    "Cant create graduates",
    "Erro ao tentar criar egresso. Tente novamente",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),


  INSTITUTION_TYPE_NOT_FOUND("Institution type not found", "Tipo da instituição não encontrado.", HttpStatus.NOT_FOUND),
  CANT_RETRIEVE_INSTITUTION_TYPE(
    "Cant retrieve institution types",
    "Erro ao retornar tipos de instituição.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  CANT_DELETE_INSTITUTION_TYPE(
    "Error deleting institution type",
    "Erro ao excluir tipo de instituição.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  CANT_CREATE_INSTITUTION_TYPE(
    "Error creating institution type",
    "Erro ao criar tipo de instituição.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  CANT_UPDATE_INSTITUTION_TYPE(
    "Error updating institution type",
    "Erro ao atualizar tipo de instituição.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),

  CANT_GET_LAST_WORK_HISTORY(
    "Error trying to get last work history",
    "Erro ao tentar retornar último histórico de trabalho.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  LAST_WORK_HISTORY_NOT_FOUND(
    "Last work history not found",
    "Último histórico de trabalho não encontrado.",
    HttpStatus.NOT_FOUND
  ),

  CANT_CREATE_INSTITUTION("Error creating institution", "Erro ao criar instituição.", HttpStatus.INTERNAL_SERVER_ERROR),
  INSTITUTION_NOT_FOUND("Institution not found", "Instituição não encontrada.", HttpStatus.NOT_FOUND),

  CANT_CREATE_WORK_HISTORY(
    "Error creating work history",
    "Erro ao criar histórico de trabalho.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  WORK_HISTORY_NOT_FOUND("Work history not found", "Histórico de trabalho não encontrado.", HttpStatus.NOT_FOUND),
  WORK_HISTORIES_NOT_FOUND(
    "Cant retrieve work histories",
    "Não foi possível retornar os históricos de trabalho.Tente novamente.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),

  CNPQSCHOLARSHIP_NOT_FOUND("CNPQ scholarship not found", "Bolsa CNPQ não encontrada.", HttpStatus.NOT_FOUND),
  CANT_CREATE_CNPQSCHOLARSHIP(
    "Error creating CNPQ scholarship",
    "Erro ao criar bolsa CNPQ.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),

  CANT_RETRIEVE_CNPQ_LEVELS(
    "Cant retrieve CNPQ levels",
    "Erro ao retornar níveis CNPQ.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  CANT_DELETE_CNPQ_LEVEL("Error deleting CNPQ level", "Erro ao excluir nível CNPQ.", HttpStatus.INTERNAL_SERVER_ERROR),
  CANT_CREATE_CNPQ_LEVEL("Error creating CNPQ level", "Erro ao criar nível CNPQ.", HttpStatus.INTERNAL_SERVER_ERROR),
  CNPQ_LEVEL_NOT_FOUND("CNPQ level not found", "Nível CNPQ não encontrado.", HttpStatus.NOT_FOUND),
  CANT_UPDATE_CNPQ_LEVEL(
    "Error updating CNPQ level",
    "Erro ao atualizar nível CNPQ.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),


  CANT_RETRIEVE_CI_PROGRAMS(
    "Cant retrieve CI programs",
    "Erro ao retornar programas do IC.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  CI_PROGRAM_NOT_FOUND(
    "CI program was not found",
    "Programa do IC não encontrado. Tente novamente.",
    HttpStatus.NOT_FOUND
  ),
  CANT_DELETE_CI_PROGRAM(
    "Error deleting CI program",
    "Erro ao excluir programa do IC.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  CANT_CREATE_CI_PROGRAM(
    "Error creating CI program",
    "Erro ao criar programa do IC.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  CANT_UPDATE_CI_PROGRAM(
    "Error updating CI program",
    "Erro ao atualizar programa do IC.",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),

  CANT_RETRIEVE_EMAILS("Cant retrieve emails", "Erro ao recuperar emails.", HttpStatus.INTERNAL_SERVER_ERROR),
  CANT_DELETE_EMAIL("Cant delete email", "Erro ao deletar email.", HttpStatus.INTERNAL_SERVER_ERROR),
  EMAIL_NOT_FOUND("Email not found", "Email não encontrado.", HttpStatus.NOT_FOUND),
  CANT_DELETE_AN_ACTIVE_EMAIL(
    "Cant delete an active email",
    "Não é possível deletar um email ativo.",
    HttpStatus.FORBIDDEN
  ),
  CANT_CREATE_EMAIL("Error creating email", "Erro ao criar email.", HttpStatus.INTERNAL_SERVER_ERROR),
  CANT_DEACTIVATE_EMAILS("Error deactivating emails", "Erro ao desativar emails.", HttpStatus.INTERNAL_SERVER_ERROR),

  INVALID_DATA("Invalid data", "Dados inválidos.", HttpStatus.UNPROCESSABLE_ENTITY),
}