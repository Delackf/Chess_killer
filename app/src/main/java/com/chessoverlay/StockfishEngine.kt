package com.chessoverlay

import android.content.Context
import java.io.*
import java.util.concurrent.TimeUnit

class StockfishEngine(private val context: Context) {

    private var stockfishProcess: Process? = null
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null

    init {
        initializeStockfish()
    }

    private fun initializeStockfish() {
        try {
            // Copy stockfish binary from assets to internal storage
            val stockfishFile = File(context.filesDir, "stockfish")
            if (!stockfishFile.exists()) {
                copyStockfishFromAssets(stockfishFile)
            }

            // Make executable
            stockfishFile.setExecutable(true)

            // Start stockfish process
            stockfishProcess = ProcessBuilder(stockfishFile.absolutePath).start()
            writer = BufferedWriter(OutputStreamWriter(stockfishProcess?.outputStream))
            reader = BufferedReader(InputStreamReader(stockfishProcess?.inputStream))

            // Initialize UCI protocol
            sendCommand("uci")
            waitForResponse("uciok")
            sendCommand("isready")
            waitForResponse("readyok")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun copyStockfishFromAssets(targetFile: File) {
        try {
            val inputStream = context.assets.open("stockfish")
            val outputStream = FileOutputStream(targetFile)
            
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getBestMove(fen: String): String? {
        return try {
            sendCommand("position fen $fen")
            sendCommand("go depth 15")
            
            var bestMove: String? = null
            var line: String?
            
            while (reader?.readLine().also { line = it } != null) {
                if (line?.startsWith("bestmove") == true) {
                    bestMove = line?.split(" ")?.get(1)
                    break
                }
            }
            
            bestMove
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sendCommand(command: String) {
        writer?.write("$command\n")
        writer?.flush()
    }

    private fun waitForResponse(expectedResponse: String) {
        try {
            var line: String?
            while (reader?.readLine().also { line = it } != null) {
                if (line == expectedResponse) {
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cleanup() {
        try {
            sendCommand("quit")
            stockfishProcess?.waitFor(2, TimeUnit.SECONDS)
            stockfishProcess?.destroyForcibly()
            writer?.close()
            reader?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
