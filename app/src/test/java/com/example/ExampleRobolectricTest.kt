package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BatteryInfo
import com.example.data.model.CableQuality
import com.example.util.AppLanguage
import com.example.util.Strings
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Smart Charging", appName)
  }

  @Test
  fun `cable quality classification`() {
    val fastBattery = BatteryInfo(isCharging = true, currentNowMa = 1800)
    assertEquals(CableQuality.FAST, fastBattery.cableQuality)

    val normalBattery = BatteryInfo(isCharging = true, currentNowMa = 1200)
    assertEquals(CableQuality.NORMAL, normalBattery.cableQuality)

    val slowBattery = BatteryInfo(isCharging = true, currentNowMa = 450)
    assertEquals(CableQuality.SLOW_FAULTY, slowBattery.cableQuality)

    val dischargingBattery = BatteryInfo(isCharging = false, currentNowMa = -350)
    assertEquals(CableQuality.DISCHARGING, dischargingBattery.cableQuality)
  }

  @Test
  fun `urdu voice alert phrases verification`() {
    assertEquals("Charging cable achi hai, fast charging ho rahi hai", Strings.voiceFastUrdu())
    assertEquals("Charging normal hai", Strings.voiceNormalUrdu())
    assertEquals("Charging cable kharab hai ya charger slow hai", Strings.voiceSlowUrdu())
  }
}
