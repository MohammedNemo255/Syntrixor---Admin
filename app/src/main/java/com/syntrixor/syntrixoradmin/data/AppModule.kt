package com.syntrixor.syntrixoradmin.data

import com.syntrixor.syntrixoradmin.data.model.Admin
import com.syntrixor.syntrixoradmin.data.repository.IAdminRepository
import com.syntrixor.syntrixoradmin.data.repository.MockAdminRepository
import com.syntrixor.syntrixoradmin.utils.FeatureFlags

object AppModule {
    val repository: IAdminRepository by lazy {
        if (FeatureFlags.USE_REAL_API) {
            // RealAdminRepository()  — swap in when backend is ready
            MockAdminRepository()
        } else {
            MockAdminRepository()
        }
    }

    var currentAdmin: Admin? = null
}
