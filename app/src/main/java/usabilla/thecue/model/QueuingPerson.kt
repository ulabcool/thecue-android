package usabilla.thecue.model

import com.google.firebase.database.ServerValue

data class QueuingPerson(val name: CharSequence, val userId: CharSequence, val createdAt: MutableMap<String, String> = ServerValue.TIMESTAMP)