package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Email

data class GetEmailsDTO(
  val data: List<Email>,
  val meta: MetaDTO,
)
