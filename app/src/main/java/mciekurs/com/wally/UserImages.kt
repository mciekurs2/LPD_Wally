package mciekurs.com.wally

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class UserImages(val id: String = "", val image_url: String = "", val title: String = "", val bio: String = "")

