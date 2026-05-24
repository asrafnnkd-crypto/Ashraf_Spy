package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val MODEL_NAME = "gemini-3.5-flash"

    suspend fun getArabicReply(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "تنبيه ⚠️: يبدو أنك لم تقم بإدخال مفتاح الـ API الخاص بـ Gemini حتى الآن. يرجى التوجه إلى قسم 'Secrets' (الأسرار) في إعدادات Google AI Studio ثم إضافة متغير جديد باسم 'GEMINI_API_KEY' لتفعيل ميزات الرد والذكاء الاصطناعي بنجاح!"
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
        
        val systemInstruction = "أنت مساعد ذكي وتفاعلي مدمج في تطبيق ياسين تي في (Yacine TV Assistant). " +
                "مهمتك الرئيسية هي الإجابة عن استفسارات المستخدمين باللغة العربية بأسلوب رياضي حماسي، رائع ولبق ومفعم بروح كرة القدم! " +
                "تفضل الردود المباشرة والمليئة بالحيوية والإيموجي الرياضية ⚽️🏆. " +
                "تستطيع مساعدة المستخدمين في معرفة جدول مباريات اليوم، القنوات المتاحة في تطبيق ياسين، " +
                "أخبار كرة القدم، التوقعات الرياضية، والتعليمات. إذا سألك المستخدم من أنت، أجب بفخر وسرور أنك مساعد ياسين الذكي!"

        try {
            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val systemInstructionObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", systemInstruction)
                        }
                        put(partObj)
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", systemInstructionObj)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string() ?: ""
                    Log.e(TAG, "API failure code: ${response.code} error: $errorBody")
                    return@withContext "حذث خطأ أثناء معالجة الطلب (كود الخطأ: ${response.code}). يرجى التحقق من صحة مفتاح API ومستواك في الأسرار."
                }

                val responseBody = response.body?.string() ?: return@withContext "عذراً، لم يتمكن السيرفر من معالجة هذا الاستفسار حالياً."
                
                val rootResp = JSONObject(responseBody)
                val candidates = rootResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "لم أستطع تجميع الإجابة المطلوبة.")
                    }
                }
                return@withContext "لم أحصل على أي إجابة مفيدة من الذكاء الاصطناعي. يرجى إعادة الصياغة."
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network call failed", e)
            return@withContext "يبدو أن هناك مشكلة في الاتصال بالإنترنت 🌐. يرجى التحقق من اتصالك والمحاولة مرة أخرى."
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            return@withContext "حصل خطأ غير متوقع: ${e.localizedMessage}"
        }
    }
}
