package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Email
import br.uff.graduatesapi.model.EmailFilters
import br.uff.graduatesapi.model.OffsetLimit
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import com.linecorp.kotlinjdsl.spring.data.singleQuery
import org.springframework.stereotype.Repository

@Repository
class EmailRepositoryImpl(
  private val queryFactory: SpringDataQueryFactory,
) : EmailRepositoryCustom {

  override fun getAllCount(emailFilters: EmailFilters, pageConfig: OffsetLimit): Long {
    return queryFactory.singleQuery {
      select(count(column(Email::id)))
      from(entity(Email::class))
      where(
        or(
          emailFilters.name.run { column(Email::name).like("%${this}") },
          emailFilters.id.run { column(Email::id).equal(this) }
        )
      )
    }
  }

  override fun getAll(emailFilters: EmailFilters, pageConfig: OffsetLimit): List<Email> {
    return queryFactory.listQuery {
      select(entity(Email::class))
      from(entity(Email::class))
      where(
        or(
          emailFilters.name.run { column(Email::name).like("%${this}") },
          emailFilters.id.run { column(Email::id).equal(this) }
        )
      )
      orderBy(column(Email::createdAt).desc())
      limit(
        pageConfig.offset,
        pageConfig.limit,
      )
    }
  }

  override fun getEmailById(id: Int): Email? {
    return queryFactory.singleQuery {
      select(entity(Email::class))
      from(entity(Email::class))
      where(
        column(Email::id).equal(id)
      )
    }
  }
}