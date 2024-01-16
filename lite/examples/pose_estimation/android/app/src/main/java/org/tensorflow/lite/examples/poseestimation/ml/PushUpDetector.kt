package org.tensorflow.lite.examples.poseestimation.ml

import android.graphics.PointF
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import kotlin.math.atan2
import kotlin.math.abs

class PushUpDetector {
    fun isCorrectPushUp(person: Person): Boolean {
        val keypoints = person.keyPoints
        val leftShoulder = keypoints.firstOrNull { it.bodyPart == BodyPart.LEFT_SHOULDER }
        val rightShoulder = keypoints.firstOrNull { it.bodyPart == BodyPart.RIGHT_SHOULDER }
        val leftElbow = keypoints.firstOrNull { it.bodyPart == BodyPart.LEFT_ELBOW }
        val rightElbow = keypoints.firstOrNull { it.bodyPart == BodyPart.RIGHT_ELBOW }
        val leftWrist = keypoints.firstOrNull { it.bodyPart == BodyPart.LEFT_WRIST }
        val rightWrist = keypoints.firstOrNull { it.bodyPart == BodyPart.RIGHT_WRIST }

        if (leftShoulder == null || rightShoulder == null ||
            leftElbow == null || rightElbow == null ||
            leftWrist == null || rightWrist == null
        ) {
            return false
        }

        val leftElbowAngle = calculateAngle(leftShoulder.coordinate, leftElbow.coordinate, leftWrist.coordinate)
        val rightElbowAngle = calculateAngle(rightShoulder.coordinate, rightElbow.coordinate, rightWrist.coordinate)
        println(rightElbowAngle)
        return rightElbowAngle in PUSH_UP_ELBOW_ANGLE_RANGE
    }

    private fun calculateAngle(firstPoint: PointF, midPoint: PointF, lastPoint: PointF): Double {
        val radians = atan2(lastPoint.y - midPoint.y, lastPoint.x - midPoint.x) -
                atan2(firstPoint.y - midPoint.y, firstPoint.x - midPoint.x)
        var angle = Math.toDegrees(radians.toDouble()).mod(180.0)
        return angle
    }


    companion object {
        private val PUSH_UP_ELBOW_ANGLE_RANGE = 70.0..110.0
    }

}