package br.uff.graduatesapi.repository

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

typealias CriteriaQueryBuilder<V, T> = (CriteriaBuilder.(query: CriteriaQuery<V>, entity: Root<T>) -> Unit)?

abstract class BaseRepository<T> {
    @PersistenceContext
    protected lateinit var entityManager: EntityManager
    abstract val resourceClass: Class<T>

    protected fun <V> criteria(
        clazz: Class<V>,
        build: CriteriaQueryBuilder<V, T> = null
    ): TypedQuery<V> {
        val builder: CriteriaBuilder = entityManager.criteriaBuilder
        val query: CriteriaQuery<V> = builder.createQuery(clazz)
        val entity: Root<T> = query.from(resourceClass)
        build?.invoke(builder, query, entity)
        return entityManager.createQuery(query)
    }

    protected fun criteria(build: CriteriaQueryBuilder<T, T> = null) =
        criteria(resourceClass)

}