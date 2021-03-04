/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    data class Question(
            val text: String,
            val answers: List<String>)

    // The first answer is the correct one.  We randomize the answers before showing the text.
    // All questions must have four answers.  We'd want these to contain references to string
    // resources so we could internationalize. (or better yet, not define the questions in code...)


    lateinit var currentQuestion: Question
    lateinit var answers: MutableList<String>
    private var questionIndex = 0
    private var answerIndex = 0
    private var numQuestions = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val questions: MutableList<Question> = mutableListOf(
                Question(text = getString(R.string.question_1),
                        answers = listOf("all of these", "tools", "documentation", "libraries")),
                Question(text = getString(R.string.question_1),
                        answers = listOf("ViewGroup", "ViewSet", "ViewCollection", "ViewRoot")),
                Question(text = getString(R.string.question_1),
                        answers = listOf("ConstraintLayout", "GridLayout", "LinearLayout", "FrameLayout")),
                Question(text = getString(R.string.question_1),
                        answers = listOf("Data Binding", "Data Pushing", "Set Text", "OnClick")),
                Question(text = getString(R.string.question_1),
                        answers = listOf("onCreateView", "onViewCreated", "onCreateLayout", "onInflateLayout")),
                Question(text = getString(R.string.question_1),
                        answers = listOf("Gradle", "Graddle", "Grodle", "Groyle")),
                Question(text = getString(R.string.question_1),
                        answers = listOf("VectorDrawable", "AndroidVectorDrawable", "DrawableVector", "AndroidVector")),
                Question(text = getString(R.string.question_1),
                        answers = listOf("NavController", "NavCentral", "NavMaster", "NavSwitcher")),
                Question(text = getString(R.string.question_1),
                        answers = listOf("intent-filter", "app-registry", "launcher-registry", "app-launcher")),
                Question(text = getString(R.string.question_1),
                        answers = listOf("<layout>", "<binding>", "<data-binding>", "<dbinding>"))
        )

        val numQuestions = Math.min((questions.size + 1) / 2, 3)

        val binding = DataBindingUtil.inflate<FragmentGameBinding>(
                inflater, R.layout.fragment_game, container, false)

        // Shuffles the questions and sets the question index to the first question.
        randomizeQuestions(questions)

        binding.game = this

        binding.submitButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            val checkedId = binding.questionRadioGroup.checkedRadioButtonId
            if (-1 != checkedId) {
                setTheAnswerIndex(checkedId)
                // The first answer in the original question is always the correct one, so if our
                // answer matches, we have the correct answer.
                if (answers[answerIndex] == currentQuestion.answers[0]) {
                    Toast.makeText(context, getString(R.string.correct_answer_message), Toast.LENGTH_LONG).show()
                    questionIndex++
                    if (questionIndex < numQuestions) {
                        currentQuestion = questions[questionIndex]
                        setQuestion(questions)
                        binding.invalidateAll()
                    } else {
                        navigateToGameWon()
                    }
                } else {
                    Toast.makeText(context, getString(R.string.wrong_answer_message), Toast.LENGTH_SHORT).show()
                    navigateToGameOver()
                }
            }
        }
        return binding.root
    }

    private fun setTheAnswerIndex(checkedId: Int) {
        when (checkedId) {
            R.id.secondAnswerRadioButton -> answerIndex = 1
            R.id.thirdAnswerRadioButton -> answerIndex = 2
            R.id.fourthAnswerRadioButton -> answerIndex = 3
        }
    }

    // randomize the questions and set the first question
    private fun randomizeQuestions(questions: MutableList<Question>) {
        questions.shuffle()
        questionIndex = 0
        setQuestion(questions)
    }

    // Sets the question and randomizes the answers.  This only changes the data, not the UI.
    // Calling invalidateAll on the FragmentGameBinding updates the data.
    private fun setQuestion(questions: MutableList<Question>) {
        currentQuestion = questions[questionIndex]
        answers = currentQuestion.answers.toMutableList()
        answers.shuffle()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_android_trivia_question, questionIndex + 1, numQuestions)
    }

    private fun navigateToGameWon() {
        view?.findNavController()?.navigate(GameFragmentDirections.actionGameFragmentToGameWonFragment(numQuestions, questionIndex))
    }

    private fun navigateToGameOver() {
        view?.findNavController()?.navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment())
    }
}
