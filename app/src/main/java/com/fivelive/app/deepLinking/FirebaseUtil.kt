package com.fivelive.app.deepLinking

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.core.app.ShareCompat
import com.fivelive.app.util.AppUtil.dismissProgressDialog
import com.fivelive.app.util.AppUtil.showProgressDialog
import com.google.firebase.dynamiclinks.DynamicLink.*
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

object FirebaseUtil {
    //https://www.sample.com/profile?id=1&name=pavan
    fun createFirebaseDynamicLink(
        activity: Activity?,
        businessId: String?,
        title: String?,
        description: String?,
        imageUrl: String?,
        randomRecipe: Boolean
    ) {
        showProgressDialog(activity)
        //String url = "https://5live.com/?id="+recipeId+"&randomRecipe="+randomRecipe;
        val url = "https://5live.com/?businessId=$businessId"
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(url))
            .setDomainUriPrefix("https://fivelive.page.link") // Open links with this app on Android
            .setAndroidParameters(AndroidParameters.Builder().build())
            .setSocialMetaTagParameters(
                SocialMetaTagParameters.Builder()
                    .setTitle(title!!)
                    .setDescription(description!!) // .setImageUrl(Uri.parse("http://mobuloustech.com/pantry/public/recipe/1512158917.png"))
                    .setImageUrl(Uri.parse(imageUrl))
                    .build()
            ) // Open links with com.example.ios on iOS
            .setIosParameters(IosParameters.Builder("com.5Live.app").build())
            .buildDynamicLink()
        val dynamicLinkUri = dynamicLink.uri
        Log.d("MyLink Long", "getFirebaseDynamicLink: $dynamicLinkUri")
        val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(dynamicLinkUri)
            .buildShortDynamicLink()
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    dismissProgressDialog()
                    val shortLink = task.result?.shortLink
                    val flowchartLink = task.result?.previewLink
                    Log.d("MyLink", "onComplete: " + shortLink.toString())
                    shareIntent(activity, shortLink.toString())
                } else {
                    Log.d("MyLink Error", "onComplete: " + task.exception)
                }
            }
    }

    fun shareIntent(activity: Activity?, shearableLink: String?) {
        dismissProgressDialog()
        ShareCompat.IntentBuilder
            .from(activity!!)
            .setText(shearableLink)
            .setType("text/plain")
            .setChooserTitle("Share with the users")
            .startChooser()
    }

    fun createFirebaseDynamicLinkManually(
        activity: Activity?,
        recipeId: String,
        title: String,
        description: String?,
        imageUrl: String,
        randomRecipe: Boolean
    ) {
        showProgressDialog(activity)
        // IOS LONG LINK    https://pantrymeals.page.link/?link=https://pantrymeals.com/?id=" + recipeId + "&randomRecipe=" + randomRecipe&apn=com.pantrymeals&isi=1539109225&ibi=com.pantrymeals.app
        //  https://pantrymeals.com/recipe-details/640844&true
        val webSiteUrl = "https://pantrymeals.com/?id=$recipeId&randomRecipe=$randomRecipe"
        val url = "https://pantrymeals.page.link/?link=" + webSiteUrl +  //"&sd=" + description +
                "&si=" + imageUrl +
                "&st=" + title +
                "&apn=com.pantrymeals&isi=1539109225&ibi=com.pantrymeals.app"
        shareIntent(activity, url)

        /*Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(url))
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            AppUtil.dismissProgressDialog();
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            shareIntent(activity, shortLink.toString());
                        } else {
                            AppUtil.dismissProgressDialog();
                            Toast.makeText(activity, "This Recipe Not Shareable.", Toast.LENGTH_SHORT).show();
                            //  Log.d("MyLink Error", "onComplete: "+task.getException());
                        }
                    }
                });*/
    }
}