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
        // TODO: player should spawn at a random room
        val curPlayer = Player(99, 99, 99, 99)
        curPlayer.roomIdx = 0
        player.value = curPlayer

        stage.value = Stage(1)
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
