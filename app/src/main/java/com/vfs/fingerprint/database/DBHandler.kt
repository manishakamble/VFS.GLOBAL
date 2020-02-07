package com.vfs.fingerprint.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.vfs.fingerprint.AppClass
import com.vfs.fingerprint.base.MrzDisplayModel


open class DBHandler : SQLiteOpenHelper {

    private val DATABASE_VERSION = 1

    private val TABLE_BIOMETRICS = "Biometric_TXN"
    private val KEY_TOKEN = "id"
    private val KEY_APPID = "id"

    //For Fingers and Thumb
    private var FINGER_CODE_7 = "FC7"
    private var FINGER_CODE_8 = "FC8"
    private var FINGER_CODE_9 = "FC9"
    private var FINGER_CODE_10 = "FC10"
    private var FINGER_CODE_2 = "FC2"
    private var FINGER_CODE_3 = "FC3"
    private var FINGER_CODE_4 = "FC4"
    private var FINGER_CODE_5 = "FC5"
    private var FINGER_CODE_6 = "FC6"
    private var FINGER_CODE_1 = "FC1"

    private var SIGNATURE = "SIGN"

    private var FACIAL = "FACE"

    private var UNIQUE_ID = "UNIQUE_ID"

    //For Mrz Fields
    private var APPLICATION_ID = "application_id"
    private var TXN_ID = "txn_id"
    private var TXN_START_DATE_TIME = "txn_start_date_time"
    private var TXN_END_DATE_TIME = "txn_end_date_time"

    private var NAME = "name"
    private var SURNAME = "surname"
    private var GENDER = "gender"
    private var DATE_OF_BIRTTH = "date_of_birth"
    private var DOCUMENT_TYPE = "document_type"
    private var DOCUMENT_NUMBER = "document_number"
    private var DATE_OF_EXPIRY = "date_of_expiry"
    private var NATIONALITY = "nationality"
    private var PERSONAL_NUMBER = "personal_number"
    private var SIGNATURE_FILE_PATH = "signature_file_path"
    private var NIST_FILE_PATH = "nist_file_path"
    private var DOCUMENT_FILE_PATH = "document_file_path"
    private var BIOMETRIC_STATUS = "biometric_status"
    private var TRANSFER_STATUS = "transfer_status"
    private var TRANSFER_DATE_TIME = "transfer_date_time"

    //get data from db variables
    var READ_NAME: String? = null
    var READ_SURNAME: String? = null

    private var context: Context? = null
    private var uniqueId: String? = null

    var FC1: String? = null
    var FC2: String? = null
    var FC3: String? = null
    var FC4: String? = null
    var FC5: String? = null
    var FC6: String? = null
    var FC7: String? = null
    var FC8: String? = null
    var FC9: String? = null
    var FC10: String? = null
    var SIGN: String? = null
    var FACE: String? = null

    companion object {
        var mInstance: DBHandler? = null
        val DATABASE_NAME = "BiometricDB"

        private val TAG = DBHandler::class.qualifiedName

        fun getInstance(ctx: Context): DBHandler {
            /**
             * use the application context as suggested by CommonsWare.
             * this will ensure that you dont accidentally leak an Activitys
             * context (see this article for more information:
             * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
             */
            if (mInstance == null) {
                mInstance = DBHandler(ctx.applicationContext, DATABASE_NAME, null, 1)
            }
            return mInstance as DBHandler
        }
    }

    constructor(
            context: Context?,
            name: String?,
            factory: SQLiteDatabase.CursorFactory?,
            version: Int
    ) : super(context, name, factory, version) {
        this.context = context
    }


    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "called chekc new row create:")
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_BIOMETRICS + "("
                + KEY_TOKEN + " INTEGER PRIMARY KEY,"
                + APPLICATION_ID + " TEXT,"
                + TXN_ID + " TEXT,"
                + TXN_START_DATE_TIME + " TEXT,"
                + TXN_END_DATE_TIME + " TEXT,"
                + NAME + " TEXT,"
                + SURNAME + " TEXT,"
                + GENDER + " TEXT,"
                + DATE_OF_BIRTTH + " TEXT,"
                + DOCUMENT_TYPE + " TEXT,"
                + DOCUMENT_NUMBER + " TEXT,"
                + DATE_OF_EXPIRY + " TEXT,"
                + NATIONALITY + " TEXT,"
                + SIGNATURE_FILE_PATH + " TEXT,"
                + NIST_FILE_PATH + " TEXT,"
                + DOCUMENT_FILE_PATH + " TEXT,"
                + BIOMETRIC_STATUS + " TEXT DEFAULT WIP,"
                + TRANSFER_STATUS + " TEXT DEFAULT P,"
                + TRANSFER_DATE_TIME + " TEXT DEFAULT NULL,"
                + PERSONAL_NUMBER + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)

        /*   uniqueId = Settings.Secure.getString(
               AppClass.getInstance().getContentResolver(),
               Settings.Secure.ANDROID_ID
           )*/
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_BIOMETRICS);

        // Create tables again
        onCreate(db);
    }

    fun addTransactionID(txnID : String){
        val db = this.writableDatabase
        var appClass = AppClass.getInstance()
        val values = ContentValues()
        values.put(TXN_ID, txnID)
        var id = db.insert(TABLE_BIOMETRICS, null, values)
        db.close()
    }
    fun addMrzData() {
        val db = this.writableDatabase
        var appClass = AppClass.getInstance()
        val values = ContentValues()
        values.put(
                APPLICATION_ID, appClass.applicationNo
        )

        values.put(
                TXN_ID, appClass.txnId
        )

        values.put(
                TXN_START_DATE_TIME, appClass.passportDate
        )

        values.put(
                TXN_END_DATE_TIME, appClass.endDate
        )

        values.put(
                NAME, appClass.passportName
        )

        values.put(
                SURNAME, appClass.passportSurname
        )

        values.put(
                GENDER, appClass.gender
        )

        values.put(
                DATE_OF_BIRTTH, appClass.dob
        )

        values.put(
                DOCUMENT_TYPE, appClass.docType
        )

        values.put(
                DOCUMENT_NUMBER, appClass.passportNumber
        )

        values.put(
                DATE_OF_EXPIRY, appClass.dateOfExpiry
        )

        values.put(
                NATIONALITY, appClass.nationality
        )

        values.put(
                PERSONAL_NUMBER, appClass.personalNumber
        )

        values.put(
                SIGNATURE_FILE_PATH, appClass.signaturePath
        )

        values.put(
                NIST_FILE_PATH, appClass.nistPath
        )

        values.put(
                DOCUMENT_FILE_PATH, appClass.documentPath
        )

        values.put(
                BIOMETRIC_STATUS, appClass.biometricStatus
        )

        /*values.put(
                TRANSFER_STATUS, appClass.biometricStatus
        )*/

        values.put(
                TRANSFER_DATE_TIME, "null"
        )

        var id = db.insert(TABLE_BIOMETRICS, null, values)
      //  db.update(TABLE_BIOMETRICS, values, "$TXN_ID=" + appClass.txnId, null)
        db.close()
    }

    fun addFingerData(fingerModel: FingerModel, id: Int): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(
                FINGER_CODE_7,
                fingerModel.getSCALE100()?.getFingerprints()?.get(0)?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )
        values.put(
                FINGER_CODE_8,
                fingerModel.getSCALE100()?.getFingerprints()?.get(1)?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )
        values.put(
                FINGER_CODE_9,
                fingerModel.getSCALE100()?.getFingerprints()?.get(2)?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )
        values.put(
                FINGER_CODE_10,
                fingerModel.getSCALE100()?.getFingerprints()?.get(3)?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )

        values.put(
                FINGER_CODE_2,
                fingerModel.getSCALE100()?.getFingerprints()?.get(4)?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )
        values.put(
                FINGER_CODE_3,
                fingerModel.getSCALE100()?.getFingerprints()?.get(5)?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )
        values.put(
                FINGER_CODE_4,
                fingerModel.getSCALE100()?.getFingerprints()?.get(6)?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )
        values.put(
                FINGER_CODE_5,
                fingerModel.getSCALE100()?.getFingerprints()?.get(7)?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )

        values.put(
                UNIQUE_ID,
                uniqueId
        )

//        var id = db.insert(TABLE_BIOMETRICS, null, values)
//        db.close() // Closing database connection
        var id = db.update(TABLE_BIOMETRICS, values, "$KEY_TOKEN=" + id, null)
        db.close()
        return id
    }

    fun addThumbData(fingerModel: FingerModel, id: Long) {
        val db = this.writableDatabase

        val values = ContentValues()

        values.put(
                FINGER_CODE_6,
                fingerModel.getSCALE100()?.getFingerprints()?.get(0)?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )

        values.put(
                FINGER_CODE_1,
                fingerModel.getSCALE100()?.getFingerprints()?.get(1)?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )

        Log.d(
                TAG,
                "called ak check what is happeing:" + KEY_TOKEN + "___" + id + "___" + fingerModel.getSCALE100()?.getFingerprints()?.get(
                        0
                )?.getFingerImpressionImage()?.getBinaryBase64ObjectBMP()
        )

        // Inserting Row
        db.update(TABLE_BIOMETRICS, values, "$KEY_TOKEN=" + id, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
    }

    fun getData():List<MrzDisplayModel> {
        var list= ArrayList<MrzDisplayModel>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_BIOMETRICS"
        var cursor = db.rawQuery(query, null)

        var fingerModel: FingerModel = FingerModel()


        if (cursor.moveToFirst()) {
            do {
                var mrzModel = MrzDisplayModel()
                mrzModel.applicatioN_ID = cursor.getString(cursor.getColumnIndex(APPLICATION_ID))
                mrzModel.txN_ID = cursor.getString(cursor.getColumnIndex(TXN_ID))
                mrzModel.txN_START_DATE_TIME = cursor.getString(cursor.getColumnIndex(TXN_START_DATE_TIME))
                mrzModel.txN_END_DATE_TIME = cursor.getString(cursor.getColumnIndex(TXN_END_DATE_TIME))
                mrzModel.name = cursor.getString(cursor.getColumnIndex(NAME))
                mrzModel.surname = cursor.getString(cursor.getColumnIndex(SURNAME))
                mrzModel.gender = cursor.getString(cursor.getColumnIndex(GENDER))
                mrzModel.datE_OF_BIRTTH = cursor.getString((cursor.getColumnIndex(DATE_OF_BIRTTH)))
                mrzModel.documenT_TYPE = cursor.getString((cursor.getColumnIndex(DOCUMENT_TYPE)))
                mrzModel.documenT_NUMBER = cursor.getString(cursor.getColumnIndex(DOCUMENT_NUMBER))
                mrzModel.datE_OF_EXPIRY = cursor.getString(cursor.getColumnIndex(DATE_OF_EXPIRY))
                mrzModel.nationality = cursor.getString(cursor.getColumnIndex(NATIONALITY))
                mrzModel.signaturE_FILE_PATH = cursor.getString(cursor.getColumnIndex(SIGNATURE_FILE_PATH))
                mrzModel.nisT_FILE_PATH = cursor.getString(cursor.getColumnIndex(NIST_FILE_PATH))
                mrzModel.documenT_FILE_PATH = cursor.getString(cursor.getColumnIndex(DOCUMENT_FILE_PATH))
                mrzModel.biometriC_STATUS = cursor.getString(cursor.getColumnIndex(BIOMETRIC_STATUS))
                mrzModel.transfeR_STATUS = cursor.getString(cursor.getColumnIndex(TRANSFER_STATUS))
                mrzModel.transfeR_DATE_TIME = cursor.getString(cursor.getColumnIndex(TRANSFER_DATE_TIME))
                mrzModel.personaL_NUMBER = cursor.getString(cursor.getColumnIndex(PERSONAL_NUMBER))
                list.add(mrzModel)
                Log.d(TAG,"called ak check :"+mrzModel.name+"__"+list?.size)
            } while (cursor.moveToNext());
        }
        return list!!
    }

    fun getId(): Int {

        val db = this.writableDatabase

        val resultSet = db.rawQuery("SELECT $KEY_TOKEN from $TABLE_BIOMETRICS order by $KEY_TOKEN DESC limit 1", null)
        resultSet.moveToFirst()
        val token = resultSet.getInt(0)
        Log.d(TAG, "called ak token ak:" + token)
        return token;
    }

    fun addSignatureInBase64(base64: String, id: Int) {
        val db = this.writableDatabase

        val values = ContentValues()

        values.put(
                SIGNATURE, base64
        )

        db.update(TABLE_BIOMETRICS, values, "$KEY_TOKEN=" + id, null)
        db.close()
    }

    fun addFacialInBase64(base64: String) {
        val db = this.writableDatabase

        val values = ContentValues()

        values.put(
                FACIAL, base64
        )

//        db.update(TABLE_BIOMETRICS, values, "$KEY_TOKEN=" + id, null)
//        db.close()
        var id = db.insert(TABLE_BIOMETRICS, null, values)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection

    }
}