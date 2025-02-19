package ru.dekabrsky.feature.notifications.implementation.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import io.reactivex.Completable
import io.reactivex.Single
import main.utils.orZero
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import ru.dekabrsky.easylife.basic.dateTime.tryParseDate
import ru.dekabrsky.feature.notifications.implementation.domain.model.RestOfPillEntity
import javax.inject.Inject

class RestOfPillRepository @Inject constructor(private val context: Context) {

    private var db: FirebaseFirestore? = null

    init {
        FirebaseApp.initializeApp(context)
        db = Firebase.firestore
    }

    fun getRestOfPillList(userId: Long): Single<List<RestOfPillEntity>> =
        Single.create { emitter ->
            db?.collection(getCollectionName(userId))
                ?.get()
                ?.addOnSuccessListener { result ->
                    emitter.onSuccess(
                        result.map { document ->
                            Log.d("YEAH", tryParseDate(
                                document.data[KEY_END_DATE].toString(),
                                DATE_FORMAT
                            ).toString())
                            RestOfPillEntity(
                                document.id,
                                name = document.data[KEY_NAME].toString(),
                                inDayCount = document.data[KEY_IN_DAY_COUNT].toString().toIntOrNull().orZero(),
                                endDate = tryParseDate(
                                    document.data[KEY_END_DATE].toString(),
                                    DATE_FORMAT
                                )?.minusDays(1) ?: LocalDate.now() // todo
                            )
                        }
                    )
                }
                ?.addOnFailureListener { exception ->
                    emitter.onError(exception)
                }
        }

    fun addRestOfPill(userId: Long, entity: RestOfPillEntity): Completable =
        Completable.create { emmiter ->
            db?.collection(getCollectionName(userId))
                ?.add(mapEntityToMap(entity))
                ?.addOnSuccessListener { emmiter.onComplete() }
                ?.addOnFailureListener { emmiter.onError(it) }
        }

    fun updateRestOfPill(userId: Long, entity: RestOfPillEntity): Completable {
        entity.id ?: return Completable.complete()
        return Completable.create { emmiter ->
            db?.collection(getCollectionName(userId))
                ?.document(entity.id)
                ?.update(mapEntityToMap(entity))
                ?.addOnSuccessListener { emmiter.onComplete() }
                ?.addOnFailureListener { emmiter.onError(it) }
        }
    }

    private fun mapEntityToMap(entity: RestOfPillEntity) =
        mapOf(
            KEY_NAME to entity.name,
            KEY_IN_DAY_COUNT to entity.inDayCount,
            KEY_END_DATE to DateTimeFormatter.ofPattern(DATE_FORMAT).format(entity.endDate)
        )

    fun deleteRestOfPill(userId: Long, restOfPillId: String): Completable =
        Completable.create { emmiter ->
            db?.collection(getCollectionName(userId))
                ?.document(restOfPillId)
                ?.delete()
                ?.addOnSuccessListener { emmiter.onComplete() }
                ?.addOnFailureListener { emmiter.onError(it) }
        }

    private fun getCollectionName(userId: Long) = "pills_remains_$userId"

    companion object {
        private const val KEY_NAME = "name"
        private const val KEY_IN_DAY_COUNT = "inDayCount"
        private const val KEY_END_DATE = "endDate"

        private const val DATE_FORMAT = "dd.MM.yyyy"
    }
}