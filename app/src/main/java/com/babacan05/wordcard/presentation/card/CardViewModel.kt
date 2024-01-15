package com.babacan05.wordcard.presentation.card

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CardViewModel :ViewModel(){
    private val db = FirebaseFirestore.getInstance()
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