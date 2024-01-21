package br.uff.graduatesapi.repository

import br.uff.graduatesapi.dto.GetInstitutionsDTO
import br.uff.graduatesapi.dto.MetaDTO
import br.uff.graduatesapi.dto.toGetInstitutionDTO
import br.uff.graduatesapi.entity.InstitutionFilters
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.model.InstitutionType
import br.uff.graduatesapi.model.PlatformUser
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.*

@Repository
class InstitutionRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,
) : InstitutionRepositoryCustom {

    @PersistenceContext
    protected lateinit var entityManager: EntityManager
    override fun findAllByFilters(filters: InstitutionFilters, pageable: Pageable): GetInstitutionsDTO {
        val builder: CriteriaBuilder = entityManager.criteriaBuilder
        val query: CriteriaQuery<Institution> = builder.createQuery(Institution::class.java)
        val entity: Root<Institution> = query.from(Institution::class.java)
        val where = mutableListOf<Predicate>()

        val countQuery = builder
            .createQuery(Long::class.java)
        val entityCount = countQuery.from(Institution::class.java)
        val whereCount = mutableListOf<Predicate>()


        filters.name?.run {
            where.add(builder.like(builder.upper(entity.get("name")), "%${this.uppercase()}%"))
            whereCount.add(builder.like(builder.upper(entityCount.get("name")), "%${this.uppercase()}%"))
        }

        filters.type?.run {
            val institutionType: Join<Institution, InstitutionType> = entity.join("type", JoinType.LEFT)
            where.add(builder.equal(institutionType.get<UUID>("id"), this))

            val institutionTypeCount: Join<Institution, InstitutionType> = entityCount.join("type", JoinType.LEFT)
            whereCount.add(builder.equal(institutionTypeCount.get<UUID>("id"), this))
        }

        query
            .select(entity)
            .where(*where.toTypedArray())
            .orderBy(builder.asc(entity.get<String>("name")))

        countQuery
            .select(builder.count(entityCount))
            .where(*whereCount.toTypedArray())

        val queryResult = entityManager.createQuery(query)
        queryResult.setFirstResult(pageable.offset.toInt())
        queryResult.setMaxResults(pageable.pageSize)

        queryResult.resultList

        val count: Long = entityManager.createQuery(countQuery).singleResult

        val meta = MetaDTO(pageable.pageNumber, pageable.pageSize, count)
        return GetInstitutionsDTO(data = queryResult.resultList.map { it.toGetInstitutionDTO() }, meta = meta)

    }
}