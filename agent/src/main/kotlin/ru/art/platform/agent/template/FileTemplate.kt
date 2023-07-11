package ru.art.platform.agent.template

import java.nio.file.Path

data class FileTemplate(val path: Path, val content: String, val context: Map<String, Any> = emptyMap())
