package kaist.software.mosecctv.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class StorageActivity : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = Firebase.storage

        // Points to the root reference
        var storageRef = storage.reference

        // Points to "images"
        var imagesRef = storageRef.child("images")

        // Points to "images/space.jpg"
        // Note that you can use variables to create child values
        val fileName = "space.jpg"
        var spaceRef = imagesRef.child(fileName)

        // File path is "images/space.jpg"
        val path = spaceRef.path

        // File name is "space.jpg"
        val name = spaceRef.name


    }
}