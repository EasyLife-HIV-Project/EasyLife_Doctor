package ru.dekabrsky.common.domain.interactor

import io.reactivex.Observable
import io.reactivex.Single
import ru.dekabrsky.common.domain.model.CallersBaseEntity
import ru.dekabrsky.common.domain.model.ContactEntity
import ru.dekabrsky.easylife.basic.network.utils.Direction
import ru.dekabrsky.easylife.basic.network.utils.SortVariants

interface ContactsInteractor {
    fun getCallersBases(
        direction: String = Direction.ASC.name,
        sortBy: String = SortVariants.NAME.name
    ): Observable<List<CallersBaseEntity>>

    fun getCallersBase(id: Long): Observable<CallersBaseEntity>

    fun getChildren(): Single<List<ContactEntity>>

    fun getParents(): Single<List<ContactEntity>>
}