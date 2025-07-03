package com.example.promptiq.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.BufferedReader
import java.io.InputStreamReader

// PDF y Word
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.InputStream


object FileUtils {

    fun leerContenidoDesdeUri(context: Context, uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri) ?: return ""
        return when {
            mimeType == "text/plain" -> leerTxt(context, uri)
            mimeType == "application/pdf" -> leerPdf(context, uri)
            mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> leerDocx(context, uri)
            else -> ""
        }
    }

    private fun leerTxt(context: Context, uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).readText()
        } ?: ""
    }

    private fun leerPdf(context: Context, uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            val text = stripper.getText(document)
            document.close()
            text
        } ?: ""
    }

    private fun leerDocx(context: Context, uri: Uri): String {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val doc = org.apache.poi.xwpf.usermodel.XWPFDocument(inputStream)
                val text = doc.paragraphs.joinToString("\n") { it.text }
                doc.close()
                text
            } ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }



}
