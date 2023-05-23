package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.InstitutionType
import java.time.LocalDateTime
import java.util.*

data class InstitutionTypeDTO(
    val id: UUID,
    val name: String,
    val createdAt: LocalDateTime,
) {
    fun convertInstitutionTypeIntoDTO (institutionType: InstitutionType): InstitutionTypeDTO{
        return InstitutionTypeDTO(institutionType.id, institutionType.name, institutionType.createdAt)
    }
}