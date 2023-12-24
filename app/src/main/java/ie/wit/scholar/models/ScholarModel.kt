package ie.wit.scholar.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class ScholarModel(
      var uid: String? = "",
      var timeline: String = "N/A",
      var pointsEarned: Int = 0,
      var message: String = "Mobile App Assignment!",
      var assignment_name: String = "",
      var pointsPossible: Int = 0,
      var profilepic: String = "",
      var email: String? = "joe@bloggs.com")
      : Parcelable
{
      @Exclude
      fun toMap(): Map<String, Any?> {
            return mapOf(
                  "uid" to uid,
                  "timeline" to timeline,
                  "pointsEarned" to pointsEarned,
                  "message" to message,
                  "assignment_name" to assignment_name,
                  "pointsPossible" to pointsPossible,
                  "profilepic" to profilepic,
                  "email" to email
            )
      }
}