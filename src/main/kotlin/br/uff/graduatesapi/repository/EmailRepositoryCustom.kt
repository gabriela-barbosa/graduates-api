package br.uff.graduatesapi.repository

import br.uff.graduatesapi.entity.EmailFilters
import br.uff.graduatesapi.entity.OffsetLimit
import br.uff.graduatesapi.model.Email
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EmailRepositoryCustom {
  fun getAllCount(emailFilters: EmailFilters, pageConfig: OffsetLimit): Long

  fun getAll(emailFilters: EmailFilters, pageConfig: OffsetLimit): List<Email>

  fun getEmailById(id: UUID): Email?
}