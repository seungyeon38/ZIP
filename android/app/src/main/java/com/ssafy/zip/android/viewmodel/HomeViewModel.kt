package com.ssafy.zip.android.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.ssafy.zip.android.App
import com.ssafy.zip.android.data.Family
import com.ssafy.zip.android.data.Missions
import com.ssafy.zip.android.data.User
import com.ssafy.zip.android.repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository): ViewModel(){
    private val _familyData = MutableLiveData<Family>()
    val familyData : LiveData<Family> get() = _familyData
    private val _missions = MutableLiveData<Missions>()
    val missions : LiveData<Missions> get() = _missions
    private val _userData = MutableLiveData<User>()
    val userData : LiveData<User> get() = _userData

    init {
//        Log.d("TAG", "HomeViewModel 생성자 호출")
        viewModelScope.launch {
            _familyData.value = repository.getFamily()
            _missions.value = repository.getMission()
            _userData.value = repository.getUserData()
            App.prefs.setString("userId", userData.value?.id.toString())
        }
    }

    class Factory(private val application: Application) :ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(HomeRepository.getInstance(application)!!) as T
        }
    }

}