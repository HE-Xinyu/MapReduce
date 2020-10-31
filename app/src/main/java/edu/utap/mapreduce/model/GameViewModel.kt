package edu.utap.mapreduce.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private var player = MutableLiveData<Player>()

    private var stage = MutableLiveData<Stage>()

    init {
        // TODO: player should spawn at a random room
        val curPlayer = Player(99, 99, 99, 99)
        curPlayer.roomId = 0
        player.value = curPlayer

        stage.value = Stage(0)
    }

    fun observePlayer(): LiveData<Player> = player

    fun observeStage(): LiveData<Stage> = stage
}
