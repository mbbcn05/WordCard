package com.babacan05.wordcard.presentation.card


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.babacan05.wordcard.common.MySharedPreferences
import com.babacan05.wordcard.common.isInternetAvailable
import com.babacan05.wordcard.model.WordCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CardViewModel :ViewModel() {
    private val _wordcardstateFlow = MutableStateFlow<List<WordCard>>(value = emptyList())
    private val _wordIdStateFlow = MutableStateFlow<List<String>>(value = emptyList())
    val wordIdStateFlow: StateFlow<List<String>> get() = _wordIdStateFlow.asStateFlow()
    val wordcardstateFlow: StateFlow<List<WordCard>> get() = _wordcardstateFlow.asStateFlow()
    private val _viewingWorCard = MutableStateFlow(value = WordCard())
    val viewingWorCard: StateFlow<WordCard> get() = _viewingWorCard.asStateFlow()
    private val _wordcardSearchStateFlow = MutableStateFlow<List<WordCard>>(value = emptyList())
    val wordcardSearchstateFlow: StateFlow<List<WordCard>> get() = _wordcardSearchStateFlow.asStateFlow()
    private val _offlineWordCards = MutableStateFlow<List<WordCard>>(value = emptyList())
    val offlineWordCards: StateFlow<List<WordCard>> get() = _offlineWordCards.asStateFlow()
    val wordCardUserId: String? = getUserId()


    private val db = FirebaseFirestore.getInstance()



    init {
        viewModelScope.launch {
            checkUserwordList()
            listenOfflineWordCards()
        }


    }

    fun updateViewingWordCard(wordCard: WordCard) {
        _viewingWorCard.value = wordCard
    }

    suspend fun deleteWordCard(wordcardId: String) {
      try {


          db.collection("users").document(wordCardUserId!!).collection("offlinewordcards")
              .document(viewingWorCard.value.documentId).delete().await()

          wordCardUserId?.let {
              val userRef = db.collection("users").document(it)
              userRef.update("wordIdList", FieldValue.arrayRemove(wordcardId))
                  .addOnSuccessListener {
                      println("Veri başarıyla eklendi.")
                  }
                  .addOnFailureListener { e ->
                      println("Veri eklenirken bir hata oluştu: $e")
                  }
          }


      }catch (e:Exception){
          print(e.toString())
      }
    }



    suspend fun updateCards() {

        listenToWordCardsByIds().collect { data ->
            // StateFlow içindeki veriyi güncelleyerek UI'a otomatik olarak yansıtın
            _wordcardstateFlow.value = data


        }
    }

    fun getWordCardIds() {

        viewModelScope.launch {
            getWordIdList().collect { data ->
                // StateFlow içindeki veriyi güncelleyerek UI'a otomatik olarak yansıtın
                _wordIdStateFlow.value = data

            }

        }
    }

    suspend fun saveWordCard(wordcard: WordCard, creator: Boolean, context: Context) {



        if(viewingWorCard.value.addingMode=="online"){


            if (!creator) {
                addWordCard(wordcard.copy(addingMode = "online"))

            } else {
                updateWordCard(wordcard.copy(addingMode = "online"))
            }
        }else if(viewingWorCard.value.addingMode==""){
            saveOfflineWordCard(wordCard = wordcard.copy(addingMode = "offline"))
        }else if(viewingWorCard.value.addingMode=="offline"){
            updateofflineWordCard(wordcard.copy(addingMode = "offline"))
        }

    }

    private fun updateofflineWordCard(wordcard: WordCard) {
        if (wordCardUserId != null && wordcard != null) {
            db.collection("users").document(wordCardUserId)
                .collection("offlinewordcards").document(wordcard.documentId)
                .set(wordcard)
        }
    }

    fun updateWordCard(wordCard: WordCard) {


        try {
            val userId = getUserId()
            userId?.let {
                if (!wordCard.documentId.isNullOrBlank()) {
                    // Firestore update operation
                    db.collection("wordcards")
                        .document(wordCard.documentId)
                        .set(wordCard)
                        .addOnSuccessListener {
                            println("Belge başarıyla güncellendi.")
                        }
                        .addOnFailureListener { e ->
                            println("Belge güncelleme hatası: $e")
                        }
                } else {
                    println("Document ID is null or empty.")
                }
            }
        } catch (e: Exception) {
            println("Bir hata oluştu: $e")
            // Handle the exception as needed
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
                        val wordIdList: List<String>? =
                            documentSnapshot.get("wordIdList") as List<String>
                        if (wordIdList != null) {
                            trySend(wordIdList)
                            viewModelScope.launch { updateCards() }
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

    fun listenToWordCardsByIds(): Flow<List<WordCard>> = callbackFlow {
        val collectionReference = db.collection("wordcards")

        // Belirli ID'leri içeren bir liste oluşturun
        val wordIds = wordIdStateFlow.value

        if (wordIds.isNotEmpty()) {
            // Firestore'dan belirli ID'lerle eşleşen WordCard'ları getirin
            val query = collectionReference.whereIn(FieldPath.documentId(), wordIds)

            // Firestore sorgusunu gerçekleştirin
            val listenerRegistration = query.addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val wordCardList = mutableListOf<WordCard>()

                querySnapshot?.documents?.forEach { documentSnapshot ->
                    val wordCard = documentSnapshot.toObject(WordCard::class.java)
                    if (wordCard != null) {
                        wordCardList.add(wordCard)
                    }
                }

                trySend(wordCardList)
            }

            // Flow sona erdiğinde dinleyiciyi kapat
            awaitClose {
                // Dinleyiciyi kapat
                listenerRegistration.remove()
            }
        } else {
            // wordIds listesi boşsa, boş bir liste gönder
            trySend(emptyList())

            // Flow sona erdiğinde başka bir kaynak kapatılması gerekmiyorsa awaitClose bloğu içinde bir şey yapmaya gerek yok.
            awaitClose { }
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

    private fun checkIsThereAnyWordCard(wordCard: WordCard, callback: (String?) -> Unit) {

        val collectionRef = db.collection("wordcards")

        collectionRef
            .whereEqualTo("word", wordCard.word)
            .whereEqualTo("translate", wordCard.translate)
            .whereEqualTo("synonyms", wordCard.synonyms)
            .whereEqualTo("sentence", wordCard.sentence)
            .get()
            .addOnSuccessListener { querySnapshot ->
                var returningWordCardId: String? = "bulunmadı"
                for (document in querySnapshot.documents) {
                    returningWordCardId = document.id


                }
                callback(returningWordCardId)
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                callback("offline")
            }

    }

    fun filterWordList(wordList: List<WordCard>, query: String): List<WordCard> {
        return wordList.filter { it.word.contains(query, ignoreCase = true) }
    }

    suspend fun addWordCard(wordCard: WordCard) {
        val wordcardsCollection = db.collection("wordcards")
        val userId = getUserId()

        val returningWordCardId =
            suspendCoroutine { continuation ->//bu hazırda var olabilecek yapılmış wordid
                checkIsThereAnyWordCard(wordCard) { returningCard ->
                    continuation.resume(returningCard)
                }
            }

        if (returningWordCardId!="bulunmadı"&&returningWordCardId!="offline") {//
            if (userId != null) {
                print(returningWordCardId+"HEEEEEY")

                viewModelScope.launch {
                    addWordtoUser(
                        userId,
                        returningWordCardId!!
                    )
                }

            }
        }
        if(returningWordCardId=="bulunmadı"){
            if (userId != null) {
                print(returningWordCardId+"HEEEEEY")
                wordcardsCollection.add(wordCard)
                    .addOnSuccessListener { documentReference ->

                        // Eklenen belge (document) ID'sini alın
                        val wordId = documentReference.id
                        userId?.let {
                            db.collection("wordcards")
                                .document(wordId)
                                .set(wordCard.copy(documentId = wordId, creatorId = userId))
                                .addOnSuccessListener {
                                    println("Belge başarıyla güncellendi.")
                                }
                                .addOnFailureListener { e ->
                                    println("Belge güncelleme hatası: $e")
                                }
                        }

                        viewModelScope.launch {
                            addWordtoUser(
                                userId,
                                wordId
                            )
                            updateCards()
                        }//bu ıd zaten var mı kontrol et
                    }
                    .addOnFailureListener { e ->

                    }
            }

        //viewModelScope.launch { deleteWordCard(_viewingWorCard.value.documentId) }

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

    suspend fun addWordtoUser(userId: String, wordId: String) {
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

    fun searchWordCardOnline(s: String) {
        // val wordListFlow = MutableStateFlow(listOf<WordCard>())

        viewModelScope.launch {
            try {
                val wordlist: MutableList<WordCard> = mutableListOf()
                val documents = db.collection("wordcards")
                    .whereEqualTo("word", s)
                    .get()
                    .await()  // await ile asenkron işlem tamamlanmasını bekleyin

                for (document in documents) {
                    wordlist.add(document.toObject(WordCard::class.java))
                    println("kelimeler geliyor")
                }


                _wordcardSearchStateFlow.value = wordlist
            } catch (exception: Exception) {
                println("hata: $exception")
            }
        }
    }

    fun updateShredPrefWordCards(context: Context) {
        val sharedPreferencesHelper = MySharedPreferences(context)
        // _sharedPrefWordCards.value=sharedPreferencesHelper.getWordCardList()
    }


    fun saveOfflineWordCard(wordCard: WordCard) {
        wordCardUserId?.let { kullaniciId ->
            // Firestore'da kullanılacak benzersiz bir belge kimliği al
            val belgeId = db.collection("users").document(kullaniciId)
                .collection("offlinewordcards").document().id

            // Belge kimliğini içeren WordCard nesnesini güncelle
            val belgeIdliWordCard = wordCard.copy(documentId = belgeId)

            // WordCard'ı Firestore'a ekle
            db.collection("users").document(kullaniciId)
                .collection("offlinewordcards").document(belgeId)
                .set(belgeIdliWordCard)
                .addOnSuccessListener {
                    println("Belge başarıyla eklendi: ")
                }
                .addOnFailureListener { hata ->
                    println("Belge eklenirken hata oluştu: $hata")
                }
        }
    }


    fun updateOfflineWordCardsFlow(): Flow<List<WordCard>> = callbackFlow{
        val docRef = db.collection("users").document(wordCardUserId!!)
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
                        document.toObject(WordCard::class.java)?.let { wordlist.add(it) }
                        println("kelimeler geliyor")
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
    suspend fun listenOfflineWordCards() {

        updateOfflineWordCardsFlow().collect { data ->
            // StateFlow içindeki veriyi güncelleyerek UI'a otomatik olarak yansıtın
            _offlineWordCards.value = data


        }
    }

}


