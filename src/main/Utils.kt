package main

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.pow

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/input/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

suspend fun <A, B> Iterable<A>.parallelMap(transform: suspend (A) -> B): List<B> =
    coroutineScope {
        map {
            async {
                transform(it)
            }
        }.awaitAll()
    }

tailrec fun gcd(x: Long, y: Long): Long {
    return if (y == 0L) x else gcd(y, x % y)
}

fun lcm(a: Long, b: Long): Long {
    return if (a == 0L || b == 0L) 0L else (a * b) / gcd(a, b)
}

fun Int.altSign(): Long = (-1.0).pow(this).toLong()