package com.chessoverlay

import android.content.Context
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

class ChessAnalyzer(private val context: Context) {

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var stockfishEngine: StockfishEngine? = null
    private var boardDetector: ChessBoardDetector? = null

    init {
        stockfishEngine = StockfishEngine(context)
        boardDetector = ChessBoardDetector()
    }

    fun analyzeCurrentPosition(callback: (String?) -> Unit) {
        executor.execute {
            try {
                // Capture screen
                val screenshot = captureScreen()
                if (screenshot == null) {
                    mainHandler.post { callback(null) }
                    return@execute
                }

                // Detect chess board and extract FEN
                val fen = boardDetector?.detectBoardAndExtractFEN(screenshot)
                if (fen == null) {
                    mainHandler.post { callback(null) }
                    return@execute
                }

                // Get best move from Stockfish
                val bestMove = stockfishEngine?.getBestMove(fen)
                
                mainHandler.post { callback(bestMove) }
            } catch (e: Exception) {
                e.printStackTrace()
                mainHandler.post { callback(null) }
            }
        }
    }

    private fun captureScreen(): Bitmap? {
        // TODO: Implement screen capture using MediaProjection API
        // This requires user permission and setup
        return null
    }

    fun cleanup() {
        executor.shutdown()
        stockfishEngine?.cleanup()
    }
}
