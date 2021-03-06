package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.InstitutionType
import java.util.*

data class InstitutionTypeDTO(
    val id: Int,
    val name: String,
    val createdAt: Date,
) {
    fun convertInstitutionTypeIntoDTO (institutionType: InstitutionType): InstitutionTypeDTO{
        return InstitutionTypeDTO(institutionType.id!!, institutionType.name, institutionType.createdAt!!)
    }
}