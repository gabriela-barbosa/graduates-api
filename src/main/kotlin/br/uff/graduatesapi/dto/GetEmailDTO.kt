package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Email

data class GetEmailDTO(
  val data: List<Email>,
  val meta: MetaDTO,
)
