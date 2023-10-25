package br.uff.graduatesapi.repository

import br.uff.graduatesapi.dto.GetUsersDTO
import br.uff.graduatesapi.dto.MetaDTO
import br.uff.graduatesapi.entity.UserFilters
import br.uff.graduatesapi.model.PlatformUser
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.pageQuery
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

@Repository
class UserRepositoryImpl(
  private val queryFactory: SpringDataQueryFactory,
) : UserRepositoryCustom {

  @PersistenceContext
  protected lateinit var entityManager: EntityManager


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

  override fun findAllCriteria(pageable: Pageable, filters: UserFilters): GetUsersDTO {
    val builder: CriteriaBuilder = entityManager.criteriaBuilder
    val query: CriteriaQuery<PlatformUser> = builder.createQuery(PlatformUser::class.java)
    val entity: Root<PlatformUser> = query.from(PlatformUser::class.java)

    query
      .select(entity)
      .orderBy(builder.asc(entity.get<String>("name")))

    val countQuery = builder
      .createQuery(Long::class.java)

    val entityCount = countQuery.from(PlatformUser::class.java)

    countQuery
      .select(builder.count(entityCount))

    filters.name?.run {
      query.where(builder.like(builder.upper(entity.get("name")), "%${this.uppercase()}%"))
      countQuery.where(builder.like(builder.upper(entity.get("name")), "%${this.uppercase()}%"))
    }

    filters.email?.run {
      query.where(builder.like(builder.upper(entity.get("email")), "%${this.uppercase()}%"))
      countQuery.where(builder.like(builder.upper(entity.get("email")), "%${this.uppercase()}%"))
    }

    val queryResult = entityManager.createQuery(query)
    queryResult.setFirstResult(pageable.offset.toInt())
    queryResult.setMaxResults(pageable.pageSize)

    queryResult.resultList


    val count: Long = entityManager.createQuery(countQuery).singleResult

    val meta = MetaDTO(pageable.pageNumber, pageable.pageSize, count)
    return GetUsersDTO(queryResult.resultList, meta)

  }
}