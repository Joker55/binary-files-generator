package pl.edu.agh.randomnumbergenerator

import java.io.File

class BinFileGenerator(seed: Int) {
    private val fileNameFormat = "rnd_%s_%s.bin"
    private val numberOfFiles = 10
    private val seqLengths = arrayOf(256, 1024, 4096, 8192, 10240, 102400, 1048576, 10485760)
    private val random = Random(seed)

    fun generateRandomFiles(dir: String) {
        seqLengths.forEach { seqLength ->
            val randomBytes: List<ByteArray> = random.getRandomBytes(seqLength, numberOfFiles)
            randomBytes.forEachIndexed { index, bytes ->
                val fileName = String.format(fileNameFormat, seqLength, index + 1)
                File(dir + "/" + fileName).writeBytes(bytes)
            }
        }
    }


}