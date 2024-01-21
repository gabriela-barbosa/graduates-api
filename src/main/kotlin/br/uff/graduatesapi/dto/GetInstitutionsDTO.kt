package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.*
import org.springframework.data.domain.Page
import java.util.*

data class GetInstitutionDTO(
    val id: UUID,
    val name: String,
    val type: InstitutionTypeDTO,
)

data class GetInstitutionsDTO(
    val meta: MetaDTO,
    val data: List<GetInstitutionDTO>,
)

fun Institution.toGetInstitutionDTO() = GetInstitutionDTO(
    id = id,
    name = name,
    type = type.toInstitutionTypeDTO(),
)

fun Page<Institution>.toGetInstitutionsDTO() = GetInstitutionsDTO(
    data = content.map { it.toGetInstitutionDTO() },
    meta = MetaDTO(
        page = pageable.pageNumber,
        size = size,
        total = totalElements,
    )
)


