package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.HistoryStatusEnum
import br.uff.graduatesapi.model.Graduate
import java.util.*

data class GraduateItemDTO(
  val id: UUID,
  val userId: UUID,
  val name: String,
  val email: String,
  val status: HistoryStatusEnum,
  var workPlace: WorkHistoryInfoDTO?,
  var position: String?,
)

fun Graduate.toGraduateItemDTO() = GraduateItemDTO(
  id = id,
  userId = user.id,
  name = user.name,
  email = user.email,
  status = currentHistoryStatus?.status ?: HistoryStatusEnum.PENDING,
  workPlace = lastWorkHistory?.toWorkHistoryInfoDTO(),
  position = lastWorkHistory?.position
)

data class ListGraduatesDTO(
  val data: List<GraduateItemDTO>,
  val meta: MetaDTO,
)
