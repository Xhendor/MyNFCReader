package com.edu.uabc.appm.mynfcreader

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.ACTION_TECH_DISCOVERED
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }

    override fun onResume() {
        super.onResume()

        NfcAdapter.getDefaultAdapter(this).let {
                nfcAdapter ->
            val soyUnIntent = Intent(this, this.javaClass)
            soyUnIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0, soyUnIntent, PendingIntent.FLAG_CANCEL_CURRENT
            )

            val nfcIntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
            val filters = arrayOf(nfcIntentFilter)

            val techLists = arrayOf(
                arrayOf(Ndef::class.java.name),
                arrayOf(NdefFormatable::class.java.name)
            )

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techLists)

        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if(NfcAdapter.EXTRA_NDEF_MESSAGES==intent.action) {
            val tag = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

            Log.e("MyNFC","Detecto un Mensaje")
        }else
        if(NfcAdapter.ACTION_NDEF_DISCOVERED==intent.action){
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            Log.e("MyNFC","Detecto un NDEF")

        } else
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            Log.e("Raw messages", tag.size.toString())
            if (tag != null) {
                val messages = arrayOfNulls<NdefMessage?>(tag.size)
                for (i in tag.indices) {
                    messages[i] = tag[i] as NdefMessage;
                }
                // Process the messages array.
                processNdefMessages(messages)
            }

            var intent:Intent=Intent(this,MapsActivity::class.java)
            startActivity(intent)


        }
    }

    private fun processNdefMessages(ndefMessages: Array<NdefMessage?>) {
        // Go through all NDEF messages found on the NFC tag
        for (curMsg in ndefMessages) {
            if (curMsg != null) {
                // Print generic information about the NDEF message
                Log.e("Message", curMsg.toString())
                // The NDEF message usually contains 1+ records - print the number of recoreds
                Log.e("Records", curMsg.records.size.toString())

                // Loop through all the records contained in the message
                for (curRecord in curMsg.records) {
                    if (curRecord.toString() != null) {
                        // URI NDEF Tag
                        Log.e("- URI", curRecord.toUri().toString())
                    } else {
                        // Other NDEF Tags - simply print the payload
                        Log.e("- Contents", curRecord.payload.contentToString())
                    }
                }
            }
        }
    }


}
