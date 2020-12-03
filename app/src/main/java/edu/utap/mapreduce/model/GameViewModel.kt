package edu.utap.mapreduce.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    /*
        Everything here should be irrelevant to the view

        Hence stuff like the mapping of the view id and the room index should be
        maintained in the activity instead
     */
    private var player = MutableLiveData<Player>()

    private var stage = MutableLiveData<Stage>()

    init {
        val initialStage = Stage(1)
        val initialPlayer = Player(100, 50, 50, 50)
        initialPlayer.initPosition(initialStage)

        player.value = initialPlayer
        stage.value = initialStage
    }

    fun observePlayer(): LiveData<Player> = player

    fun observeStage(): LiveData<Stage> = stage

    fun setPlayer(newPlayer: Player) {
        player.value = newPlayer
    }

    fun setStage(newStage: Stage) {
        stage.value = newStage
    }
}
