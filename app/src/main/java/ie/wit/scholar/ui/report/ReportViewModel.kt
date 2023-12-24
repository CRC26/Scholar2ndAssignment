package ie.wit.scholar.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.scholar.models.ScholarModel
import ie.wit.scholar.firebase.FirebaseDBManager
import timber.log.Timber
import java.lang.Exception

class ReportViewModel : ViewModel() {

    private val scholarsList =
        MutableLiveData<List<ScholarModel>>()
    var readOnly = MutableLiveData(false)

    val observableScholarsList: LiveData<List<ScholarModel>>
        get() = scholarsList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init { load() }

    fun load() {
        try {
            //ScholarManager.findAll(liveFirebaseUser.value?.email!!, scholarsList)
            FirebaseDBManager.findAll(liveFirebaseUser.value?.uid!!, scholarsList)
            Timber.i("Report Load Success : ${scholarsList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Report Load Error : ${e.message}")
        }
    }

    fun loadAll() {
        try {
            readOnly.value = true
            FirebaseDBManager.findAll(scholarsList)
            Timber.i("Report LoadAll Success : ${scholarsList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Report LoadAll Error : $e.message")
        }
    }

    fun delete(userid: String, id: String) {
        try {
            //ScholarManager.delete(email,id)
            FirebaseDBManager.delete(userid,id)

            Timber.i("Report Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Report Delete Error : $e.message")
        }
    }
}

