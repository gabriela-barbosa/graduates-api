package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.InstitutionType
import java.time.LocalDate
import java.util.*

data class InstitutionTypeDTO(
    val id: Int,
    val name: String,
    val createdAt: LocalDate,
) {
    fun convertInstitutionTypeIntoDTO (institutionType: InstitutionType): InstitutionTypeDTO{
        return InstitutionTypeDTO(institutionType.id!!, institutionType.name, institutionType.createdAt!!)
    }
}