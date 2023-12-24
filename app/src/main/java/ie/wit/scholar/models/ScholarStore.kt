package ie.wit.scholar.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser


interface ScholarStore {
    fun findAll(scholarsList:
                MutableLiveData<List<ScholarModel>>)
    fun findAll(userid:String,
                scholarsList:
                MutableLiveData<List<ScholarModel>>)
    fun findById(userid:String, scholarid: String,
                 scholar: MutableLiveData<ScholarModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, scholar: ScholarModel)
    fun delete(userid:String, scholarid: String)
    fun update(userid:String, scholarid: String, scholar: ScholarModel)
}

