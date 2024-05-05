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
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
//                    delay(5000L)
//                    Snackbar.make(binding.root,"${response.body()!!.main.temp}",Snackbar.LENGTH_LONG).show()
                    val data = response.body()!!

                    val iconId = data.weather[0].icon
                    val imgUrl = "https://openweathermap.org/img/wn/$iconId@4x.png"

                    Picasso.get().load(imgUrl).into(binding.imgWeather)

                    binding.tvSunset.text =
                        dateFormatConverter(
                            data.sys.sunset.toLong()
                        )

                    binding.tvSunrise.text =
                        dateFormatConverter(
                            data.sys.sunrise.toLong()
                        )

                    binding.apply {
                        tvStatus.text = data.weather[0].description
                        tvWind.text = "${data.wind.speed} KM/H"
                        tvLocation.text = "${data.name}\n${data.sys.country}"
                        tvTemp.text = "${data.main.temp.toInt()}°C"
                        tvFeelsLike.text = "Feels like: ${data.main.feels_like.toInt()}°C"
                        tvMinTemp.text = "Min temp: ${data.main.temp_min.toInt()}°C"
                        tvMaxTemp.text = "Max temp: ${data.main.temp_max.toInt()}°C"
                        tvHumidity.text = "${data.main.humidity} %"
                        tvPressure.text = "${data.main.pressure} hPa"
                        tvUpdateTime.text = "Last Update: ${
                            dateFormatConverter(
                                data.dt.toLong()
                            )
                        }"


                    }

                }
            }



        }
    }
    private fun dateFormatConverter(date: Long): String {

        return SimpleDateFormat(
            "hh:mm a",
            Locale.ENGLISH
        ).format(Date(date * 1000))
    }
}