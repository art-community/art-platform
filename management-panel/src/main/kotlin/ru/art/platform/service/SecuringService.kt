package ru.art.platform.service

import com.google.crypto.tink.Aead
import com.google.crypto.tink.BinaryKeysetReader
import com.google.crypto.tink.BinaryKeysetWriter
import com.google.crypto.tink.CleartextKeysetHandle.read
import com.google.crypto.tink.CleartextKeysetHandle.write
import com.google.crypto.tink.KeysetHandle.generateNew
import com.google.crypto.tink.aead.AeadConfig.register
import com.google.crypto.tink.aead.AesGcmKeyManager.aes256GcmTemplate
import okhttp3.internal.EMPTY_BYTE_ARRAY
import ru.art.platform.constants.SecurityConstants.KEY_FILE_NAME
import java.io.File
import java.nio.file.Files.exists
import java.nio.file.Paths.get

private fun readKey(): Aead {
    val file = File(KEY_FILE_NAME)
    if (!exists(get(KEY_FILE_NAME))) {
        file.createNewFile()

        return generateNew(aes256GcmTemplate())
                .apply { write(this, BinaryKeysetWriter.withFile(file)) }
                .getPrimitive(Aead::class.java)
    }
    return read(BinaryKeysetReader.withFile(file)).getPrimitive(Aead::class.java)
}

object SecuringService {
    private val aead by lazy(::readKey)

    fun initializeSecurity() = register()


    fun encrypt(value: ByteArray): ByteArray = aead.encrypt(value, EMPTY_BYTE_ARRAY)

    fun encrypt(value: String) = encrypt(value.toByteArray())

    fun encryptString(value: String) = encryptString(value.toByteArray())

    fun encryptString(value: ByteArray) = String(encrypt(value))


    fun decrypt(value: ByteArray): ByteArray = aead.decrypt(value, EMPTY_BYTE_ARRAY)

    fun decrypt(value: String): ByteArray = decrypt(value.toByteArray())

    fun decryptString(value: String): String = decryptString(value.toByteArray())

    fun decryptString(value: ByteArray): String = String(aead.decrypt(value, EMPTY_BYTE_ARRAY))
}
