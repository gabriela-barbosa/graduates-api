package br.uff.graduatesapi.dto

data class CreateEmailDTO(
  val title: String,
  val name: String,
  val content: String,
  val buttonText: String,
  val buttonURL: String,
  val isGraduateEmail: Boolean,
  )
