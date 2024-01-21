package br.uff.graduatesapi.repository

import br.uff.graduatesapi.dto.GetUsersDTO
import br.uff.graduatesapi.entity.UserFilters
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface UserRepositoryCustom {

    fun findAllUsers(pageable: Pageable): GetUsersDTO?
    fun findAllCriteria(pageable: Pageable, filters: UserFilters): GetUsersDTO?

}