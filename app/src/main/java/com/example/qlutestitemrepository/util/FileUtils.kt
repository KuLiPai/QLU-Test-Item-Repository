package com.example.qlutestitemrepository.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

object FileUtils {

    fun openFile(context: Context, fileName: String, remoteUrl: String) {
        val file = getTestsFile(fileName)
        if (file.exists()) {
            try {
                val uri = getFileUri(context, file)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, getMimeType(file))
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "无法打开本地文件，尝试在线预览", Toast.LENGTH_SHORT).show()
                openRemote(context, remoteUrl)
            }
        } else {
            openRemote(context, remoteUrl)
        }
    }

    private fun openRemote(context: Context, url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    suspend fun saveToUri(context: Context, url: String, uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    URL(url).openStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun downloadToTests(context: Context, url: String, fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val destFile = getTestsFile(fileName)
                // Ensure directory exists
                destFile.parentFile?.mkdirs()
                
                URL(url).openStream().use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun isFileDownloaded(fileName: String): Boolean {
        return getTestsFile(fileName).exists()
    }

    fun getTestsFile(fileName: String): File {
        // Use Downloads/tests folder
        val testsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "tests")
        return File(testsDir, fileName)
    }

    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    private fun getMimeType(file: File): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath) ?: "pdf"
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/pdf"
    }

    fun shareFile(context: Context, fileName: String, platform: SharePlatform) {
        val file = getTestsFile(fileName)
        if (!file.exists()) {
            Toast.makeText(context, "文件未找到", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri = getFileUri(context, file)
            val mimeType = getMimeType(file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val packageName = when (platform) {
                SharePlatform.QQ -> "com.tencent.mobileqq"
                SharePlatform.WECHAT -> "com.tencent.mm"
            }

            intent.setPackage(packageName)

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "未安装该应用，使用系统分享", Toast.LENGTH_SHORT).show()
                val chooser = Intent.createChooser(intent, "分享文件")
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "分享失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

enum class SharePlatform {
    QQ, WECHAT
}
