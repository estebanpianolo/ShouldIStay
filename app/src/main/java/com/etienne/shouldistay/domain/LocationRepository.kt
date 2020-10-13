package com.etienne.shouldistay.domain

import com.etienne.libraries.pratik.compat.Optional
import com.etienne.shouldistay.domain.Location
import io.reactivex.rxjava3.core.Single

interface LocationRepository {
    fun getLastKnownLocation(): Single<Optional<Location>>
}
