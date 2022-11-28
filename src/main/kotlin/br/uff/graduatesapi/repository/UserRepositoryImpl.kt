package br.uff.graduatesapi.repository

import br.uff.graduatesapi.dto.GetUsersDTO
import br.uff.graduatesapi.dto.MetaDTO
import br.uff.graduatesapi.model.PlatformUser
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.pageQuery
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
  private val queryFactory: SpringDataQueryFactory,
) : UserRepositoryCustom {

   override fun findAllUsers(pageable: Pageable): GetUsersDTO {
    val result = queryFactory.pageQuery<PlatformUser>(pageable) {
      select(entity(PlatformUser::class))
      from(entity(PlatformUser::class))
    }
     result.let {
       val meta = MetaDTO(pageable.pageNumber, it.size, it.totalElements)
       return GetUsersDTO(it.content, meta)
     }
  }
}