/**
* Copyright Soramitsu Co., Ltd. All Rights Reserved.
* SPDX-License-Identifier: GPL-3.0
*/

package jp.co.soramitsu.common.resourses

import android.content.Context
import jp.co.soramitsu.common.util.SingletonHolder
import java.util.Locale
import javax.inject.Singleton

@Singleton
class ContextManager private constructor(
    private var context: Context,
    private val languagesHolder: LanguagesHolder
) {

    private val LANGUAGE_PART_INDEX = 0
    private val COUNTRY_PART_INDEX = 1
    private val prefsLanguage = "sora_prefs"
    private val prefCurrentLanguage = "current_language"

    companion object : SingletonHolder<ContextManager, Context, LanguagesHolder>(::ContextManager)

    fun getContext(): Context {
        return context
    }

    fun setLocale(context: Context): Context {
        return updateResources(context)
    }

    fun getLocale(): Locale {
        return if (Locale.getDefault().displayLanguage != "ba") Locale.getDefault() else Locale("ru")
    }

    private fun updateResources(context: Context): Context {
        val currentLanguageNullable = getCurrentLanguage()
        val currentLanguage = if (currentLanguageNullable.isNullOrEmpty()) {
            val currentLocale = Locale.getDefault()
            val result = if (languagesHolder.getLanguages().map { it.iso }.contains(currentLocale.language)) {
                currentLocale.language
            } else {
                languagesHolder.getEnglishLang().iso
            }
            setCurrentLanguage(result)
        } else {
            currentLanguageNullable
        }

        val locale = mapLanguageToLocale(currentLanguage)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        this.context = context.createConfigurationContext(configuration)

        return this.context
    }

    fun getCurrentLanguage(): String? {
        val prefs = context.getSharedPreferences(prefsLanguage, Context.MODE_PRIVATE)
        return prefs.getString(prefCurrentLanguage, null)
    }

    fun setCurrentLanguage(l: String): String {
        val prefs = context.getSharedPreferences(prefsLanguage, Context.MODE_PRIVATE)
        prefs.edit().putString(prefCurrentLanguage, l).apply()
        return l
    }

    private fun mapLanguageToLocale(language: String): Locale {
        val codes = language.split("_")

        return if (hasCountryCode(codes)) {
            Locale(codes[LANGUAGE_PART_INDEX], codes[COUNTRY_PART_INDEX])
        } else {
            Locale(language)
        }
    }

    private fun hasCountryCode(codes: List<String>) = codes.size != 1
}
