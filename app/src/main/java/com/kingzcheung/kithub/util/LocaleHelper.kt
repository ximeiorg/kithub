package com.kingzcheung.kithub.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.kingzcheung.kithub.data.store.AppLanguage
import java.util.Locale

object LocaleHelper {
    
    fun wrapContext(context: Context, language: AppLanguage): Context {
        val locale = getLocale(language)
        return updateResources(context, locale)
    }
    
    fun getLocale(language: AppLanguage): Locale {
        return when (language) {
            AppLanguage.ENGLISH -> Locale.ENGLISH
            AppLanguage.CHINESE -> Locale.CHINESE
            AppLanguage.SYSTEM -> getSystemLocale()
        }
    }
    
    private fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale.getDefault()
        } else {
            @Suppress("DEPRECATION")
            Locale.getDefault()
        }
    }
    
    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
}