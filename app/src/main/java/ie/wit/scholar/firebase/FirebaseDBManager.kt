package ie.wit.scholar.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ie.wit.scholar.models.ScholarModel
import ie.wit.scholar.models.ScholarStore
import timber.log.Timber

object FirebaseDBManager : ScholarStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(scholarsList: MutableLiveData<List<ScholarModel>>) {
        database.child("scholars")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Scholar error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ScholarModel>()
                    val children = snapshot.children
                    children.forEach {
                        val scholar = it.getValue(ScholarModel::class.java)
                        localList.add(scholar!!)
                    }
                    database.child("scholars")
                        .removeEventListener(this)

                    scholarsList.value = localList
                }
            })
    }

    override fun findAll(userid: String, scholarsList: MutableLiveData<List<ScholarModel>>) {

        database.child("user-scholars").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Scholar error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ScholarModel>()
                    val children = snapshot.children
                    children.forEach {
                        val scholar = it.getValue(ScholarModel::class.java)
                        localList.add(scholar!!)
                    }
                    database.child("user-scholars").child(userid)
                        .removeEventListener(this)

                    scholarsList.value = localList
                }
            })
    }

    override fun findById(userid: String, scholarid: String, scholar: MutableLiveData<ScholarModel>) {

        database.child("user-scholars").child(userid)
            .child(scholarid).get().addOnSuccessListener {
                scholar.value = it.getValue(ScholarModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, scholar: ScholarModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("scholars").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        scholar.uid = key
        val scholarValues = scholar.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/scholars/$key"] = scholarValues
        childAdd["/user-scholars/$uid/$key"] = scholarValues

        database.updateChildren(childAdd)
    }

    override fun delete(userid: String, scholarid: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/scholars/$scholarid"] = null
        childDelete["/user-scholars/$userid/$scholarid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, scholarid: String, scholar: ScholarModel) {

        val scholarValues = scholar.toMap()

        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["scholars/$scholarid"] = scholarValues
        childUpdate["user-scholars/$userid/$scholarid"] = scholarValues

        database.updateChildren(childUpdate)
    }

    fun updateImageRef(userid: String,imageUri: String) {

        val userScholars = database.child("user-scholars").child(userid)
        val allScholars = database.child("scholars")

        userScholars.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        //Update Users imageUri
                        it.ref.child("profilepic").setValue(imageUri)
                        //Update all scholars that match 'it'
                        val scholar = it.getValue(ScholarModel::class.java)
                        allScholars.child(scholar!!.uid!!)
                            .child("profilepic").setValue(imageUri)
                    }
                }
            })
    }
}