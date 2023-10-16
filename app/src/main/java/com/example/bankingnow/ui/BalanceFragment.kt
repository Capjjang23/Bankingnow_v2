package com.example.bankingnow.ui

import android.util.Log
import android.view.MotionEvent
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.R
import com.example.bankingnow.apiManager.RecordApiManager
import com.example.bankingnow.base.BaseFragment
import com.example.bankingnow.databinding.FragmentBalanceBinding
import com.example.bankingnow.model.BalanceResponseModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BalanceFragment : BaseFragment<FragmentBalanceBinding>(R.layout.fragment_balance),
    RecordApiManager.postMyBalance {
    private val apiManager = RecordApiManager()
    private val finAcno = prefs.getString("FinAcno", "")

    override fun initStartView() {
        super.initStartView()

        apiManager.listener = this

        apiManager.postBalance(finAcno)
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

        setTouchScreen()
    }

    private fun setTouchScreen() {
        var startX = 0f
        var startY = 0f

        binding.fragmentBalance.setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                }

                MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    val distanceX = endX - startX

                    // 스와이프를 감지하기 위한 조건 설정
                    if (distanceX > 100) {
                        // 오른쪽으로 스와이프
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }
                        requireActivity().onBackPressed()
                    } else if (distanceX>-10 && distanceX<10){
                        // 클릭으로 처리
                        if (customTTS.tts.isSpeaking) {
                            tts.stop()
                        }
                        apiManager.postBalance(finAcno)
                    }
                }
            }
            true // 이벤트 소비
        }
    }

    override fun postBalance(balanceModel: BalanceResponseModel) {
        Log.d("잔액확인", balanceModel.toString())
        val depositor = prefs.getString("Dpnm", "")

        binding.tvUserInfo.text = "${depositor} 님\n농협은행 통장잔액"
        binding.tvBalance.text = balanceModel.Ldbl + " 원"

        val formattedString = getString(R.string.account_balance, depositor, "농협은행", balanceModel.Ldbl)
        customTTS.speak(formattedString)

    }

    fun getDate(): String {
        val sdf = SimpleDateFormat("yyyyMMdd")
        val currentDate = Date()
        return sdf.format(currentDate)
    }

    fun getTime(): String {
        val sdf = SimpleDateFormat("HHmmss")
        val currentTime = Date()
        return sdf.format(currentTime)
    }

}
