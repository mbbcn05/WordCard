package com.babacan05.wordcard.presentation.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CardViewModel :ViewModel(){
    private val _dataStateFlow = MutableStateFlow<String>("")
    val dataStateFlow: StateFlow<String> get() = _dataStateFlow.asStateFlow()
    private val db = FirebaseFirestore.getInstance()


    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            fetchFirestoreData().collect { data ->
                // StateFlow içindeki veriyi güncelleyerek UI'a otomatik olarak yansıtın
                _dataStateFlow.value = data
            }
        }
    }

    private fun fetchFirestoreData(): Flow<String> = callbackFlow {
        val listener = db
            .collection("users")
            .document("XFDFUWXoyIarilZLrSwZFrW3Em93")
            .addSnapshotListener { documentSnapshot, error ->
                if (error == null && documentSnapshot != null) {
                    val data = documentSnapshot.getString("email") ?: ""
                    trySend(data)
                } else {
                    // Hata durumunda bir şeyler yapabilirsiniz
                    trySend("")
                }
            }

        // Flow sona erdiğinde dinleyiciyi kapat
        awaitClose {
            listener.remove()
        }
    }



suspend fun addUserToFirestore(userId:String,name: String, email: String): Boolean {
        return try {
            val user = hashMapOf(
                "name" to name,
                "email" to email
            )

            db.collection("users")
                .document(userId)
                .set(user)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

}