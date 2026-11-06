package ai.thanasakis.nothotdog.ui

import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry

internal fun string(@StringRes id: Int, vararg args: Any): String =
    InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)
