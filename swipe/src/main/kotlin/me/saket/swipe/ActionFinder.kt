package me.saket.swipe

import kotlin.math.abs

internal data class SwipeActionMeta(
  val value: SwipeAction,
  val isOnRightSide: Boolean,
)

internal data class ActionFinder(
  private val left: List<SwipeAction>,
  private val right: List<SwipeAction>
) {

  fun actionAt(offset: Float, totalWidth: Int): SwipeActionMeta? {
    if (offset == 0f) {
      return null
    }

    val isOnRightSide = offset < 0f
    val actions = if (isOnRightSide) right else left

    return SwipeActionMeta(
      value = actions.actionAt(abs(offset), totalWidth) ?: return null,
      isOnRightSide = isOnRightSide
    )
  }

  private fun List<SwipeAction>.actionAt(offset: Float, totalWidth: Int): SwipeAction? {
    if (isEmpty()) {
      return null
    }

    val totalWeights = this.sumOf { it.weight }
    var offsetSoFar = 0.0

    for (i in 0 until size) {
      val action = this[i]
      val actionWidth = (action.weight / totalWeights) * totalWidth
      val actionEndX = offsetSoFar + actionWidth

      if (offset <= actionEndX) {
        return action
      }
      offsetSoFar += actionEndX
    }

    // Precision error in the above loop maybe?
    error("Couldn't find any swipe action. Width=$totalWidth, offset=$offset, actions=$this")
  }
}
