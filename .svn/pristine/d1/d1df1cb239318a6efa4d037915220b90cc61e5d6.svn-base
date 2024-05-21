package ngo.friendship.satellite.utility

import java.lang.Exception
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

/**
 *
 * @author juba
 */
object SecurityUtil {
    private const val PROVIDER = "AES"
    private const val ALGORITHAM = "AES/CBC/PKCS5PADDING"

    @Throws(Exception::class)
    fun encrypt(key: ByteArray, clear: ByteArray?): ByteArray {
        val cipher =
            initCipher(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(clear)
    }

    @Throws(Exception::class)
    fun decrypt(key: ByteArray, encrypted: ByteArray?): ByteArray {
        val cipher =
            initCipher(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(encrypted)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class
    )
    private fun initCipher(TYPE: Int, key: ByteArray): Cipher {
        val keySpec = SecretKeySpec(key, PROVIDER)
        val cipher = Cipher.getInstance(PROVIDER)
        cipher.init(TYPE, keySpec)
        return cipher
    }
}