package com.example.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.data.remote.RetrofitInstance
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG="MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDelhiWeather()

    }
    private fun getDelhiWeather(){
        lifecycleScope.launch(Dispatchers.IO){
            val response=try {
                RetrofitInstance.api.getCurrentWeather(
                    "New Delhi",
                    "metric",
                    BuildConfig.API_KEY

                )

            }catch (e:IOException){
                Log.e(TAG,"please check the internet connection")
                return@launch
            }
            catch (e:HttpException){
                Log.e(TAG,"http exception ${e.message()}")
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                withContext(Dispatchers.Main){
                    delay(5000L)
                    Snackbar.make(binding.root,"${response.body()!!.main.temp}",Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}