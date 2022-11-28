package br.uff.graduatesapi.repository

import br.uff.graduatesapi.dto.GetUsersDTO
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface UserRepositoryCustom {

    fun findAllUsers(pageable: Pageable): GetUsersDTO?

}