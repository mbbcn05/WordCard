package com.babacan05.wordcard.presentation.card



import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacan05.wordcard.common.MySharedPreferences
import com.babacan05.wordcard.common.Settings
import com.babacan05.wordcard.common.isInternetAvailable
import com.babacan05.wordcard.common.readByteArrayFromFileUri
import com.babacan05.wordcard.model.WordCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class CardViewModel :ViewModel() {
    private val _viewingWorCard = MutableStateFlow(value = WordCard())
    val viewingWorCard: StateFlow<WordCard> get() = _viewingWorCard.asStateFlow()
    private val _wordcardSearchStateFlow = MutableStateFlow<List<WordCard>>(value = emptyList())
    val wordcardSearchstateFlow: StateFlow<List<WordCard>> get() = _wordcardSearchStateFlow.asStateFlow()
    private val _offlineWordCards = MutableStateFlow<List<WordCard>>(value = emptyList())
    val offlineWordCards: StateFlow<List<WordCard>> get() = _offlineWordCards.asStateFlow()
    val wordCardUserId: String = getUserId() ?: "26152122810forgoogle"


    private val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    init {

        viewModelScope.launch {
            listenOfflineWordCards()
        }


    }
fun getSettings(context: Context)=MySharedPreferences(context).getSettings()
    fun uploadSettings(settings: Settings,context: Context){
        MySharedPreferences(context = context).updateSettings(settings)
    }

    suspend fun uploadImageToCloud(imageByteArray: ByteArray):String {


        val storageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")
        val uploadTask = storageRef.putBytes(imageByteArray)

        try {
            val uploadResult = uploadTask.await()

            val downloadUrl = storageRef.downloadUrl.await()




           return downloadUrl.toString()
        }
        catch (e: StorageException) {


            e.printStackTrace()
            return ""
        }catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
    fun updateViewingWordCard(wordCard: WordCard) {
        _viewingWorCard.value = wordCard
    }

    suspend fun deleteWordCard(wordcardId: String) {
        try {


            db.collection("users").document(wordCardUserId!!).collection("offlinewordcards")
                .document(viewingWorCard.value.documentId).delete().await()


                wordCardUserId.let {
                    val userRef = db.collection("users").document(it)
                    userRef.update("wordIdList", FieldValue.arrayRemove(wordcardId))
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                        }
                }
                delay(1000)


            } catch (e:Exception){

            }


    }







    suspend fun saveWordCard(wordcard: WordCard,creator:Boolean) {



         if(viewingWorCard.value.addingMode==""){

            saveOfflineWordCard(wordCard = wordcard.copy(addingMode = "offline"))

         }else  if(viewingWorCard.value.addingMode=="offline") {
            if (creator){
             updateofflineWordCard(wordcard.copy(addingMode = "offline"))
            } else{
                 deleteWordCard(wordcard.documentId)
                saveOfflineWordCard(wordcard.copy(addingMode = "offline"))

             }
         }else
             updateofflineWordCard(wordcard.copy(addingMode = "offline"))




    }

     fun updateofflineWordCard(wordcard: WordCard) {
        if (wordCardUserId != null) {
            db.collection("users").document(wordCardUserId)
                .collection("offlinewordcards").document(wordcard.documentId)
                .set(wordcard)
        }
    }

    fun updateWordCard(wordCard: WordCard) {


        try {

            wordCardUserId?.let {
                if (wordCard.documentId.isNotBlank()) {
                    // Firestore update operation
                    db.collection("wordcards")
                        .document(wordCard.documentId)
                        .set(wordCard)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                        }
                } else {
                }
            }
        } catch (e: Exception) {
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
            .whereEqualTo("imageUrl", wordCard.imageUrl)
            .get()
            .addOnSuccessListener { querySnapshot ->
                var returningWordCardId: String? = "bulunmadı"
                for (document in querySnapshot.documents) {
                    returningWordCardId = document.id


                }
                callback(returningWordCardId)
            }
            .addOnFailureListener { exception ->
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


        if(returningWordCardId=="bulunmadı"){
            if (userId != null) {
                wordcardsCollection.add(wordCard)
                    .addOnSuccessListener { documentReference ->

                        // Eklenen belge (document) ID'sini alın
                        val wordId = documentReference.id
                        userId?.let {
                            db.collection("wordcards")
                                .document(wordId)
                                .set(wordCard.copy(documentId = wordId, creatorId = userId))
                                .addOnSuccessListener {
                                }
                                .addOnFailureListener { e ->
                                }
                        }


                    }
                    .addOnFailureListener { e ->

                    }
            }



    }
    }



fun getİsLearned(wordcardId: String,callBack:(Boolean)->Unit){

    if (wordCardUserId != null) {
        db.collection("users")
            .document(wordCardUserId)
            .collection("offlinewordcards")
            .document(wordcardId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val fieldValue = documentSnapshot.getBoolean("isLearned")
                    if (fieldValue != null) {
                       callBack(fieldValue)

                    }
                }
            }
            .addOnFailureListener { e ->

            }
    }

}


    fun searchWordCardOnline(s: String) {


        viewModelScope.launch {
            try {
                val wordlist: MutableList<WordCard> = mutableListOf()
                val documents = db.collection("wordcards")
                    .whereEqualTo("word", s)
                    .get()
                    .await()  // await ile asenkron işlem tamamlanmasını bekleyin

                for (document in documents) {
                    wordlist.add(document.toObject(WordCard::class.java))
                }


                _wordcardSearchStateFlow.value = wordlist
            } catch (exception: Exception) {
            }
        }
    }




    fun saveOfflineWordCard(wordCard: WordCard) {
        wordCardUserId?.let { kullaniciId ->

            val belgeId = db.collection("users").document(kullaniciId)
                .collection("offlinewordcards").document().id


            val belgeIdliWordCard = wordCard.copy(documentId = belgeId, creatorId = wordCardUserId)


            db.collection("users").document(kullaniciId)
                .collection("offlinewordcards").document(belgeId)
                .set(belgeIdliWordCard)
                .addOnSuccessListener {
                }
                .addOnFailureListener { hata ->
                }
        }
    }


 suspend fun updateOfflineWordCardsFlow(): Flow<List<WordCard>> = callbackFlow{
        val docRef = db.collection("users").document(wordCardUserId!!)
            .collection("offlinewordcards")

       val listener= docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {

                try {
                    val wordlist: MutableList<WordCard> = mutableListOf()


                    for (document in snapshot.documents) {
                        document.toObject(WordCard::class.java)?.let { word ->
                            wordlist.add(word)


                            }



                        }


                trySend(wordlist)


                } catch (exception: Exception) {
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

            _offlineWordCards.value = data



        }
    }
     suspend fun migrateCardsIntoOnline(context: Context,wordList:List<WordCard>){

        if(isInternetAvailable(context = context)&& wordList.isNotEmpty()) {

        val deletingwordList=wordList.map { it }.toList()
        for (wordcard in deletingwordList){
            var imageUrl=wordcard.imageUrl

            if(wordcard.imageUrl.startsWith("file")){
                imageUrl= readByteArrayFromFileUri(wordcard.imageUrl)?.let { uploadImageToCloud(it) }?:wordcard.imageUrl
                delay(2000)
            }

            _viewingWorCard.value= WordCard()
            updateofflineWordCard(wordcard.copy(addingMode = "offline", imageUrl = imageUrl, updateMode = false))
            saveOnlineWordCard(wordcard.copy(addingMode = "online", imageUrl = imageUrl, updateMode = false))
            delay(1000)


        }

        }

    }

    private fun saveOnlineWordCard(wordCard: WordCard) {
        try {

            wordCardUserId?.let {
                if (wordCard.documentId.isNotBlank()) {

                    db.collection("wordcards")
                        .document(wordCard.documentId)
                        .set(wordCard.copy(learning = "false"))
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener { e ->

                        }
                }
            }
        } catch (e: Exception) {

        }


    }

}
