package br.uff.graduatesapi.dto

data class UpdateEmailDTO(
  val title: String?,
  val content: String?,
  val buttonText: String?,
  val buttonURL: String?,
  val active: Boolean?,
  )
