package br.uff.graduatesapi.enum

enum class WorkHistoryStatus(val value: Int) {
    PENDING(0),
    UPDATED_PARTIALLY(2),
    UNKNOWN(3),
    UPDATED(1),
}