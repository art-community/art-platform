package ru.art.platform.script.loader

import org.jetbrains.org.objectweb.asm.*
import org.jetbrains.org.objectweb.asm.util.*
import java.io.*
import java.lang.Thread.*

object KotlinScriptClassLoader : ClassLoader(currentThread().contextClassLoader) {
    private val classes: MutableMap<String, ByteArray> = mutableMapOf()

    fun initializeScriptClass(name: String, bytes: ByteArray): Class<*> {
        classes[name] = bytes
        return this.defineClass(name, bytes, 0, bytes.size)
    }

    fun dumpScripts(writer: PrintWriter) {
        val iterator: Iterator<*> = classes.values.iterator()
        while (iterator.hasNext()) {
            val classBytes = iterator.next() as ByteArray
            ClassReader(classBytes).accept(TraceClassVisitor(writer), 0)
        }
    }
}
