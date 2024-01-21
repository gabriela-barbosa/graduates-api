package br.uff.graduatesapi.dto

data class UpdateWorkHistoryDTO(
  val email: String,
  val knownWorkPlace: Boolean?,
  var position: String? = null,
  var institution: InstitutionDTO? = null,
  val cnpqLevelId: Int? = null,
  var postDoctorate: InstitutionDTO? = null,
  var hasFinishedDoctorateOnUFF: Boolean? = null,
  var hasFinishedMasterDegreeOnUFF: Boolean? = null,
  var successCase: String? = null
)
