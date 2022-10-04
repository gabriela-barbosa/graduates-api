package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Email
import br.uff.graduatesapi.model.EmailFilters
import br.uff.graduatesapi.model.OffsetLimit
import org.springframework.stereotype.Repository

@Repository
interface EmailRepositoryCustom {
  fun getAllCount(emailFilters: EmailFilters, pageConfig: OffsetLimit): Long

  fun getAll(emailFilters: EmailFilters, pageConfig: OffsetLimit): List<Email>

  fun getEmailById(id: Int): Email?
}