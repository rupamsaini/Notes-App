package com.rupam.notes.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rupam.notes.R
import java.util.*

class NoteDetailsActivity : AppCompatActivity() {
    private lateinit var title: TextView
    private lateinit var body: TextView
    private lateinit var editBtn: Button
    private lateinit var dltBtn: Button
    private lateinit var extras: Bundle
    private lateinit var alertDialog: AlertDialog
    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var edit_title: EditText
    private lateinit var edit_body: EditText
    private lateinit var save_note: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mRef: DatabaseReference
    private lateinit var getTitle: String
    private lateinit var getBody: String
    private lateinit var key: String
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        supportActionBar!!.hide()
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        mDatabase = FirebaseDatabase.getInstance()
        mRef = mDatabase.reference.child("Users").child(currentUser.uid)
        extras = intent.extras!!
        title = findViewById(R.id.titleDET)
        body = findViewById(R.id.bodyDET)
        editBtn = findViewById(R.id.editBtn)
        dltBtn = findViewById(R.id.dltBtn)
        progressDialog = ProgressDialog(this)
        getTitle = extras.getString("title")!!
        getBody = extras.getString("body")!!
        title.text = getTitle
        body.text = getBody
        body.movementMethod = ScrollingMovementMethod()

//        Getting key to determine which Unique key to be delete from FB
        key = extras.getString("userKey").toString()

//       Implement modification button
        editBtn.setOnClickListener(View.OnClickListener { //                Toast.makeText(NoteDetailsActivity.this, key, Toast.LENGTH_SHORT).show();
            createPopup()
        })
        dltBtn.setOnClickListener(View.OnClickListener {
            mRef.child(key).removeValue()
            val goBack = Intent(this@NoteDetailsActivity, NotesListActivity::class.java)
            goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(goBack)
            Toast.makeText(this@NoteDetailsActivity, "Note Deleted", Toast.LENGTH_SHORT).show()
        })
    }

    private fun createPopup() {
        dialogBuilder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.modify_note, null)
        edit_title = view.findViewById(R.id.title_mod)
        edit_body = view.findViewById(R.id.body_mod)
        save_note = view.findViewById(R.id.save_mod)
        dialogBuilder.setView(view)
        alertDialog = dialogBuilder.create()
        edit_title.setText(getTitle)
        edit_body.setText(getBody)
        alertDialog.show()
        save_note.setOnClickListener(View.OnClickListener { addNote() })
    }

    private fun addNote() {
        progressDialog.setMessage("Adding Note...")
        progressDialog.show()
        val mTitleVal = edit_title.text.toString().trim { it <= ' ' }
        val mBodyVal = edit_body.text.toString().trim { it <= ' ' }
        if (!TextUtils.isEmpty(edit_title.text) && !TextUtils.isEmpty(edit_body.text)) {

//            DatabaseReference addNote = mRef.child(key).push();
            val newNote: MutableMap<String, Any> = HashMap()
            newNote["title"] = mTitleVal
            newNote["noteBody"] = mBodyVal
            //            newNote.put("dateAdded", String.valueOf(java.lang.System.currentTimeMillis()));
            newNote["key"] = currentUser.uid
            mRef.child(key).updateChildren(newNote)
            progressDialog.dismiss()
            startActivity(Intent(this@NoteDetailsActivity, NotesListActivity::class.java))
            finish()
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
        }
    }
}