package com.babacan05.wordcard.presentation.card



import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babacan05.wordcard.common.FileUtil
import com.babacan05.wordcard.common.MySharedPreferences
import com.babacan05.wordcard.common.isInternetAvailable
import com.babacan05.wordcard.common.readByteArrayFromFileUri
import com.babacan05.wordcard.model.WordCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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

    suspend fun uploadImageToCloud(imageByteArray: ByteArray):String {


        // Resmi Firestore Storage'a yükleyin (Opsiyonel)
        val storageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")
        val uploadTask = storageRef.putBytes(imageByteArray)

        try {
            // Yükleme işlemi tamamlandığında
            val uploadResult = uploadTask.await()

            // Resmin indirme URL'sini alın
            val downloadUrl = storageRef.downloadUrl.await()




           return downloadUrl.toString()
        }
        catch (e: StorageException) {
            val innerException = e.cause
            println("Inner Exception: ${innerException?.message}")
            println("HTTP Result Code: ${e.httpResultCode}")
            e.printStackTrace()
            return ""
        }catch (e: Exception) {
            println("Resim yüklenirken hata oluştu: $e")
            e.printStackTrace() // Hata ayrıntılarını ekrana bas
            println("Hata Mesajı: ${e.message}")
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
                            println("Veri başarıyla eklendi.")
                        }
                        .addOnFailureListener { e ->
                            println("Veri eklenirken bir hata oluştu: $e")
                        }
                }
                delay(1000)

               // Toast.makeText(context, "Bu işleminiz online olduğunuzda otomatik gerçekleşecektir", Toast.LENGTH_SHORT).show()

            } catch (e:Exception){
                print(e.toString())
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
            println("çözmen gereken Bir hata oluştu: $e")
            // Handle the exception as needed
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

            }
            ?.addOnFailureListener { e ->
                println("Belge okunurken bir hata oluştu: $e")
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
print("CALLBACK BAŞARILI$fieldValue")

                    }
                }
            }
            .addOnFailureListener { e ->

            }
    }

}
fun updateIsLearned(wordCard:WordCard,isLearned:String,context: Context) {

    val updateData = hashMapOf(
        "isLearned" to isLearned
    )

    if (wordCardUserId != null) {
        db.collection("users")
            .document(wordCardUserId)
            .collection("offlinewordcards")
            .document(wordCard.documentId)
            .update(updateData as Map<String, Any>)
            .addOnSuccessListener {
               // _viewingWorCard.value=_viewingWorCard.value.copy(isLearned=isLearned)
                if(isLearned=="true") {

                    Toast.makeText(
                        context,
                        "The WordCard is marked as learned",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    Toast.makeText(
                        context,
                        "The WordCard is marked as being studied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->

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




    fun saveOfflineWordCard(wordCard: WordCard) {
        wordCardUserId?.let { kullaniciId ->
            // Firestore'da kullanılacak benzersiz bir belge kimliği al
            val belgeId = db.collection("users").document(kullaniciId)
                .collection("offlinewordcards").document().id

            // Belge kimliğini içeren WordCard nesnesini güncelle
            val belgeIdliWordCard = wordCard.copy(documentId = belgeId, creatorId = wordCardUserId)

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


 suspend fun updateOfflineWordCardsFlow(): Flow<List<WordCard>> = callbackFlow{
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
    suspend fun listenOfflineWordCards() {

        updateOfflineWordCardsFlow().collect { data ->
            // StateFlow içindeki veriyi güncelleyerek UI'a otomatik olarak yansıtın
            _offlineWordCards.value = data



        }
    }
     suspend fun migrateCardsIntoOnline(context: Context,wordList:List<WordCard>){

        if(isInternetAvailable(context = context)&& wordList.isNotEmpty()) {

        val deletingwordList=wordList.map { it }.toList()
        for (wordcard in deletingwordList){
            var imageUrl= ""

            if(wordcard.imageUrl.isNotEmpty()){
                imageUrl= readByteArrayFromFileUri(wordcard.imageUrl)?.let { uploadImageToCloud(it) }?:wordcard.imageUrl
                delay(2000)
            }

            print("işlem"+wordcard.documentId)
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
                    // Firestore update operation
                    db.collection("wordcards")
                        .document(wordCard.documentId)
                        .set(wordCard.copy(learning = "false"))
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
            println("çözmen gereken Bir hata oluştu: $e")
            // Handle the exception as needed
        }


    }

}
