package com.babacan05.wordcard.presentation.card


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacan05.wordcard.model.WordCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CardViewModel :ViewModel() {
    private val _wordcardstateFlow = MutableStateFlow<List<WordCard>>(value = emptyList())
private val _wordIdStateFlow= MutableStateFlow<List<String>>(value = emptyList())
    val wordIdStateFlow: StateFlow<List<String>> get() = _wordIdStateFlow.asStateFlow()
    val wordcardstateFlow: StateFlow<List<WordCard>> get() = _wordcardstateFlow.asStateFlow()


    private val db = FirebaseFirestore.getInstance()


    init {
        viewModelScope.launch {
            checkUserwordList()

            //getWordCardIds()  checkUserwordlist içine konuldu böylece list oluşumu tamamlandığında çalışacak

            //updateCards()  wordcardidler alındığıkça çaprılıyor böylece idlist gücellemesi olduğunda card uptate garanti oldu
        }


    }
   fun addWordCardsOfflineMode(){




   }


suspend fun updateCards(){

        listenToWordCardsByIds().collect{ data ->
            // StateFlow içindeki veriyi güncelleyerek UI'a otomatik olarak yansıtın
            _wordcardstateFlow.value = data


    }
}
     fun getWordCardIds() {

        viewModelScope.launch {
            getWordIdList().collect{ data ->
                // StateFlow içindeki veriyi güncelleyerek UI'a otomatik olarak yansıtın
                _wordIdStateFlow.value = data

            }

        }
    }

    private fun getWordIdList(): Flow<List<String>> = callbackFlow {
        val userId = getUserId()
        if (userId != null) {
            val listener = db
                .collection("users")
                .document(userId)
                .addSnapshotListener { documentSnapshot, error ->
                    if (error == null && documentSnapshot != null) {
                        val wordIdList:List<String>? =
                            documentSnapshot.get("wordIdList") as List<String>
                        if (wordIdList != null) {
                            trySend(wordIdList)
                           viewModelScope.launch{ updateCards()}
                           // VERİ İD EKLENDİKÇE WORDCARDLARI TEKRAR SRGULAYACAK
                        }
                    } else {
                        // Hata durumunda bir şeyler yapabilirsiniz
                        trySend(emptyList())
                    }
                }

            // Flow sona erdiğinde dinleyiciyi kapat
            awaitClose {
                listener.remove()
            }
        }
    }
    fun listenToWordCardsByIds( ): Flow<List<WordCard>> = callbackFlow {


        val collectionReference = db.collection("wordcards")


        val listener = collectionReference.addSnapshotListener { querySnapshot, error ->
            if (error != null) {

                trySend(emptyList())
                return@addSnapshotListener
            }

            val wordCardList = mutableListOf<WordCard>()


            querySnapshot?.documents?.forEach { documentSnapshot ->


                // Eğer belge ID'leri listesinde ise, WordCard oluştur ve Flow'a ekleyin
                if (documentSnapshot.id in wordIdStateFlow.value) {
                    val wordCard=documentSnapshot.toObject<WordCard>()
                    if (wordCard != null) {
                        wordCardList.add(wordCard)
                        print(wordCard.toString()+"alındı")
                    }
                }
            }


            trySend(wordCardList)

            if(wordCardList.isEmpty()){

            }
        }

        // Flow sona erdiğinde dinleyiciyi kapat
        awaitClose { listener.remove() }
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
private fun checkIsThereAnyWordCard(wordCard:WordCard,callback: (String?) -> Unit){

    val collectionRef = db.collection("wordcards")

    collectionRef
        .whereEqualTo("word", wordCard.word)
        .whereEqualTo("translate", wordCard.translate)
        .whereEqualTo("synonyms", wordCard.synonyms)
        .whereEqualTo("sentence", wordCard.sentence)
        .get()
        .addOnSuccessListener { querySnapshot ->
            var returningWordCardId: String? = null
            for (document in querySnapshot.documents) {
                returningWordCardId=document.id


            }
            callback(returningWordCardId)
        }
        .addOnFailureListener { exception ->
            println("Error getting documents: $exception")
            callback(null)
        }

}
    suspend fun addWordCard(wordCard: WordCard) {
        val wordcardsCollection = db.collection("wordcards")
        val userId = getUserId()
        val returningWordCardId = suspendCoroutine { continuation ->
            checkIsThereAnyWordCard(wordCard) { returningCard ->
                continuation.resume(returningCard)
            }
        }

        if(returningWordCardId!=null){
            if (userId != null) {


                viewModelScope.launch { addWordtoUser(userId, returningWordCardId) }//bu ıd zaten var mı kontrol et

            }
                }else {
            if (userId != null) {
                wordcardsCollection.add(wordCard.copy(creatorId =userId))
                    .addOnSuccessListener { documentReference ->
                        // Eklenen belge (document) ID'sini alın
                        val wordId = documentReference.id
                        viewModelScope.launch { addWordtoUser(userId, wordId) }//bu ıd zaten var mı kontrol et
                    }
                    .addOnFailureListener { e ->

                    }
            }
        }


    }




 fun checkUserwordList() {


        val userId = getUserId()
        val wordListRef = userId?.let {
            db.collection("users").document(it)
        }
        wordListRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                // Belge var mı kontrol et
                if (documentSnapshot.exists()) {
                    // Belge varsa "liste" adındaki alanı alın
                    val liste = documentSnapshot["wordIdList"] as? List<String>

                    if (liste != null) {
                        println("Liste zaten var: $liste")
                    } else {
                        // Liste alanı yoksa oluşturun
                        wordListRef.update("wordIdList", emptyList<String>())
                            .addOnSuccessListener {
                                println("Liste oluşturuldu.")
                            }
                            .addOnFailureListener { e ->
                                println("Liste oluşturulurken bir hata oluştu: $e")
                            }
                    }
                } else {
                    // Belge yoksa belgeyi oluşturun ve "liste" alanını ekleyin
                    wordListRef.set(mapOf("wordIdList" to emptyList<String>()))
                        .addOnSuccessListener {
                            println("Belge ve liste oluşturuldu.")
                        }
                        .addOnFailureListener { e ->
                            println("Belge oluşturulurken bir hata oluştu: $e")
                        }
                }
                getWordCardIds()
            }
            ?.addOnFailureListener { e ->
                println("Belge okunurken bir hata oluştu: $e")
            }



}
    private suspend fun addWordtoUser(userId: String, wordId: String) {
        val userRef = db.collection("users").document(userId)


        viewModelScope.launch {

            userRef.update("wordIdList", FieldValue.arrayUnion(wordId))
                .addOnSuccessListener {
                    println("Veri başarıyla eklendi.")
                }
                .addOnFailureListener { e ->
                    println("Veri eklenirken bir hata oluştu: $e")
                }
        }
    }
}