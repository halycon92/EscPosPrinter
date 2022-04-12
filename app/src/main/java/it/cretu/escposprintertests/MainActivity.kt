package it.cretu.escposprintertests

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import it.cretu.escposprinter.EscPosPrinter
import it.cretu.escposprinter.enums.*
import it.cretu.escposprinter.model.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnExecute : Button = findViewById(R.id.btnExecute)

        btnExecute.setOnClickListener{
            val printThread = Thread {
                try {
                    val printer = EscPosPrinter("192.168.1.22", 9100)
                    val random = Random()
                    val separatorLine : EscPosSeparatorLine = EscPosSeparatorLine.build(EscPosSeparatorType.DASH, true)
                    printer.addLine(separatorLine)
                    val textLine : EscPosTextLine = EscPosTextLine.build("Random Integer: ${random.nextInt()}", EscPosAlignment.CENTER, EscPosLineSize.DOUBLE_HEIGHT, EscPosFontStyle.BOLD)
                    printer.addLine(textLine)
                    printer.addLine(separatorLine)
                    val qrCodeLine : EscPosQrCodeLine = EscPosQrCodeLine.build("https://www.google.com", EscPosQrCodeCorrectionLevel.L, EscPosQrCodeSize.MEDIUM,
                                                                               EscPosAlignment.CENTER, false)
                    printer.addLine(qrCodeLine)

                    val barcodeLine = EscPosBarcodeLine.build("0423383051382", 50, EscPosBarcodeWidth.THIN, EscPosBarcodeType.EAN13, EscPosAlignment.CENTER,
                                                              EscPosBarcodeFont.FONT_A, EscPosBarcodeLabelPosition.BELOW)
                    printer.addLine(barcodeLine)

                    printer.print(true)
                } catch (ex: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Print Failed due to Error:\n${ex.message ?: ex.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            printThread.start()
        }
    }
}