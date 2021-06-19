package ru.netology.nmedia.viewmodel

import android.app.Application
import android.content.Context
import android.text.Editable
import androidx.lifecycle.*

import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: PostRepository,
    private val workManager: WorkManager,
    auth: AppAuth,
) : ViewModel() {
    val data: LiveData<FeedModel> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.authorId == myId) },
                        posts.isEmpty()
                    )
                }
        }.asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    fun onLogin(login: Editable, pass: Editable) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.updateUser(login, pass)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
}



//class LoginViewModel (application: Application) : AndroidViewModel(application){
//    private val repository: PostRepository =
//        PostRepositoryImpl(AppDb.getInstance(context = application).postDao(), AppDb.getInstance(context = application).postWorkDao())
//
//
//
//
//     val _dataState = MutableLiveData<FeedModelState>()
//    val dataState: LiveData<FeedModelState>
//        get() = _dataState
//
//    fun onLogin(login: Editable, pass: Editable) = viewModelScope.launch {
//        try {
//            _dataState.value = FeedModelState(loading = true)
//            repository.updateUser(login, pass)
//            _dataState.value = FeedModelState()
//        } catch (e: Exception) {
//            _dataState.value = FeedModelState(error = true)
//        }
//    }
//}