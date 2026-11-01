package com.example.util

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "EN"),
    URDU("ur", "اردو")
}

object Strings {
    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Smart Charging Checker"
        AppLanguage.URDU -> "سمارٹ چارجنگ چیکر"
    }

    fun appSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Voice Alert & Cable Diagnostics"
        AppLanguage.URDU -> "آواز الرٹ اور کیبل تشخیصی نظام"
    }

    // Tabs
    fun tabHome(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Home"
        AppLanguage.URDU -> "ہوم"
    }

    fun tabStats(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Stats"
        AppLanguage.URDU -> "ریکارڈ"
    }

    fun tabGuide(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Rehnumai"
        AppLanguage.URDU -> "رہنمائی"
    }

    fun tabAbout(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "About"
        AppLanguage.URDU -> "معلومات"
    }

    // Gauge & Meter
    fun charging(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Charging"
        AppLanguage.URDU -> "چارج ہو رہا ہے"
    }

    fun discharging(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Discharging"
        AppLanguage.URDU -> "ڈسچارج ہو رہا ہے"
    }

    fun full(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Full Battery"
        AppLanguage.URDU -> "مکمل چارج"
    }

    fun notCharging(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Not Charging"
        AppLanguage.URDU -> "چارج نہیں ہو رہا"
    }

    fun currentMa(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Current Flow"
        AppLanguage.URDU -> "کرنٹ کی روانی"
    }

    fun minMa(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Min"
        AppLanguage.URDU -> "کم ترین"
    }

    fun maxMa(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Max"
        AppLanguage.URDU -> "زیادہ ترین"
    }

    fun avgMa(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Avg"
        AppLanguage.URDU -> "اوسط"
    }

    // 4 Cards
    fun batteryLevel(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Battery Level"
        AppLanguage.URDU -> "بیٹری لیول"
    }

    fun temperature(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Temperature"
        AppLanguage.URDU -> "درجہ حرارت"
    }

    fun voltage(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Voltage"
        AppLanguage.URDU -> "وولٹیج"
    }

    fun batteryHealth(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Battery Health"
        AppLanguage.URDU -> "بیٹری کی حالت"
    }

    // Health states
    fun healthGood(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Good"
        AppLanguage.URDU -> "بہترین"
    }

    fun healthOverheat(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Overheat"
        AppLanguage.URDU -> "بہت گرم"
    }

    fun healthDead(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Dead"
        AppLanguage.URDU -> "خراب / ناکارہ"
    }

    fun healthOverVoltage(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Over Voltage"
        AppLanguage.URDU -> "زیادہ وولٹیج"
    }

    fun healthCold(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Cold"
        AppLanguage.URDU -> "ٹھنڈی"
    }

    // Cable Quality
    fun fastCharging(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Fast Charging"
        AppLanguage.URDU -> "فاسٹ چارجنگ"
    }

    fun normalCharging(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Normal Charging"
        AppLanguage.URDU -> "نارمل چارجنگ"
    }

    fun slowOrFaulty(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Faulty Cable / Slow Charger"
        AppLanguage.URDU -> "خراب کیبل یا سلو چارجر"
    }

    fun cableStatus(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Cable Assessment"
        AppLanguage.URDU -> "کیبل کا معیار"
    }

    // Time estimate
    fun estTimeToFull(lang: AppLanguage, minutes: Int) = when (lang) {
        AppLanguage.ENGLISH -> if (minutes > 0) "Est. $minutes min until full" else "Calculating time..."
        AppLanguage.URDU -> if (minutes > 0) "مکمل چارج ہونے میں تقریباً $minutes منٹ" else "وقت کا حساب لگایا جا رہا ہے..."
    }

    fun estTimeToDischarge(lang: AppLanguage, minutes: Int) = when (lang) {
        AppLanguage.ENGLISH -> if (minutes > 0) "Est. remaining: ${minutes / 60}h ${minutes % 60}m" else "Estimating battery life..."
        AppLanguage.URDU -> if (minutes > 0) "باقی بیٹری: ${minutes / 60} گھنٹے ${minutes % 60} منٹ" else "بیٹری لائف کا حساب لگایا جا رہا ہے..."
    }

    // Voice Alert strings
    fun voiceFastUrdu() = "Charging cable achi hai, fast charging ho rahi hai"
    fun voiceNormalUrdu() = "Charging normal hai"
    fun voiceSlowUrdu() = "Charging cable kharab hai ya charger slow hai"

    fun voiceFastEnglish() = "Charging cable is good, fast charging"
    fun voiceNormalEnglish() = "Charging is normal"
    fun voiceSlowEnglish() = "Charging cable is faulty or charger is slow"

    fun voiceTimeUrdu(minutes: Int) = "Mukammal charge hone mein taqreeban $minutes minute lagenge."
    fun voiceTimeEnglish(minutes: Int) = "Estimated time to full charge is $minutes minutes."

    fun analyzingCountdown(lang: AppLanguage, seconds: Int) = when (lang) {
        AppLanguage.ENGLISH -> "Analyzing cable in ${seconds}s..."
        AppLanguage.URDU -> "کیبل کی جانچ ${seconds} سیکنڈ میں..."
    }

    fun testVoiceBtn(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Test Voice Alert"
        AppLanguage.URDU -> "آواز الرٹ چیک کریں"
    }

    fun simulationMode(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Cable Testing Sandbox"
        AppLanguage.URDU -> "کیبل ٹیسٹنگ سیمولیشن"
    }

    fun simulationHelp(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Simulate plugging different chargers to test voice alerts & speed checks."
        AppLanguage.URDU -> "مختلف چارجرز اور کیبلز لگا کر آواز کے الرٹ اور سپیڈ چیک کیجیے۔"
    }

    // Guide (Rehnumai)
    fun guidePageTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Rehnumai / Guide - How This App Works"
        AppLanguage.URDU -> "رہنمائی / گائیڈ - یہ ایپ کس اصول پر کام کرتی ہے"
    }

    fun guideHeader(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "How This App Works"
        AppLanguage.URDU -> "اس ایپ کے کام کرنے کا طریقہ"
    }

    fun guideSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Understand electric current, charging cables, and battery longevity principles."
        AppLanguage.URDU -> "الیکٹرک کرنٹ، چارجنگ کیبلز اور بیٹری کی طویل عمر کے اصول سمجھیں۔"
    }

    fun point1(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "This app precisely measures the real electric current (mA) entering or leaving the battery."
        AppLanguage.URDU -> "یہ ایپ بیٹری کے اندر جانے والے کرنٹ (mA) کو ناپتی ہے۔"
    }

    fun point2(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "If charging current is high (+1500mA or more), it confirms both the charger adapter and cable are of top quality for fast charging."
        AppLanguage.URDU -> "اگر کرنٹ زیادہ ہے (+1500mA) تو اس کا مطلب چارجر اور کیبل دونوں اچھے ہیں۔"
    }

    fun point3(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "If current is low (less than +500mA or +800mA), the cable is faulty/damaged, or the charger has insufficient wattage."
        AppLanguage.URDU -> "اگر کرنٹ کم ہے (+500mA سے کم) تو کیبل خراب ہے یا چارجر کم واٹ کا ہے۔"
    }

    fun point4(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Voltage and temperature levels provide crucial indicators of internal battery health and prevent overheating."
        AppLanguage.URDU -> "وولٹیج اور ٹمپریچر سے بیٹری کی ہیلتھ کا پتہ چلتا ہے۔"
    }

    fun point5(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Formula: Remaining Battery Capacity (mAh) ÷ Charging Current (mA) = Time to Full Charge."
        AppLanguage.URDU -> "فارمولا: باقی بیٹری / چارجنگ کرنٹ = مکمل چارج ہونے کا وقت۔"
    }

    // Stats
    fun statsTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Charging Logs & Telemetry"
        AppLanguage.URDU -> "چارجنگ ریکارڈ اور تاریخچہ"
    }

    fun statsSessionLogs(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Recent Diagnostic Snapshots"
        AppLanguage.URDU -> "حالیہ ٹیسٹ ریکارڈ"
    }

    fun clearLogs(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Clear Logs"
        AppLanguage.URDU -> "ریکارڈ صاف کریں"
    }

    fun logCurrentSession(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Save Current Reading"
        AppLanguage.URDU -> "موجودہ ریڈنگ محفوظ کریں"
    }

    fun noLogsYet(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "No saved logs yet. Plug in a charger to record charging speed."
        AppLanguage.URDU -> "ابھی تک کوئی ریکارڈ نہیں۔ چارجر لگا کر ریڈنگ محفوظ کریں۔"
    }

    // About
    fun aboutTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "About the App"
        AppLanguage.URDU -> "ایپ کے بارے میں"
    }

    fun appNameVersion(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Smart Charging Checker - Voice Alert v1.0"
        AppLanguage.URDU -> "سمارٹ چارجنگ چیکر - وائس الرٹ ورژن 1.0"
    }

    fun developerName(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Rao Muhammad Sarfraz"
        AppLanguage.URDU -> "راؤ محمد سرفراز"
    }

    fun developerTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Lead Android & Flutter Specialist"
        AppLanguage.URDU -> "لیڈ اینڈرائڈ اور فلٹر سپیشلسٹ"
    }

    fun whatsappBtn(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Chat on WhatsApp (+923360074976)"
        AppLanguage.URDU -> "واٹس ایپ پر رابطہ کریں (+923360074976)"
    }

    fun facebookBtn(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Official Facebook Profile"
        AppLanguage.URDU -> "فیس بک پروفائل وزٹ کریں"
    }

    fun aboutFooter(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Developed with love in Pakistan by Rao Muhammad Sarfraz"
        AppLanguage.URDU -> "پاکستان میں راؤ محمد سرفراز کی محبت سے تیار کردہ"
    }

    fun technology(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Technology"
        AppLanguage.URDU -> "ٹیکنالوجی"
    }

    fun powerSource(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Power Source"
        AppLanguage.URDU -> "پاور سورس"
    }

    fun voiceAlertToggle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Voice Alerts"
        AppLanguage.URDU -> "آواز الرٹس"
    }
}
