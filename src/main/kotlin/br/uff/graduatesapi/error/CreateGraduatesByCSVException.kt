package br.uff.graduatesapi.error

class CreateGraduatesByCSVException(error: Errors) : Exception(error.message)