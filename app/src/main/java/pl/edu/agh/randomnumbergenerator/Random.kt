package pl.edu.agh.randomnumbergenerator

class Random(private var seed: Int) {
    private val INTEGER_SIZE = 4

    companion object {
        /**
         * Creates a single integer seed from multiple integer seeds.
         * This implementation returns a sum of all of the given seeds.
         */
        fun aggregateSeeds(vararg seeds: Int): Int =
                seeds.sum()

        fun aggregateSeeds(vararg seeds: Float): Int =
                seeds.sum().toInt()
    }

    /**
     * Returns 'numberOfArrays' random byte arrays, 'size' size each.
     *
     * E.g. getRandomBytes(256, 10) will create 10 byte arrays of 256 size each.
     */
    fun getRandomBytes(size: Int, numberOfArrays: Int): List<ByteArray> =
            (0 until numberOfArrays).map { getRandomBytes(size) }

    /**
     * Returns a random byte array of size 'size'
     */
    private fun getRandomBytes(size: Int): ByteArray {
        val blocks: Int = size / INTEGER_SIZE
        val remainder: Int = size - blocks * INTEGER_SIZE
        val bytes = ByteArray(size)

        for (i in 0 until size - remainder step INTEGER_SIZE) {
            seed = getRandomInt(seed)
            bytes[i] = (seed shr 24).toByte()
            bytes[i + 1] = (seed shr 16).toByte()
            bytes[i + 2] = (seed shr 8).toByte()
            bytes[i + 3] = seed.toByte()
        }

        for (i in 0 until remainder) {
            seed = getRandomInt(seed)
            bytes[i] = seed.toByte()
        }

        return bytes
    }

    /**
     * Returns a pseudo-random integer from the given seed.
     */
    private fun getRandomInt(seed: Int): Int =
            (seed * 1103515245 + 12345) and 0x7fffffff
}