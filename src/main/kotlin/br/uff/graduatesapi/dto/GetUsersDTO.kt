package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.PlatformUser

data class GetUsersDTO(
  val data: List<PlatformUser>,
  val meta: MetaDTO,
)
