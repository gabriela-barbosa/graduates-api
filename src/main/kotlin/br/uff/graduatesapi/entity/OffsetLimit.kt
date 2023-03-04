package br.uff.graduatesapi.entity

data class OffsetLimit(
  val offset: Int = 0,
  val limit: Int = 100000000,
  val page: Int = 0,
  val pageSize: Int = 100000000,
)
