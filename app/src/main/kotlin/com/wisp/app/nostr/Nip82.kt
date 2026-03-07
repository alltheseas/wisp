package com.wisp.app.nostr

object Nip82 {
    const val KIND_SOFTWARE_APPLICATION = 32267
    const val KIND_SOFTWARE_RELEASE = 30063
    const val KIND_SOFTWARE_ASSET = 3063

    /** Default relay for fetching software events. */
    val DEFAULT_RELAYS = listOf("wss://relay.zapstore.dev")

    fun getAppName(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "name" }?.get(1)

    fun getAppIcon(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "icon" }?.get(1)

    fun getAppImages(tags: List<List<String>>): List<String> =
        tags.filter { it.size >= 2 && it[0] == "image" }.map { it[1] }

    fun getPlatforms(tags: List<List<String>>): List<String> =
        tags.filter { it.size >= 2 && it[0] == "f" }.map { it[1] }

    fun getLicense(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "license" }?.get(1)

    fun getRepository(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "repository" }?.get(1)

    fun getVersion(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "version" }?.get(1)

    fun getVersionCode(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "version_code" }?.get(1)

    fun getMimeType(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "m" }?.get(1)

    fun getHash(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "x" }?.get(1)

    fun getSize(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "size" }?.get(1)

    fun getUrls(tags: List<List<String>>): List<String> =
        tags.filter { it.size >= 2 && (it[0] == "url" || it[0] == "r") }.map { it[1] }

    fun getIdentifier(tags: List<List<String>>): String? =
        tags.firstOrNull { it.size >= 2 && it[0] == "d" }?.get(1)

    fun isAndroidApp(tags: List<List<String>>): Boolean =
        getPlatforms(tags).any { it.startsWith("android") }

    /** Returns human-readable platform labels from f tags. */
    fun getPlatformLabels(tags: List<List<String>>): List<String> {
        val platforms = getPlatforms(tags)
        val labels = mutableSetOf<String>()
        for (platform in platforms) {
            when {
                platform.startsWith("android") -> labels.add("Android")
                platform.startsWith("ios") -> labels.add("iOS")
                platform.startsWith("linux") -> labels.add("Linux")
                platform.startsWith("windows") || platform.startsWith("win") -> labels.add("Windows")
                platform.startsWith("macos") || platform.startsWith("darwin") -> labels.add("macOS")
                platform.startsWith("web") -> labels.add("Web")
                else -> labels.add(platform)
            }
        }
        return labels.toList()
    }

    fun isSoftwareEvent(kind: Int): Boolean =
        kind == KIND_SOFTWARE_APPLICATION || kind == KIND_SOFTWARE_RELEASE || kind == KIND_SOFTWARE_ASSET
}
