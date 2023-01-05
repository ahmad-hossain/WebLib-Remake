package com.github.godspeed010.weblib.feature_webview.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun makeHttpsIfNeeded_convertsHttpToHttps() {
        assertThat("http://foo.com".makeHttpsIfNeeded())
            .isEqualTo("https://foo.com")
    }


    @Test
    fun makeHttpsIfNeeded_withNoHttpOrHttps_prependsHttps() {
        assertThat("foo.com".makeHttpsIfNeeded())
            .isEqualTo("https://foo.com")
    }

    @Test
    fun makeHttpsIfNeeded_withHttps_returnsHttps() {
        assertThat("https://foo.com".makeHttpsIfNeeded())
            .isEqualTo("https://foo.com")
    }

}