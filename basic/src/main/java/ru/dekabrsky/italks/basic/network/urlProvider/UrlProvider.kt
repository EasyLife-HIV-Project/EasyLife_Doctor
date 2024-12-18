package ru.dekabrsky.easylife.basic.network.urlProvider

interface UrlProvider {
    fun provideServerUrl(): String
    fun provideServerUrlPostfix(): String
    fun provideBaseUrl(): String = provideServerUrl() + provideServerUrlPostfix()
}