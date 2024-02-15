package com.babacan05.wordcard.common

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.babacan05.wordcard.model.WordCard
import com.babacan05.wordcard.presentation.card.CardViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.random.Random

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend  fun  doWork(): Result {
        val cardViewModel=CardViewModel()
        delay(2000)
        val wordcards=cardViewModel.offlineWordCards.value.shuffled().filter {it.learning=="false" }
        val randomInt = Random.nextInt(0, wordcards.size)
        if(wordcards.isNotEmpty()) {
           showReminderNotification(applicationContext, wordcards[randomInt])
       }
        return Result.success()
    }

    private fun showReminderNotification(context: Context, wordCard: WordCard,) {
      NotificationUtils.showNotification(context = context,wordCard)
    }

}




suspend fun updateOfflineWordCardsFlow(): Flow<List<WordCard>> = callbackFlow{

    val _offlineWordCards = MutableStateFlow<List<WordCard>>(value = emptyList())
    updateOfflineWordCardsFlow().collect { data ->
        // StateFlow içindeki veriyi güncelleyerek UI'a otomatik olarak yansıtın
        _offlineWordCards.value = data



    }




    ////
    val docRef = FirebaseFirestore.getInstance().collection("users").document(getUserId()?:"26152122810forgoogle")
        .collection("offlinewordcards")

    val listener= docRef.addSnapshotListener { snapshot, e ->
        if (e != null) {
            println("Listen failed: $e")
            return@addSnapshotListener
        }

        if (snapshot != null && !snapshot.isEmpty) {
            // Veriler değiştiğinde yapılacak işlemler
            try {
                val wordlist: MutableList<WordCard> = mutableListOf()


                for (document in snapshot.documents) {
                    document.toObject(WordCard::class.java)?.let { word ->
                        wordlist.add(word)


                    }



                }


                trySend(wordlist)


            } catch (exception: Exception) {
                println("hata: $exception")
                trySend(emptyList())
            }
        }else{trySend(emptyList())}
    }
    awaitClose {

        listener.remove()
    }
}
fun getUserId(): String? {


    val auth = FirebaseAuth.getInstance()


    val currentUser = auth.currentUser

    // Kullanıcı oturum açmışsa UID'yi alın
    if (currentUser != null) {
        return currentUser.uid

    } else {
        return null
    }

}