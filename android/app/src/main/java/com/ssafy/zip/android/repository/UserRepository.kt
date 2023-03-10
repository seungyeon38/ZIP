package com.ssafy.zip.android.repository

import android.app.Application
import com.ssafy.zip.android.ApiService
import com.ssafy.zip.android.App
import com.ssafy.zip.android.data.User
import com.ssafy.zip.android.data.UserFamily
import com.ssafy.zip.android.data.request.RequestFamilyroom
import com.ssafy.zip.android.data.request.RequestLoginData
import com.ssafy.zip.android.data.request.RequestModify
import com.ssafy.zip.android.data.request.RequestSignup

class UserRepository private constructor(application: Application) {

    suspend fun login(
        requestLoginData: RequestLoginData,
    ): Any? {
        val response = ApiService.getApiService.userLogin(requestLoginData)
        var returnData: Any?
        returnData = if (response.code()==200) {
            val headers = response.headers()
            val accesstoken = headers.get("ACCESSTOKEN").toString()
            val refreshtoken = headers.get("REFRESHTOKEN").toString()
            App.prefs.setString("accesstoken", accesstoken)
            App.prefs.setString("refreshtoken", refreshtoken)
            response.body() as User
        } else {
            response.code()
        }
        return returnData
    }
    suspend fun postFcmToken(
        token : String
    ) : String? {
        val response = ApiService.getApiService.putFcmToken(token)
        var returnData : String?
        returnData = response.code().toString()
        return returnData
    }

    suspend fun signUp(
        requestSignup: RequestSignup,
    ): String?{
        val response = ApiService.getApiService.userSignup(requestSignup)
//        println("UserRepository signUp response: " + response)
        var returnData : String?
        returnData = response.code().toString()
        return returnData
    }

    suspend fun emailCheck(
        email : String,
    ) : String?{
        val response = ApiService.getApiService.emailCheck(email)
        var returnData : String?
        returnData = response.code().toString()
        return returnData
    }

    suspend fun enterRoom(
        code: Int,
    )
    : Any?{
        val response = ApiService.getApiService.enterRoom(code)
        var returnData : Any?
        returnData = if(response.isSuccessful) {
            response.body() as UserFamily
        } else {
            response.code()
        }
        return returnData
    }

    suspend fun createRoom(
        requestFamilyroom: RequestFamilyroom
    ) : Any? {
        val response = ApiService.getApiService.createFamily(requestFamilyroom)
        var returnData : Any?
        returnData = if(response.isSuccessful) {
            response.body() as UserFamily
        } else{
            response.code()
        }
        return returnData
    }

    suspend fun logout()
    : String?{
        val response = ApiService.getApiService.logoutUser()
        App.prefs.setString("accesstoken", "")
        App.prefs.setString("refreshtoken", "")
        return response.code().toString()
    }

    suspend fun modifyUser(
       requestModify: RequestModify
    ) : Any?{
        val response = ApiService.getApiService.modifyUser(requestModify)
//        println("modifyUser response: " + response)

        var returnData : Any?
        returnData = if(response.isSuccessful) {
            response.body() as User
        } else {
            null
        }
        return returnData
    }
    suspend fun readAllNotification() : String? {
        val response = ApiService.getApiService.readNotificationAll()
        return response.code().toString()
    }

    companion object {
        private var instance: UserRepository? = null

        fun getInstance(application: Application): UserRepository? {
            if (instance == null) instance = UserRepository(application)
            return instance
        }
    }

}