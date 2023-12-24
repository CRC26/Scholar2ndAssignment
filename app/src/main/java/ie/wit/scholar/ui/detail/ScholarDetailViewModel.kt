package ie.wit.scholar.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.scholar.models.ScholarModel
import ie.wit.scholar.firebase.FirebaseDBManager
import timber.log.Timber


class ScholarDetailViewModel : ViewModel() {
    private val scholar = MutableLiveData<ScholarModel>()

    var observableScholar: LiveData<ScholarModel>
        get() = scholar
        set(value) {scholar.value = value.value}

    fun getScholar(userid:String, id: String) {
        try {
            //ScholarManager.findById(email, id, scholar)
            FirebaseDBManager.findById(userid, id, scholar)
            Timber.i("Detail getScholar() Success : ${scholar.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getScholar() Error : $e.message")
        }
    }
    fun updateScholar(userid:String, id: String,scholar: ScholarModel) {
        try {
            //ScholarManager.update(email, id, donation)
            FirebaseDBManager.update(userid, id, scholar)
            Timber.i("Detail update() Success : $scholar")
        }
        catch (e: Exception) {
            Timber.i("Detail update() Error : $e.message")
        }
    }
}