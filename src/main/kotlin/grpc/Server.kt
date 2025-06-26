package grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import io.mdu.grpc.noteservice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*


class NoteRepository {

    private val notesFlow = MutableSharedFlow<Note>(replay = 0, extraBufferCapacity = 100)
    private val mutex = Mutex()

    fun streamNotes() = notesFlow.asSharedFlow()

    suspend fun addNote(note: Note) = mutex.withLock {
        notesFlow.tryEmit(note)
    }

}

class NoteService(private val noteRepository: NoteRepository) :
    NoteServiceGrpcKt.NoteServiceCoroutineImplBase() {

    override suspend fun createNote(request: CreateNoteRequest): Note {
        val newNoteId = UUID.randomUUID().toString()

        return note {
            id = newNoteId
            title = request.title
            content = request.content
        }
    }

    override fun streamNotesByTag(request: NoteTagFilter): Flow<Note> = flow {
        emitAll(noteRepository.streamNotes())
    }

}


fun main() {
    val noteRepository = NoteRepository()
    val scope = CoroutineScope(Dispatchers.Default)

    val server: Server = ServerBuilder.forPort(50051)
        .addService(NoteService(noteRepository))
        .build()

    server.start()
    println("Server started on port ${server.port}")

    scope.launch {
        while (isActive) { // Check if the coroutine is still active
            delay(15000)
            val titleNumber = UUID.randomUUID().toString()
            println("Generating next title $titleNumber")
            noteRepository.addNote(note {
                title = "Generated $titleNumber"
            })
        }
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutting down...")
        scope.cancel() // Cancel the coroutine scope
        server.shutdown() // Shut down the gRPC server
    })
    server.awaitTermination()
}