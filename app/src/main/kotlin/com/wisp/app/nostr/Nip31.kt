package com.wisp.app.nostr

object Nip31 {
    fun getAlt(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "alt" }?.get(1)
}
