package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Email
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface EmailRepository : JpaRepository<Email, Int>, EmailRepositoryCustom {
  @Modifying
  @Transactional
  @Query("update Email email set email.active = false where email.active = true and email.isGraduateEmail= ?1")
  fun deactivateEmails(isGraduateEmail: Boolean)

}