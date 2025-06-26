package grpc

import io.mdu.grpc.noteservice.NoteServiceGrpcKt
import io.mdu.grpc.noteservice.createNoteRequest
import io.grpc.ManagedChannelBuilder
import io.mdu.grpc.noteservice.noteTagFilter
import kotlinx.coroutines.coroutineScope


suspend fun main() = coroutineScope {
    val channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    val stub = NoteServiceGrpcKt.NoteServiceCoroutineStub(channel)

    val request = createNoteRequest {
        title = "My first note"
        content = "This is my first note created with gRPC!"
    }
    val newNote = stub.createNote(request)
    println("Note created ${newNote.id} - ${newNote.title}")

    val tagFilter = noteTagFilter {
        tag = "kotlin"
    }
    val responseFlow = stub.streamNotesByTag(tagFilter)
    responseFlow.collect { note ->
        println("Received note: ${note.title}")
    }


}