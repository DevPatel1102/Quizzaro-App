package com.example.quizzaro

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.quizzaro.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuizActivity : AppCompatActivity() {

    lateinit var quizBinding : ActivityQuizBinding

    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference : DatabaseReference = database.reference.child("Question")

//    var question = ""
//    var answerA = ""
//    var answerB = ""
//    var answerC = ""
//    var answerD = ""
//    var correctAnswer = ""
//    var questionCount = 0

    var questionText = ""
    var optionA = ""
    var optionB = ""
    var optionC = ""
    var optionD = ""
    var questionNumber = 0
    var collectingDataNumber = 0
    var correctAnswerRetrofit =""
    var questionSize = 0

    var userAnswer = ""
    var userCorrect = 0
    var userWrong = 0

    lateinit var timer: CountDownTimer
    val totalTime = 30000L
    var timerContinue = false
    var leftTime = totalTime

    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val scoreRef = database.reference

    var remainingTime = 0

    val baseUrl = "https://opentdb.com/"

    var questionsArray: ArrayList<String> = ArrayList()
    var correctAnswerArray: ArrayList<String> = ArrayList()
    var incorrectAnswerArray: ArrayList<String> = ArrayList()
    var totalAnswers : ArrayList<String> = ArrayList()

    val quizQuestionsList = mutableListOf<QuizQuestion>()

    var art = false
    var science = false
    var history = false
    var food = false

    // Define retrofitBuilder globally
    val retrofitBuilder: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    // Define retrofitData globally
    lateinit var retrofitData: Call<QuizResponse>

    override fun onCreate(savedInstanceState: Bundle?) {

        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root

        super.onCreate(savedInstanceState)
        setContentView(view)

        val intent = intent
        art = intent.getBooleanExtra("Art",false)
        science = intent.getBooleanExtra("Science",false)
        history = intent.getBooleanExtra("History",false)
        food = intent.getBooleanExtra("Food",false)

        quizData()

        quizBinding.nextButton.setOnClickListener {

            if (userAnswer.isNotEmpty()) {
                userAnswer=""
                resetTimer()
                collectingData()

            } else {
                if(remainingTime==30 || remainingTime==0){
                    userWrong++
                    quizBinding.textViewWrongAns.text = userWrong.toString()
                    userAnswer=""
                    resetTimer()
                    collectingData()
                }else {
                    Toast.makeText(
                        applicationContext,
                        "Please select an answer before moving to the next question",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
        quizBinding.finishButton.setOnClickListener {

            sendScore()

        }
        quizBinding.textViewA.setOnClickListener {

            pauseTimer()
            userAnswer = quizBinding.textViewA.text.toString()
            if(correctAnswerRetrofit == userAnswer){

                quizBinding.textViewA.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrectAns.text = userCorrect.toString()

            }else{

                quizBinding.textViewA.backgroundTintList = ColorStateList.valueOf(Color.RED)
                userWrong++
                quizBinding.textViewWrongAns.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()

        }
        quizBinding.textViewB.setOnClickListener {

            pauseTimer()
            userAnswer = quizBinding.textViewB.text.toString()
            if(correctAnswerRetrofit == userAnswer){

                quizBinding.textViewB.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrectAns.text = userCorrect.toString()

            }else{

                quizBinding.textViewB.backgroundTintList = ColorStateList.valueOf(Color.RED)
                userWrong++
                quizBinding.textViewWrongAns.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()

        }
        quizBinding.textViewC.setOnClickListener {

            pauseTimer()
            userAnswer = quizBinding.textViewC.text.toString()
            if(correctAnswerRetrofit == userAnswer){

                quizBinding.textViewC.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrectAns.text = userCorrect.toString()

            }else{

                quizBinding.textViewC.backgroundTintList = ColorStateList.valueOf(Color.RED)
                userWrong++
                quizBinding.textViewWrongAns.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()

        }
        quizBinding.textViewD.setOnClickListener {

            pauseTimer()
            userAnswer = quizBinding.textViewD.text.toString()
            if(correctAnswerRetrofit == userAnswer){
                quizBinding.textViewD.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrectAns.text = userCorrect.toString()

            }else{
                quizBinding.textViewD.backgroundTintList = ColorStateList.valueOf(Color.RED)
                userWrong++
                quizBinding.textViewWrongAns.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()

        }
    }


    // Firebase
//    private fun gameLogic(){
//
//        restoreOptions()
//
//        reference.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                questionCount = snapshot.childrenCount.toInt()
//
//                if(questionNumber < questions.size){
//
//                    question = snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
//                    answerA = snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
//                    answerB = snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
//                    answerC = snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
//                    answerD = snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
//                    correctAnswer = snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()
//
//                    quizBinding.textViewQuestion.text = question
//                    quizBinding.textViewA.text = answerA
//                    quizBinding.textViewB.text = answerB
//                    quizBinding.textViewC.text = answerC
//                    quizBinding.textViewD.text = answerD
//
//                    quizBinding.progressBarQuestionPage.visibility = View.INVISIBLE
//                    quizBinding.linearLayoutInfo.visibility=View.VISIBLE
//                    quizBinding.linearLayoutQuestion.visibility=View.VISIBLE
//                    quizBinding.linearLayoutButton.visibility=View.VISIBLE
//
//                    startTimer()
//
//                }else{
//
//                    val dialogMessage = AlertDialog.Builder(this@QuizActivity)
//                    dialogMessage.setTitle("Quizzaro")
//                    dialogMessage.setMessage("Congratulations!!\n You have answered all the questions. Do you want to see your result?")
//                    dialogMessage.setCancelable(false)
//                    dialogMessage.setPositiveButton("See Result"){dialogWindow,position ->
//
//                        sendScore()
//                    }
//                    dialogMessage.setNegativeButton("Play Again"){dialogWindow,position ->
//
//                        val intent = Intent(this@QuizActivity,MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//
//                    dialogMessage.create().show()
//                }
//
//                questionNumber++
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//                Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
//
//            }
//        })
//    }

    // For Retrofit = correctAnswerRetrofit   and   For Firebase = correctAnswer
    fun findAnswer(){
        when(correctAnswerRetrofit){
            quizBinding.textViewA.text -> quizBinding.textViewA.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            quizBinding.textViewB.text -> quizBinding.textViewB.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            quizBinding.textViewC.text -> quizBinding.textViewC.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            quizBinding.textViewD.text -> quizBinding.textViewD.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
        }
    }

    fun disableClickableOptions(){

        quizBinding.textViewA.isClickable = false
        quizBinding.textViewB.isClickable = false
        quizBinding.textViewC.isClickable = false
        quizBinding.textViewD.isClickable = false
    }

    fun restoreOptions(){

        quizBinding.textViewA.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        quizBinding.textViewB.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        quizBinding.textViewC.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        quizBinding.textViewD.backgroundTintList = ColorStateList.valueOf(Color.WHITE)

        quizBinding.textViewA.isClickable = true
        quizBinding.textViewB.isClickable = true
        quizBinding.textViewC.isClickable = true
        quizBinding.textViewD.isClickable = true
    }

    fun startTimer(){

        timer = object : CountDownTimer(leftTime,1000){
            override fun onTick(millisUntilFinished: Long) {

                leftTime = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {

                disableClickableOptions()
                resetTimer()
                updateCountDownText()
                quizBinding.textViewQuestion.text = "Sorry, Time is Up! Continue with next Question"
                timerContinue = false

            }
        }.start()

        timerContinue = true
    }
    fun updateCountDownText(){

        remainingTime = (leftTime/1000).toInt()
        quizBinding.textViewTime.text = remainingTime.toString()

    }

    fun pauseTimer(){

        timer.cancel()
        timerContinue = false

    }

    fun resetTimer(){

        pauseTimer()
        leftTime = totalTime
        updateCountDownText()
    }

    fun sendScore(){

        user?.let {
            val userUID = it.uid
            scoreRef.child("Scores").child(userUID).child("correct").setValue(userCorrect)
            scoreRef.child("Scores").child(userUID).child("wrong").setValue(userWrong).addOnSuccessListener {

                Toast.makeText(applicationContext,"Scores updated successfully",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@QuizActivity,ResultActivity::class.java)
                startActivity(intent)
                finish()

            }
        }

    }



    // Retrofit Library
    private fun quizData() {

        // Determine the endpoint based on the condition
        val endpoint = when {
            art -> retrofitBuilder.create(ApiInterfaceArt::class.java).getDataArt()
            science -> retrofitBuilder.create(ApiInterfaceScience::class.java).getDataScience()
            history -> retrofitBuilder.create(ApiInterfaceHistory::class.java).getDataHistory()
            else -> retrofitBuilder.create(ApiInterfaceSports::class.java).getDataSports()
        }

        // Assign retrofitData globally
        retrofitData = endpoint

        retrofitData.enqueue(object : Callback<QuizResponse> {
            override fun onResponse(call: Call<QuizResponse>, response: Response<QuizResponse>) {
                // Handle the response
                val responseBody = response.body()

                responseBody?.let { quizResponse ->
                    val results = quizResponse.results

                    if (results.size >= 10) {
                        for (i in 0 until 10) {
                            val myData = results[i]
                            questionsArray.add(myData.question)
                            correctAnswerArray.add(myData.correct_answer)
                            incorrectAnswerArray.add(myData.incorrect_answers.toString())
                        }
                        collectingData()
                    } else {
                        Log.e("QuizActivity", "Not enough questions in the API response.")
                    }
                }
            }

            override fun onFailure(call: Call<QuizResponse>, t: Throwable) {
                Log.e("Retrofit", "Error fetching quiz data", t)
                // Handle the failure appropriately
            }
        })
    }

    private fun collectingData(){
        startTimer()
        if(collectingDataNumber < 10) {
            totalAnswers.add(questionsArray[collectingDataNumber])
            totalAnswers.add(correctAnswerArray[collectingDataNumber])
            totalAnswers.add(incorrectAnswerArray[collectingDataNumber])

            val question = totalAnswers[collectingDataNumber * 3]
            val correctAnswer = totalAnswers[collectingDataNumber * 3 + 1]
            val incorrectAnswers = totalAnswers[collectingDataNumber * 3 + 2].replace("[", "").replace("]", "").split(", ")

            val allAnswers = mutableListOf<String>()
            allAnswers.add(correctAnswer)
            allAnswers.addAll(incorrectAnswers)

            allAnswers.shuffle()

            val quizQuestion = QuizQuestion(question, correctAnswer, incorrectAnswers, allAnswers)
            quizQuestionsList.add(quizQuestion)

            gameLogic2()
            collectingDataNumber++
        }
        else{
            gameLogic2()
        }
    }

    private fun gameLogic2(){

        restoreOptions()
        if(collectingDataNumber < 10) {
                questionText = quizQuestionsList[questionNumber].question
                correctAnswerRetrofit = quizQuestionsList[questionNumber].correctAnswer
                val allAnswers = quizQuestionsList[questionNumber].allAnswers

                 optionA = allAnswers[0]
                 optionB = allAnswers[1]
                 optionC = allAnswers[2]
                 optionD = allAnswers[3]

                quizBinding.textViewQuestion.text = questionText
                quizBinding.textViewA.text = optionA
                quizBinding.textViewB.text = optionB
                quizBinding.textViewC.text = optionC
                quizBinding.textViewD.text = optionD

                quizBinding.progressBarQuestionPage.visibility = View.INVISIBLE
                quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                quizBinding.linearLayoutAnswer.visibility = View.VISIBLE
                quizBinding.linearLayoutQuestionBox.visibility = View.VISIBLE


        }else{
            val dialogMessage = AlertDialog.Builder(this@QuizActivity)
            dialogMessage.setTitle("Quizzaro")
            dialogMessage.setMessage("Congratulations!!\nYou have answered all the questions. Do you want to see your result?")
            dialogMessage.setCancelable(false)
            dialogMessage.setPositiveButton("See Result"){dialogWindow,position ->

                sendScore()
            }
            dialogMessage.setNegativeButton("Play Again"){dialogWindow,position ->

                user?.let {
                    val userUID = it.uid
                    scoreRef.child("Scores").child(userUID).child("correct").setValue(userCorrect)
                    scoreRef.child("Scores").child(userUID).child("wrong").setValue(userWrong).addOnSuccessListener {

                        Toast.makeText(applicationContext,"Scores updated successfully",Toast.LENGTH_SHORT).show()

                    }
                }
                val intent = Intent(this@QuizActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            dialogMessage.create().show()
        }
        questionNumber++

    }
}