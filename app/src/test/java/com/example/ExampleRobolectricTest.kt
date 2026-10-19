package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AgroCampo", appName)
  }

  @Test
  fun `verify planting population formula from reference manual`() {
    // Exemplo 1 do manual: 0,50 m entre linhas e 0,33 m entre plantas
    val linhas = 0.50
    val plantas = 0.33
    val plantasPorHa = 10000.0 / (linhas * plantas)
    assertEquals(60606.0, plantasPorHa, 1.0)
  }

  @Test
  fun `verify seed requirement with germination and field emergence`() {
    // Exemplo B do manual: alvo 260.000, germinação 90%, emergência 85%
    val popAlvo = 260000.0
    val g = 0.90
    val e = 0.85
    val sementesHa = popAlvo / (g * e)
    assertEquals(339869.0, sementesHa, 1.0)
  }

  @Test
  fun `verify moisture correction formula`() {
    // Exemplo 5.5 do manual: 1.000 kg medidos a 20% corrigidos para 14%
    val massaMedida = 1000.0
    val umidadeMedida = 20.0
    val umidadePadrao = 14.0
    val massaCorrigida = massaMedida * (100.0 - umidadeMedida) / (100.0 - umidadePadrao)
    assertEquals(930.23, massaCorrigida, 0.1)
  }

  @Test
  fun `verify harvest loss frame calculation`() {
    // Exemplo 6.3 do manual: 0,018 kg em 2 m²
    val massa = 0.018
    val area = 2.0
    val perdaKgHa = (massa / area) * 10000.0
    val perdaSacas = perdaKgHa / 60.0
    assertEquals(90.0, perdaKgHa, 0.01)
    assertEquals(1.5, perdaSacas, 0.01)
  }
}
