package ie.wit.scholar.ui.scholarly

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.scholar.firebase.FirebaseDBManager
import ie.wit.scholar.models.ScholarModel
import ie.wit.scholar.firebase.FirebaseImageManager

class ScholarlyViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addScholar(firebaseUser: MutableLiveData<FirebaseUser>,
                    scholar: ScholarModel) {
        status.value = try {
            //ScholarManager.create(scholar)
            scholar.profilepic = FirebaseImageManager.imageUri.value.toString()
            FirebaseDBManager.create(firebaseUser,scholar)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}