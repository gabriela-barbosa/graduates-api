package br.uff.graduatesapi.repository

import br.uff.graduatesapi.dto.GetInstitutionsDTO
import br.uff.graduatesapi.entity.InstitutionFilters
import br.uff.graduatesapi.model.Institution
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface InstitutionRepositoryCustom {
    fun findAllByFilters(filters: InstitutionFilters, pageable: Pageable): GetInstitutionsDTO
}