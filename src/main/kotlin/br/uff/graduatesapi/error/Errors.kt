package br.uff.graduatesapi.error

import org.springframework.http.HttpStatus

enum class Errors(val message: String, val responseMessage: String, val errorCode: HttpStatus) {
    CANT_UPDATE_EMAIL("Error updating email", "Erro ao atualizar email. Tente novamente.", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_IN_USE("Email already in use", "Novo email já está sendo utilizado.", HttpStatus.UNPROCESSABLE_ENTITY),

    USER_NOT_FOUND("User not found", "Usuário não encontrado.", HttpStatus.NOT_FOUND),

    INSTITUTION_TYPE_NOT_FOUND("Institution type not found", "Tipo da instituição não encontrado.", HttpStatus.NOT_FOUND),

    CANT_CREATE_INSTITUTION("Error creating institution", "Erro ao criar instituição.", HttpStatus.INTERNAL_SERVER_ERROR),
    INSTITUTION_NOT_FOUND("Institution not found", "Instituição não encontrada.", HttpStatus.NOT_FOUND),

    CANT_CREATE_WORK_HISTORY("Error creating work history", "Erro ao criar histórico de trabalho.", HttpStatus.INTERNAL_SERVER_ERROR),
    WORK_HISTORY_NOT_FOUND("User not found", "Usuário não encontrado.", HttpStatus.NOT_FOUND),

    CNPQSCHOLARSHIP_NOT_FOUND("CNPQ scholarship not found", "Bolsa CNPQ não encontrada.", HttpStatus.NOT_FOUND),
    CANT_CREATE_CNPQSCHOLARSHIP("Error creating CNPQ scholarship", "Erro ao criar bolsa CNPQ.", HttpStatus.INTERNAL_SERVER_ERROR),

    CANT_RETRIEVE_CNPQ_LEVELS("Cant retrieve CNPQ levels", "Erro ao retornar níveis CNPQ.", HttpStatus.INTERNAL_SERVER_ERROR),

    CANT_DELETE_CIPROGRAM("Error deleting CI program", "Erro ao excluir programa do IC.", HttpStatus.INTERNAL_SERVER_ERROR),

}