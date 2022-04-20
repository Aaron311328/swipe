package me.saket.swipe

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberSwipeableActionsState(): SwipeableActionsState {
  return remember { SwipeableActionsState() }
}

/**
 * The state of a [SwipeableActionsBox].
 */
@Stable
class SwipeableActionsState internal constructor() {
  /**
   * The current position (in pixels) of a [SwipeableActionsBox].
   */
  val offset: State<Float> get() = offsetState
  internal var offsetState = mutableStateOf(0f)

  /**
   * Whether [SwipeableActionsBox] is currently animating to reset its offset after it was swiped.
   */
  var isResettingOnRelease: Boolean by mutableStateOf(false)
    private set

  internal lateinit var canSwipeTowardsRight: () -> Boolean
  internal lateinit var canSwipeTowardsLeft: () -> Boolean

  internal val draggableState = DraggableState { delta ->
    val targetOffset = offsetState.value + delta
    val isAllowed = isResettingOnRelease
      || targetOffset > 0f && canSwipeTowardsRight()
      || targetOffset < 0f && canSwipeTowardsLeft()

    // Add some resistance if needed.
    offsetState.value += if (isAllowed) delta else delta / 10
  }

  @SuppressLint("LongLogTag")
  internal suspend fun resetOffset() {
    draggableState.drag(MutatePriority.PreventUserInput) {
      isResettingOnRelease = true
      try {
        Animatable(offsetState.value).animateTo(targetValue = 0f, tween(durationMillis = animationDurationMs)) {
          Log.d("rememberSwipeableActionsState","value:$value, offsetState.value:${offsetState.value}")
          dragBy(value - offsetState.value)
          Log.d("rememberSwipeableActionsState","value - offsetState.value:${value - offsetState.value}")
        }
      } finally {
        isResettingOnRelease = false
      }
    }
  }

  @SuppressLint("LongLogTag")
  internal suspend fun moveOffset() {
    draggableState.drag(MutatePriority.PreventUserInput) {
      isResettingOnRelease = true
      try {
        Animatable(offsetState.value).animateTo(targetValue = -150f, tween(durationMillis = animationDurationMs)) {
          Log.d("rememberSwipeableActionsState","value:$value, offsetState.value:${offsetState.value}")
          dragBy(value - offsetState.value - 150f)
        }
      } finally {
        isResettingOnRelease = false
      }
    }
  }
}
